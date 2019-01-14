package com.kristofszilagyi.intellij.kotlin.inspections

import com.intellij.codeInsight.daemon.GroupNames
import com.intellij.codeInspection.InspectionsBundle
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import org.jetbrains.annotations.NonNls
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import java.util.*


class ComparingReferencesInspection : AbstractKotlinInspection() {

    @NonNls
    private var checkedClasses = "java.lang.String;java.util.Date"

    override fun getDisplayName(): String = "'==' or '!=' instead of 'equals()'"

    override fun getGroupDisplayName(): String = GroupNames.BUGS_GROUP_NAME

    override fun getShortName(): String = "ComparingReferences"

    private fun isCheckedType(type: PsiType?): Boolean {
        if (type !is PsiClassType) return false

        val tokenizer = StringTokenizer(checkedClasses, ";")
        while (tokenizer.hasMoreTokens()) {
            val className = tokenizer.nextToken()
            if (type.equalsToText(className)) return true
        }

        return false
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {

            override fun visitReferenceExpression(psiReferenceExpression: PsiReferenceExpression?) {}


            override fun visitBinaryExpression(expression: PsiBinaryExpression) {
                super.visitBinaryExpression(expression)
                val opSign = expression.operationTokenType
                if (opSign === JavaTokenType.EQEQ || opSign === JavaTokenType.NE) {
                    val lOperand = expression.lOperand
                    val rOperand = expression.rOperand
                    if (rOperand == null || isNullLiteral(
                            lOperand
                        ) || isNullLiteral(
                            rOperand
                        )
                    ) return

                    val lType = lOperand.type
                    val rType = rOperand.type

                    if (isCheckedType(lType) || isCheckedType(rType)) {
                        holder.registerProblem(
                            expression,
                            DESCRIPTION_TEMPLATE
                        )
                    }
                }
            }
        }
    }

    override fun isEnabledByDefault(): Boolean = true

    companion object {
        @NonNls
        private val DESCRIPTION_TEMPLATE =
            InspectionsBundle.message("inspection.comparing.references.problem.descriptor")

        private fun isNullLiteral(expr: PsiExpression?): Boolean {
            return expr is PsiLiteralExpression && "null" == expr.text
        }
    }
}