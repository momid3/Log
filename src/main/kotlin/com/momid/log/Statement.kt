package com.momid.log

import com.momid.compiler.okOrReport
import com.momid.parser.expression.*

val statements =
    some(spaces + anyOf(info, print, rule)["statement"] + spaces)

fun ExpressionResultsHandlerContext.handleStatements(generation: Generation): Result<Boolean> {
    with(this.expressionResult) {
        this.forEach {
            with(it["statement"].content) {
                this.isOf(info) {
                    continueStraight(it) { handleInfo(generation) }.okOrReport {
                        return it.to()
                    }
//                    println(execute(eval.output))
                }

                this.isOf(print) {
                    continueStraight(it) { handlePrint(generation) }.okOrReport {
                        return it.to()
                    }
                }

                this.isOf(rule) {
                    continueStraight(it) { handleRuleDeclaration(generation) }.okOrReport {
                        return it.to()
                    }
                }
            }
        }
        return Ok(true)
    }
}
