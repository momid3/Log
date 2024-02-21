package com.momid.log

import com.momid.compiler.asMulti
import com.momid.compiler.okOrReport
import com.momid.compiler.splitBy
import com.momid.compiler.splitByNW
import com.momid.log.output.Rule
import com.momid.log.output.RuleDefinitionContext
import com.momid.log.output.Scope
import com.momid.parser.expression.*
import com.momid.parser.not

val rule =
    splitByNW((spaces + ruleInfo["ruleInfo"] + spaces)["ifs"], ",")["ifs"] + spaces + !"->" + spaces +
            splitByNW((spaces + ruleInfo["ruleInfo"] + spaces)["thens"], ",")["then"] + spaces + !";"

fun ExpressionResultsHandlerContext.handleRuleDeclaration(generation: Generation): Result<Boolean> {
    with(this.expressionResult) {
        val rule = Rule(listOf(), listOf(), arrayListOf())
        val ruleScope = Scope()
        ruleScope.context = RuleDefinitionContext(rule)
        generation.createScope(ruleScope)
        val ifs = this["ifs"].asMulti().map {
            val info = it["ruleInfo"]
            val ruleInfo = continueWithOne(info, ruleInfo) { handleRuleInfo(generation) }.okOrReport {
                return it.to()
            }
            ruleInfo
        }

        val thens = this["thens"].asMulti().map {
            val info = it["ruleInfo"]
            val ruleInfo = continueWithOne(info, ruleInfo) { handleRuleInfo(generation) }.okOrReport {
                return it.to()
            }
            ruleInfo
        }

        rule.ifs = ifs
        rule.thens = thens
        generation.rules.add(rule)
        generation.getOutOfScope()
        return Ok(true)
    }
}

fun main() {
    finder("areFriends(3, 7) = 3 ->", splitByNW(spaces + ruleInfo["ruleInfo"] + spaces, ",")) {
        println(this.expressionResult.tokens)
        Ok(true)
    }
}
