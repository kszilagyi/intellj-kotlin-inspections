package com.kristofszilagyi.intellij.kotlin.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.parentOfType
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.isFlexible


class ReturnPlatformTypeInspection : AbstractKotlinInspection() {

    override fun getShortName(): String = "ReturnPlatformType"

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : KtVisitorVoid() {

            override fun visitReturnExpression(expression: KtReturnExpression) {
                super.visitReturnExpression(expression)
                val typeInReturn = expression.returnedExpression?.resolveType()
                val context = expression.analyze(BodyResolveMode.PARTIAL);
                val functionReturnType = context.
                    get(BindingContext.TYPE, expression.parentOfType(KtCallableDeclaration::class)?.typeReference)

                if (typeInReturn != null && functionReturnType != null) {
                    if (typeInReturn.unwrap().isFlexible() && !functionReturnType.isFlexible() && !functionReturnType.isMarkedNullable) {
                        holder.registerProblem(expression,
                            "You are implicitly converting a platform type into a non-nullable type. This code might throw.",
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                    }
                }
            }


        }
    }
}