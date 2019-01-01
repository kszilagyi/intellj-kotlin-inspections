package com.kristofszilagyi.intellj.kotlin.inspections


import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.ComparingReferencesInspection
import com.intellij.testFramework.UsefulTestCase
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder
import com.intellij.testFramework.fixtures.*
import junit.framework.Assert


class TestThisPlugin : UsefulTestCase() {

    private var myFixture: CodeInsightTestFixture? = null
    // Specify path to your test data directory
    // e.g.  final String dataPath = "c:\\users\\john.doe\\idea\\community\\samples\\ComparingReferences/testData";
    internal val dataPath = "c:\\users\\John.Doe\\idea\\community\\samples\\comparingReferences/testData"


    @Throws(Exception::class)
    override fun setUp() {

        val fixtureFactory = IdeaTestFixtureFactory.getFixtureFactory()
        val testFixtureBuilder = fixtureFactory.createFixtureBuilder(getName())
        myFixture = JavaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(testFixtureBuilder.getFixture())
        myFixture!!.setTestDataPath(dataPath)
        val builder = testFixtureBuilder.addModule(JavaModuleFixtureBuilder<*>::class.java)

        builder.addContentRoot(myFixture!!.getTempDirPath()).addSourceRoot("")
        builder.setMockJdkLevel(JavaModuleFixtureBuilder.MockJdkLevel.jdk15)
        myFixture!!.setUp()
    }

    @Throws(Exception::class)
    override fun tearDown() {
        myFixture!!.tearDown()
        myFixture = null
    }

    @Throws(Throwable::class)
    private fun doTest(testName: String, hint: String) {
        myFixture!!.configureByFile("$testName.java")
        myFixture!!.enableInspections(ComparingReferencesInspection::class.java!!)
        val highlightInfos = myFixture!!.doHighlighting()
        Assert.assertTrue(!highlightInfos.isEmpty())

        val action = myFixture!!.findSingleIntention(hint)

        Assert.assertNotNull(action)
        myFixture!!.launchAction(action)
        myFixture!!.checkResultByFile("$testName.after.java")
    }

    // Test the "==" case
    @Throws(Throwable::class)
    fun test() {
        doTest("before", "Use equals()")
    }

    // Test the "!=" case
    @Throws(Throwable::class)
    fun test1() {
        doTest("before1", "Use equals()")
    }

}