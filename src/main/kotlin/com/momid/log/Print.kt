package com.momid.log

import com.momid.compiler.continuing
import com.momid.compiler.okOrReport
import com.momid.compiler.terminal.blue
import com.momid.parser.expression.*
import com.momid.parser.not

val print =
    !"print" + insideOf('(', ')') {
        complexExpression["insidePrint"]
    }["insidePrint"] + spaces + !";"

fun ExpressionResultsHandlerContext.handlePrint(generation: Generation): Result<Boolean> {
    with(this.expressionResult) {
        val expressionInsidePrint = this["insidePrint"].continuing ?:
        return Error("print needs to have parameters", this.range)
        val eval = continueWithOne(expressionInsidePrint, complexExpression) { handleExpression(generation) }.okOrReport {
            return it.to()
        }

        if (eval.type is NumberType) {
            println(blue("printing " + execute(eval.output)))
        } else {
            println(blue("printing " + eval.output))
        }

        return Ok(true)
    }
}
