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
import org.jetbrains.kotlin.resolve.descriptorUtil.classId
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.isFlexible
import org.jetbrains.kotlinx.serialization.compiler.resolve.toClassDescriptor


class MoreStrictNullSafetyInspection : AbstractKotlinInspection() {

    override fun getShortName(): String = "MoreStrictNullSafety"

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : KtVisitorVoid() {
            override fun visitNamedFunction(function: KtNamedFunction) {
                super.visitNamedFunction(function)
                function.bodyExpression?.let { body ->
                    val context = function.analyze(BodyResolveMode.PARTIAL)
                    val functionReturnType = context.get(BindingContext.TYPE, function.typeReference)
                    val bodyReturnType = body.resolveType()
                    if (functionReturnType != null) {
                        if(bodyReturnType.unwrap().isFlexible() && !functionReturnType.isFlexible() && !functionReturnType.isMarkedNullable) {
                            registerProblem(holder, body)
                        }
                    }
                }
            }

            override fun visitIfExpression(expression: KtIfExpression) {
                super.visitIfExpression(expression)
                expression.analyze(BodyResolveMode.FULL) // without this the type of then and else is intermittently Unit (in the test)
                val type = expression.resolveType()

                if (!type.isFlexible() && !type.isMarkedNullable) {
                    val thenBlock = expression.then
                    val elseBlock = expression.`else`
                    if (thenBlock != null && thenBlock.resolveType().isFlexible()) {
                        registerProblem(holder, thenBlock)
                    }

                    if (elseBlock != null && elseBlock.resolveType().isFlexible()) {
                        registerProblem(holder, elseBlock)
                    }
                }
            }


            override fun visitWhenExpression(expression: KtWhenExpression) {
                super.visitWhenExpression(expression)

                val type = expression.resolveType()

                if (!type.isFlexible() && !type.isMarkedNullable) {
                    val whens = expression.entries
                    whens.forEach {
                        val entryExpression = it.expression
                        if (entryExpression != null && entryExpression.resolveType().isFlexible()) {
                            registerProblem(holder, entryExpression)
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
                if (!type.isFlexible() && !type.isMarkedNullable && !isStringConcat(expression)) {
                    val leftBlock = expression.left
                    val rightBlock = expression.right
                    val calledFunction = (expression.operationReference.reference?.resolve() as? KtCallableDeclaration)

                    val operatorContext = calledFunction?.analyze(BodyResolveMode.FULL)
                    if (leftBlock != null && leftBlock.resolveType().isFlexible()
                            && expression.operationReference.operationSignTokenType != ELVIS) {
                        val receiverType = operatorContext?.get(BindingContext.TYPE, calledFunction.receiverTypeReference)
                        if (receiverType == null || !receiverType.isMarkedNullable) {
                            registerProblem(holder, leftBlock)
                        }
                    }

                    if (rightBlock != null && rightBlock.resolveType().isFlexible()) {
                        val parameterType = operatorContext?.get(BindingContext.TYPE,
                            calledFunction.valueParameterList?.parameters?.firstOrNull()?.typeReference)
                        if (parameterType == null || !parameterType.isMarkedNullable) {
                            registerProblem(holder, rightBlock)
                        }
                    }
                }
            }

            override fun visitReturnExpression(expression: KtReturnExpression) {
                super.visitReturnExpression(expression)
                val typeInReturn = expression.returnedExpression?.resolveType()
                val context = expression.analyze(BodyResolveMode.PARTIAL)
                val functionReturnType = context.
                    get(BindingContext.TYPE, expression.parentOfType(KtCallableDeclaration::class)?.typeReference)

                if (typeInReturn != null && functionReturnType != null) {
                    if (typeInReturn.unwrap().isFlexible() && !functionReturnType.isFlexible() && !functionReturnType.isMarkedNullable) {
                        registerProblem(holder, expression)
                    }
                }
            }
        }
    }

    companion object {
        fun registerProblem(holder: ProblemsHolder, expression: KtExpression) {
            holder.registerProblem(expression,
                "You are implicitly converting a platform type into a non-nullable type. This code might throw.",
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
        }
    }
}