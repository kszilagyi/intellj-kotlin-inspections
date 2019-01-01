package com.kristofszilagyi.intellij.kotlin.inspections

import com.intellij.codeInsight.daemon.GroupNames
import com.intellij.codeInspection.*
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.ui.DocumentAdapter
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.*
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection

import javax.swing.*
import javax.swing.event.DocumentEvent
import java.awt.*
import java.util.StringTokenizer

/**
 * @author max
 */
class ComparingReferencesInspection : AbstractKotlinInspection() {

    private val myQuickFix = MyQuickFix()

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
                            DESCRIPTION_TEMPLATE, myQuickFix
                        )
                    }
                }
            }
        }
    }

    private class MyQuickFix : LocalQuickFix {
        override fun getName(): String {
            // The test (see the TestThisPlugin class) uses this string to identify the quick fix action.
            return InspectionsBundle.message("inspection.comparing.references.use.quickfix")
        }


        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            try {
                val binaryExpression = descriptor.psiElement as PsiBinaryExpression
                val opSign = binaryExpression.operationTokenType
                val lExpr = binaryExpression.lOperand
                val rExpr = binaryExpression.rOperand ?: return

                val factory = JavaPsiFacade.getInstance(project).elementFactory
                val equalsCall = factory.createExpressionFromText("a.equals(b)", null) as PsiMethodCallExpression

                equalsCall.methodExpression.qualifierExpression!!.replace(lExpr)
                equalsCall.argumentList.expressions[0].replace(rExpr)

                val result = binaryExpression.replace(equalsCall) as PsiExpression

                if (opSign === JavaTokenType.NE) {
                    val negation = factory.createExpressionFromText("!a", null) as PsiPrefixExpression
                    negation.operand!!.replace(result)
                    result.replace(negation)
                }
            } catch (e: IncorrectOperationException) {
                LOG.error(e)
            }

        }

        override fun getFamilyName(): String {
            return name
        }
    }

    override fun createOptionsPanel(): JComponent? {
        val panel = JPanel(FlowLayout(FlowLayout.LEFT))
        val checkedClasses = JTextField(checkedClasses)
        checkedClasses.document.addDocumentListener(object : DocumentAdapter() {
            public override fun textChanged(event: DocumentEvent) {
                this@ComparingReferencesInspection.checkedClasses = checkedClasses.text
            }
        })

        panel.add(checkedClasses)
        return panel
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }

    companion object {
        private val LOG = Logger.getInstance(this::class.java)
        @NonNls
        private val DESCRIPTION_TEMPLATE =
            InspectionsBundle.message("inspection.comparing.references.problem.descriptor")

        private fun isNullLiteral(expr: PsiExpression?): Boolean {
            return expr is PsiLiteralExpression && "null" == expr.text
        }
    }
}
