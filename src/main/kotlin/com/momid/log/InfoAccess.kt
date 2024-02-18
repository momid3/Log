package com.momid.log

import com.momid.compiler.*
import com.momid.parser.expression.*
import com.momid.parser.not

val infoAccess: MultiExpression by lazy {
    className["infoName"] + insideOf('(', ')') {
        splitBy(expression["infoParameter"], ",")
    }["infoParameters"]
}

fun ExpressionResultsHandlerContext.handleInfoAccess(generation: Generation): Result<Eval> {
    with(this.expressionResult) {
        val name = this["infoName"]
        val parameters = this["infoParameters"].continuing?.asMulti()?.map {
            val evaluation = continueWithOne(it, expression) { handleExpression(generation) }.okOrReport {
                return it.to()
            }
            evaluation
        }.orEmpty()

        val info = generation.infos.find {
            it.name == name.tokens && it.parameters.forEveryIndexed { index, parameter ->
                it.parameters[index] == parameter
            }
        }

        if (info != null) {
            return Ok(info.value)
        } else {
            return Ok(unknownTypeEval)
        }
    }
}

val unknownTypeEval = Eval("", UnknownType())
