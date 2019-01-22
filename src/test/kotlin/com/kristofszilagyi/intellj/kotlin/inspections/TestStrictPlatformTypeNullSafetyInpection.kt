package com.kristofszilagyi.intellj.kotlin.inspections

import com.intellij.testFramework.LightProjectDescriptor
import com.kristofszilagyi.intellij.kotlin.inspections.MoreStrictNullSafetyInspection
import org.jetbrains.kotlin.idea.test.KotlinLightCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.test.KotlinLightProjectDescriptor
import org.jetbrains.kotlin.idea.test.PluginTestCaseBase

class TestStrictPlatformTypeNullSafetyInpection : KotlinLightCodeInsightFixtureTestCase() {

    override fun getTestDataPath(): String = PluginTestCaseBase.getTestDataPathBase() + "/ImplicitPlatformTypeConversion"

    override fun getProjectDescriptor(): LightProjectDescriptor = KotlinLightProjectDescriptor.INSTANCE

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(MoreStrictNullSafetyInspection())
    }


    fun testConvertPlatformTypeOnReturn() {
        myFixture.configureByFiles("ConvertPlatformTypeOnReturn.kt", "ValueExtension.kt", "MyClass.java", "Value.java")
        myFixture.testHighlighting( "ConvertPlatformTypeOnReturn.kt")
    }

    //other cases: overriding things which takes platform type, assigning to variable
}