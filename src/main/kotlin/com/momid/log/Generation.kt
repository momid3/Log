package com.momid.log

import com.momid.log.output.Info
import com.momid.log.output.Rule
import com.momid.log.output.Scope

class Generation() {
    private val rootScope = Scope()
    val atoms = ArrayList<Atom>()
    val infos = ArrayList<Info>()
    val rules = ArrayList<Rule>()
    var currentScope = rootScope

    fun createScope(): Scope {
        val scope = Scope()
        currentScope.scopes += scope
        scope.upperScope = currentScope
        currentScope = scope
        return scope
    }

    fun createScope(scope: Scope) {
        currentScope.scopes += scope
        scope.upperScope = currentScope
        currentScope = scope
    }

    fun getOutOfScope(): Boolean {
        if (currentScope != rootScope) {
            currentScope = currentScope.upperScope!!
            return true
        } else {
            return false
        }
    }
}
