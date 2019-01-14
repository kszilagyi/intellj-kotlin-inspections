package com.kristofszilagyi.intellij.kotlin.inspections

import com.intellij.codeInspection.InspectionToolProvider


class ComparingReferencesProvider : InspectionToolProvider {
    override fun getInspectionClasses(): Array<Class<*>> = arrayOf(ComparingReferencesInspection::class.java)
}