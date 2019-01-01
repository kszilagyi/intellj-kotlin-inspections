package com.kristofszilagyi.intellij.kotlin.inspections

import com.intellij.codeInspection.InspectionToolProvider

/**
 * @author max
 */
class ComparingReferencesProvider : InspectionToolProvider {
    override fun getInspectionClasses(): Array<Class<*>> {
        return arrayOf(ComparingReferencesInspection::class.java)
    }
}
