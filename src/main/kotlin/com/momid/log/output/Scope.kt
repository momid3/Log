package com.momid.log.output

class Scope(var upperScope: Scope? = null, var context: Context = Context()) {
    val scopes = ArrayList<Scope>()
}

open class Context()

class InfoAccessContext(): Context()

class InfoDeclarationContext(): Context()

class RuleDefinitionContext(val rule: Rule): Context()
