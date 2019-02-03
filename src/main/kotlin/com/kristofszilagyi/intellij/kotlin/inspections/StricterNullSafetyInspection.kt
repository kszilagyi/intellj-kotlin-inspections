package com.kristofszilagyi.intellij.kotlin.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.*
import com.intellij.psi.util.parentOfType
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.idea.refactoring.renderTrimmed
import org.jetbrains.kotlin.lexer.KtTokens.ELVIS
import org.jetbrains.kotlin.lexer.KtTokens.PLUS
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.classId
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isFlexible
import org.jetbrains.kotlin.types.isNullabilityFlexible
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlinx.serialization.compiler.resolve.toClassDescriptor


class StricterNullSafetyInspection : AbstractKotlinInspection() {

    override fun getShortName(): String = "StricterNullSafety"

    override fun getStaticDescription(): String? {
        return "Using platform types as non-nullable or passing in nullable types (or platform types as we don't know their nullability)" +
                " to Java code is unsafe and might result in NullPointerException. You have to explicitly decide about these cases with ... and ... "
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : KtVisitorVoid() {

            private fun checkExpressionBodyReturnType(callable: KtCallableDeclaration, body: KtExpression) {
                val context = callable.analyze(BodyResolveMode.PARTIAL)
                val functionReturnType = context.get(BindingContext.TYPE, callable.typeReference)
                val bodyReturnType = body.safeResolveType()
                if (functionReturnType != null && bodyReturnType != null) {
                    if(bodyReturnType.unwrap().isNullabilityFlexible() && !functionReturnType.isFlexible() && !functionReturnType.isNullable()) {
                        registerProblemFromJava(holder, body)
                    }
                }
            }

            override fun visitNamedFunction(function: KtNamedFunction) {
                super.visitNamedFunction(function)
                function.bodyExpression?.let { body ->
                    checkExpressionBodyReturnType(function, body)
                }
            }

            override fun visitProperty(property: KtProperty) {
                super.visitProperty(property)
                property.initializer?.let { body ->
                    checkExpressionBodyReturnType(property, body)
                }
            }

            override fun visitCallExpression(expression: KtCallExpression) {
                super.visitCallExpression(expression)

                val ctx = expression.analyze(BodyResolveMode.FULL)
                val call = expression.getResolvedCall(ctx)
                call?.valueArguments?.forEach{(parameterDescriptor, argumentDescriptor) ->
                    val parameterType = parameterDescriptor.type
                    val argumentExpression = argumentDescriptor.arguments.firstOrNull()?.getArgumentExpression()
                    val argumentExpressionType = argumentExpression?.safeResolveType()

                    if (argumentExpressionType != null) {
                        if (parameterType.toClassDescriptor.classId?.asString()?.matches("""kotlin/Function\d+""".toRegex()) == true) {
                            val parameterLambdaReturnType = parameterType.arguments.lastOrNull()?.type
                            val argumentLambdaReturnType = argumentExpressionType.arguments.lastOrNull()?.type
                            if (parameterLambdaReturnType != null && argumentLambdaReturnType != null &&
                                !parameterLambdaReturnType.unwrap().isNullable() && argumentLambdaReturnType.isFlexible()
                            ) {
                                registerProblemFromJava(holder, argumentExpression)
                            } else if (parameterLambdaReturnType?.isFlexible() == true && argumentLambdaReturnType?.isNullable() == true) {
                                registerProblemToJava(holder, argumentExpression, argumentLambdaReturnType.isFlexible())
                            }
                        } else {
                            if (!parameterType.isFlexible() && !parameterType.isNullable() && argumentExpressionType.isNullabilityFlexible()) {
                                registerProblemFromJava(holder, argumentExpression)
                            } else if (parameterType.isFlexible() && argumentExpressionType.isNullable()) {
                                registerProblemToJava(holder, argumentExpression, argumentExpressionType.isFlexible())
                            }
                        }
                    }
                }
            }

            override fun visitIfExpression(expression: KtIfExpression) {
                super.visitIfExpression(expression)
                expression.analyze(BodyResolveMode.FULL) // without this the type of then and else is intermittently Unit (in the test)
                val type = expression.safeResolveType()

                if (type != null && !type.isFlexible() && !type.isNullable()) {
                    val thenBlock = expression.then
                    val elseBlock = expression.`else`
                    if (thenBlock != null && thenBlock.safeResolveType()?.isFlexible() == true) {
                        registerProblemFromJava(holder, thenBlock)
                    }

                    if (elseBlock != null && elseBlock.safeResolveType()?.isFlexible() == true) {
                        registerProblemFromJava(holder, elseBlock)
                    }
                }
            }


            override fun visitWhenExpression(expression: KtWhenExpression) {
                super.visitWhenExpression(expression)
                val type = expression.safeResolveType()
                if (type != null && !type.isFlexible() && !type.isNullable()) {
                    val whens: List<KtWhenEntry> = expression.entries
                    whens.forEach {
                        val entryExpression = it.expression
                        if (entryExpression != null && entryExpression.safeResolveType()?.isFlexible() == true) {
                            registerProblemFromJava(holder, entryExpression)
                        }
                    }
                }
            }

            private fun isStringConcat(expression: KtBinaryExpression): Boolean {
                if (expression.operationReference.operationSignTokenType == PLUS) {
                    val leftType = expression.left?.safeResolveType()
                    val rightType =  expression.right?.safeResolveType()
                    if (leftType?.unwrap()?.toClassDescriptor?.classId?.asString() == "kotlin/String" &&
                        rightType?.unwrap()?.toClassDescriptor?.classId?.asString() == "kotlin/String") {
                        return true
                    }
                }
                return false
            }

            override fun visitBinaryExpression(expression: KtBinaryExpression) {
                super.visitBinaryExpression(expression)

                val type = expression.safeResolveType()
                if (type != null && !type.isFlexible() && !type.isNullable() && !isStringConcat(expression)) {
                    val leftBlock = expression.left
                    val rightBlock = expression.right
                    val calledFunction = (expression.operationReference.reference?.resolve() as? KtCallableDeclaration)

                    val operatorContext = calledFunction?.analyze(BodyResolveMode.FULL)
                    if (leftBlock != null && leftBlock.safeResolveType()?.isFlexible() == true
                            && expression.operationReference.operationSignTokenType != ELVIS) {
                        val receiverType = operatorContext?.get(BindingContext.TYPE, calledFunction.receiverTypeReference)
                        if (receiverType == null || !receiverType.isNullable()) {
                            registerProblemFromJava(holder, leftBlock)
                        }
                    }

                    if (rightBlock != null && rightBlock.safeResolveType()?.isFlexible() == true) {
                        val parameterType = operatorContext?.get(BindingContext.TYPE,
                            calledFunction.valueParameterList?.parameters?.firstOrNull()?.typeReference)
                        if (parameterType == null || !parameterType.isNullable()) {
                            registerProblemFromJava(holder, rightBlock)
                        }
                    }
                }
            }

            override fun visitReturnExpression(expression: KtReturnExpression) {
                super.visitReturnExpression(expression)
                val ctx = expression.analyze(BodyResolveMode.PARTIAL)

                val functionReturnType = if (expression.labelQualifier != null) {
                    val label = expression.getTargetLabel()
                    val labelledTarget = ctx.get(BindingContext.LABEL_TARGET, label)
                    val function = labelledTarget as? KtFunction
                    val functionContext = function?.analyze(BodyResolveMode.PARTIAL)
                    functionContext?.get(BindingContext.TYPE, function.typeReference)
                } else {
                    val function: KtCallableDeclaration? =
                        expression.parentOfType(KtNamedFunction::class) ?: expression.parentOfType(KtProperty::class)
                    ctx.get(BindingContext.TYPE, function?.typeReference)
                }
                val typeInReturn = expression.returnedExpression?.safeResolveType()

                if (typeInReturn != null && functionReturnType != null) {
                    if (typeInReturn.unwrap().isFlexible() && !functionReturnType.isFlexible() && !functionReturnType.isNullable()) {
                        registerProblemFromJava(holder, expression)
                    }
                }
            }

            override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
                super.visitDotQualifiedExpression(expression)
                val ctx = expression.analyze(BodyResolveMode.PARTIAL)
                val receiverType = expression.receiverExpression.getType(ctx)
                val selectorExpression = expression.selectorExpression
                if(receiverType?.isFlexible() == true && selectorExpression != null) {
                    if (selectorExpression is KtCallExpression && selectorExpression.firstChild != null) {
                        registerProblemWithMessage(holder, selectorExpression.firstChild, "Unsafe call on platform type")
                    }
                }
            }
        }
    }

    companion object {
        private val logger = Logger.getInstance(this::class.java)

        private fun registerProblemFromJava(holder: ProblemsHolder, expression: PsiElement) {
            registerProblemWithMessage(holder, expression,
                "Implicit conversion of platform type to non-nullable")
        }

        private fun registerProblemWithMessage(holder: ProblemsHolder, expression: PsiElement, message: String) {
            holder.registerProblem(expression,
                message,
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
        }


        private fun registerProblemToJava(holder: ProblemsHolder, expression: PsiElement, platform: Boolean) {
            val typeString = if(platform) "platform type"
                             else "nullable"

            registerProblemWithMessage(holder, expression, "Passing $typeString to Java code")
        }

        private fun KtExpression.safeResolveType(): KotlinType? {
            val type = this.analyze(BodyResolveMode.PARTIAL).getType(this)
            if (type == null) {
                logger.info("Couldn't resolve type for ${this.renderTrimmed()}")
            }
            return type
        }
    }
}