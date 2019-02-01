package com.kristofszilagyi.intellij.kotlin.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.parentOfType
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.lexer.KtTokens.ELVIS
import org.jetbrains.kotlin.lexer.KtTokens.PLUS
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.classId
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.isFlexible
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
                val bodyReturnType = body.resolveType()
                if (functionReturnType != null) {
                    if(bodyReturnType.unwrap().isFlexible() && !functionReturnType.isFlexible() && !functionReturnType.isNullable()) {
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

                    if (parameterType.toClassDescriptor.classId?.asString()?.matches("""kotlin/Function\d+""".toRegex()) == true) {
                        val parameterLambdaReturnType = parameterType.arguments.lastOrNull()?.type
                        val argumentLambdaReturnType = argumentExpression?.resolveType()?.arguments?.lastOrNull()?.type
                        if(parameterLambdaReturnType != null && argumentLambdaReturnType != null &&
                            !parameterLambdaReturnType.unwrap().isNullable() && argumentLambdaReturnType.isFlexible()) {
                            registerProblemFromJava(holder, argumentExpression)
                        }
                    } else {
                        if(!parameterType.isFlexible() && !parameterType.isNullable() && argumentExpression?.resolveType()?.isFlexible() == true) {
                            registerProblemFromJava(holder, argumentExpression)
                        }
                        else if (parameterType.isFlexible()) {
                            val argumentType = argumentExpression?.resolveType()
                            if (argumentType != null && argumentType.isNullable())
                            registerProblemToJava(holder, argumentExpression)
                        }
                    }
                }
            }

            override fun visitIfExpression(expression: KtIfExpression) {
                super.visitIfExpression(expression)
                expression.analyze(BodyResolveMode.FULL) // without this the type of then and else is intermittently Unit (in the test)
                val type = expression.resolveType()

                if (!type.isFlexible() && !type.isNullable()) {
                    val thenBlock = expression.then
                    val elseBlock = expression.`else`
                    if (thenBlock != null && thenBlock.resolveType().isFlexible()) {
                        registerProblemFromJava(holder, thenBlock)
                    }

                    if (elseBlock != null && elseBlock.resolveType().isFlexible()) {
                        registerProblemFromJava(holder, elseBlock)
                    }
                }
            }


            override fun visitWhenExpression(expression: KtWhenExpression) {
                super.visitWhenExpression(expression)

                val type = expression.resolveType()
                if (!type.isFlexible() && !type.isNullable()) {
                    val whens = expression.entries
                    whens.forEach {
                        val entryExpression = it.expression
                        if (entryExpression != null && entryExpression.resolveType().isFlexible()) {
                            registerProblemFromJava(holder, entryExpression)
                        }
                    }
                }
            }

            private fun isStringConcat(expression: KtBinaryExpression): Boolean {
                if (expression.operationReference.operationSignTokenType == PLUS) {
                    val leftType = expression.left?.resolveType()
                    val rightType =  expression.right?.resolveType()
                    if (leftType?.unwrap()?.toClassDescriptor?.classId?.asString() == "kotlin/String" &&
                        rightType?.unwrap()?.toClassDescriptor?.classId?.asString() == "kotlin/String") {
                        return true
                    }
                }
                return false
            }

            override fun visitBinaryExpression(expression: KtBinaryExpression) {
                super.visitBinaryExpression(expression)

                val type = expression.resolveType()
                if (!type.isFlexible() && !type.isNullable() && !isStringConcat(expression)) {
                    val leftBlock = expression.left
                    val rightBlock = expression.right
                    val calledFunction = (expression.operationReference.reference?.resolve() as? KtCallableDeclaration)

                    val operatorContext = calledFunction?.analyze(BodyResolveMode.FULL)
                    if (leftBlock != null && leftBlock.resolveType().isFlexible()
                            && expression.operationReference.operationSignTokenType != ELVIS) {
                        val receiverType = operatorContext?.get(BindingContext.TYPE, calledFunction.receiverTypeReference)
                        if (receiverType == null || !receiverType.isNullable()) {
                            registerProblemFromJava(holder, leftBlock)
                        }
                    }

                    if (rightBlock != null && rightBlock.resolveType().isFlexible()) {
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
                val typeInReturn = expression.returnedExpression?.resolveType()

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
                if(receiverType?.isFlexible() == true) {
                    registerProblemFromJava(holder, expression)
                }
            }

        }
    }

    companion object {
        fun registerProblemFromJava(holder: ProblemsHolder, expression: KtExpression) {
            holder.registerProblem(expression,
                "Implicit conversion of platform type to non-nullable",
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
        }

        fun registerProblemToJava(holder: ProblemsHolder, expression: KtExpression) {
            holder.registerProblem(expression,
                "Passing nullable to Java code",
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
        }
    }
}