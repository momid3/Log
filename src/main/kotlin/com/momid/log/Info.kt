package com.momid.log

import com.momid.compiler.*
import com.momid.log.output.Info
import com.momid.log.output.sameInfo
import com.momid.parser.expression.*
import com.momid.parser.not

val info =
    className["infoName"] + insideOf('(', ')') {
        splitByNW(complexExpression["infoParameter"], ",")["infoParameters"]
    }["infoParameters"] + spaces + "=" + spaces + complexExpression["infoValue"] + spaces + !";"

fun ExpressionResultsHandlerContext.handleInfo(generation: Generation, includeInInfos: Boolean = true): Result<Info> {
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
        if (includeInInfos) {
            generation.infos.addOrReplace(info).also {
                if (it) {
                    println("info added")
                } else {
                    println("info updated")
                }
            }
        }
        return Ok(info)
    }
}

val Info.text: String
    get() {
        return this.name + "(" + this.parameters.joinToString(", ") {
            it.output
        } + ")" + " = " + this.value.output
    }

fun ArrayList<Info>.addOrReplace(info: Info): Boolean {
    this.forEachIndexed { index, thisInfo ->
        if (sameInfo(thisInfo, info)) {
            this[index] = info
            return false
        }
    }
    this.add(info)
    return true
}
