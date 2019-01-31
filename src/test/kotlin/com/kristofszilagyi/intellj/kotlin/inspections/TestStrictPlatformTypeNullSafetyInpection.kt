package com.kristofszilagyi.intellj.kotlin.inspections

import com.intellij.testFramework.LightProjectDescriptor
import com.kristofszilagyi.intellij.kotlin.inspections.MoreStrictNullSafetyInspection
import org.jetbrains.kotlin.idea.test.KotlinLightCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.test.KotlinLightProjectDescriptor
import org.jetbrains.kotlin.idea.test.PluginTestCaseBase

class TestStrictPlatformTypeNullSafetyInpection : KotlinLightCodeInsightFixtureTestCase() {

    override fun getTestDataPath(): String = PluginTestCaseBase.getTestDataPathBase() + "/MoreStrictNullSafety"

    override fun getProjectDescriptor(): LightProjectDescriptor = KotlinLightProjectDescriptor.INSTANCE

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(MoreStrictNullSafetyInspection())
        myFixture.configureByFiles("ValueExtension.kt", "MyClass.java",
            "Value.java", "Generic.kt")
    }

    fun testReturnAndExpressions() {
        myFixture.testHighlighting( "tests/ReturnAndExpressions.kt")
    }

    fun testFunctionCalls() {
        myFixture.testHighlighting( "tests/FunctionCalls.kt")
    }

    fun testLambdas() {
        myFixture.testHighlighting( "tests/Lamdas.kt")
    }

}