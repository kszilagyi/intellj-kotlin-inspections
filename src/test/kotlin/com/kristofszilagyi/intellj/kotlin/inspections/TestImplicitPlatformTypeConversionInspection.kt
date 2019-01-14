package com.kristofszilagyi.intellj.kotlin.inspections

import com.intellij.testFramework.LightProjectDescriptor
import org.jetbrains.kotlin.idea.test.KotlinLightCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.test.KotlinLightProjectDescriptor
import org.jetbrains.kotlin.idea.test.PluginTestCaseBase

class TestImplicitPlatformTypeConversionInspection : KotlinLightCodeInsightFixtureTestCase() {

    override fun getTestDataPath(): String = PluginTestCaseBase.getTestDataPathBase()

    override fun getProjectDescriptor(): LightProjectDescriptor = KotlinLightProjectDescriptor.INSTANCE

    override fun setUp() {
        super.setUp()
//        myFixture.enableInspections(BlockingMethodInNonBlockingContextInspection::class.java)
    }


    fun testOne() {
        myFixture.configureByFile("testData/Test1.kt")
        myFixture.testHighlighting(true, false, false, "testData/Test1.kt")

    }

}