package com.kristofszilagyi.intellj.kotlin.inspections

import com.intellij.testFramework.LightProjectDescriptor
import org.jetbrains.kotlin.idea.test.KotlinLightCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.test.KotlinLightProjectDescriptor
import org.jetbrains.kotlin.idea.test.PluginTestCaseBase

class TestImplicitPlatformTypeConversionInspection : KotlinLightCodeInsightFixtureTestCase() {

    override fun getTestDataPath(): String = PluginTestCaseBase.getTestDataPathBase() + "/ImplicitPlatformTypeConversion"

    override fun getProjectDescriptor(): LightProjectDescriptor = KotlinLightProjectDescriptor.INSTANCE

    override fun setUp() {
        super.setUp()
//        myFixture.enableInspections(BlockingMethodInNonBlockingContextInspection::class.java)
    }


    fun testConvertPlatformTypeOnReturn() {
        myFixture.configureByFiles("ConvertPlatformTypeOnReturn.kt", "MyClass.java", "Value.java")
        myFixture.testHighlighting( "ConvertPlatformTypeOnReturn.kt")
    }

}