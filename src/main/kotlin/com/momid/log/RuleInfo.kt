package com.momid.log

import com.momid.compiler.*
import com.momid.log.output.Info
import com.momid.log.output.RuleInfo
import com.momid.parser.expression.*
import com.momid.parser.not

val ruleInfo =
    className["infoName"] + insideOf('(', ')') {
        splitByNW(complexExpression["infoParameter"], ",")["infoParameters"]
    }["infoParameters"] + spaces + "=" + spaces + complexExpression["infoValue"]

fun ExpressionResultsHandlerContext.handleRuleInfo(generation: Generation): Result<RuleInfo> {
    with(this.expressionResult) {
        val name = this["infoName"]
        val parameters = this["infoParameters"].continuing?.asMulti()?.map {
            val evaluation = continueWithOne(it, complexExpression) { handleExpressionEvaluation(generation) }.okOrReport {
                return it.to()
            }
            evaluation
        }.orEmpty()
        val value = continueWithOne(this["infoValue"], complexExpression) { handleExpressionEvaluation(generation) }.okOrReport {
            return it.to()
        }

        val ruleInfo = RuleInfo(name.tokens, parameters, value)
        println("ruleInfo: " + ruleInfo.text)
        return Ok(ruleInfo)
    }
}

val RuleInfo.text: String
    get() {
        return this.name + "(" + this.parameters.joinToString(", ") {
            it.output
        } + ")" + " = " + this.value.output
    }
