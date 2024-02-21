package com.momid.log

import com.momid.compiler.*
import com.momid.log.output.Info
import com.momid.parser.expression.*
import com.momid.parser.not

val info =
    className["infoName"] + insideOf('(', ')') {
        splitByNW(complexExpression["infoParameter"], ",")["infoParameters"]
    }["infoParameters"] + spaces + "=" + spaces + complexExpression["infoValue"] + spaces + !";"

fun ExpressionResultsHandlerContext.handleInfo(generation: Generation): Result<Info> {
    with(this.expressionResult) {
        val name = this["infoName"]
        val parameters = this["infoParameters"].continuing?.asMulti()?.map {
            val evaluation = continueWithOne(it, complexExpression) { handleExpression(generation) }.okOrReport {
                return it.to()
            }
            evaluation
        }.orEmpty()
        val value = continueWithOne(this["infoValue"], complexExpression) { handleExpression(generation) }.okOrReport {
            return it.to()
        }

        val info = Info(name.tokens, parameters, value)
        println("info declaration: " + info.text)
        generation.infos.add(info)
        return Ok(info)
    }
}

val Info.text: String
    get() {
        return this.name + "(" + this.parameters.joinToString(", ") {
            it.output
        } + ")" + " = " + this.value.output
    }
