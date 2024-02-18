package com.momid.log

import com.momid.compiler.*
import com.momid.log.output.Info
import com.momid.parser.expression.*
import com.momid.parser.not

val info =
    className["infoName"] + insideOf('(', ')') {
        splitBy(expression["infoParameter"], ",")
    }["infoParameters"] + spaces + "=" + spaces + expression["infoValue"] + spaces + !";"

fun ExpressionResultsHandlerContext.handleInfo(generation: Generation): Result<Info> {
    with(this.expressionResult) {
        val name = this["infoName"]
        val parameters = this["infoParameters"].continuing?.asMulti()?.map {
            val evaluation = continueWithOne(it, expression) { handleExpression(generation) }.okOrReport {
                return it.to()
            }
            evaluation
        }.orEmpty()
        val value = continueWithOne(this["infoValue"], expression) { handleExpression(generation) }.okOrReport {
            return it.to()
        }

        val info = Info(name.tokens, parameters, value)
        generation.infos.add(info)
        return Ok(info)
    }
}
