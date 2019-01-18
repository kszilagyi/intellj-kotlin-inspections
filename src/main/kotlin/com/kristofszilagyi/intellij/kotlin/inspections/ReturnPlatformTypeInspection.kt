package com.kristofszilagyi.intellij.kotlin.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.parentOfType
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.isFlexible


class ReturnPlatformTypeInspection : AbstractKotlinInspection() {

    override fun getShortName(): String = "ReturnPlatformType"

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