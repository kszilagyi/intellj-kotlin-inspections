package com.kristofszilagyi.intellj.kotlin.inspections


import com.intellij.testFramework.UsefulTestCase
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.intellij.testFramework.fixtures.JavaTestFixtureFactory
import com.kristofszilagyi.intellij.kotlin.inspections.ComparingReferencesInspection
import junit.framework.Assert


class TestThisPlugin : UsefulTestCase() {

    private var myFixture: CodeInsightTestFixture? = null
    // Specify path to your test data directory
    // e.g.  final String dataPath = "c:\\users\\john.doe\\idea\\community\\samples\\ComparingReferences/testData";
    private val dataPath = "c:\\users\\John.Doe\\idea\\community\\samples\\comparingReferences/testData"

    override fun setUp() {

        val fixtureFactory = IdeaTestFixtureFactory.getFixtureFactory()
        val testFixtureBuilder = fixtureFactory.createFixtureBuilder(name)
        myFixture = JavaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(testFixtureBuilder.fixture)
        myFixture!!.testDataPath = dataPath
        val builder = testFixtureBuilder.addModule(JavaModuleFixtureBuilder::class.java)

        builder.addContentRoot(myFixture!!.tempDirPath).addSourceRoot("")
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
        myFixture!!.enableInspections(ComparingReferencesInspection::class.java)
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