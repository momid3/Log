package com.momid.log

import com.momid.compiler.*
import com.momid.log.infoAccess
import com.momid.parser.expression.*
import com.momid.parser.not

val atom =
    className["atom"]

val operator =
    anyOf(!"+", !"-", !"*", !"/")["operator"]

val expression by lazy {
    some(
        spaces + anyOf(
            atom["atom"],
            operator["operator"],
            number["number"],
            stringLiteral["stringLiteral"],
            infoAccess["infoAccess"]
        )["atomicExp"] + spaces
    )
}

fun ExpressionResultsHandlerContext.handleExpression(generation: Generation): Result<Eval> {
    with(this.expressionResult) {
        var output = ""
        var type: Type
        this.forEach {
            with(it["atomicExp"]) {
                this.content.isOf(atom) {
                    println("atom")
                    val atom = it.tokens
                    return Ok(Eval(atom, AtomType(Atom(atom))))
                }

                this.content.isOf(operator) {
                    println("operator")
                    val operator = it.tokens
                    output += " " + operator + " "
                }

                this.content.isOf(number) {
                    println("number")
                    val number = it.tokens
                    output += " " + number + " "
                    type = NumberType(number.toInt())
                }

                this.content.isOf(infoAccess) {
                    println("info access")
                    val eval = continueStraight(it) { handleInfoAccess(generation) }.okOrReport {
                        return it.to()
                    }
                    output += " " + eval.output + " "
                    type = eval.type
                }
            }
        }

        return Ok(Eval(output, BooleanType(BooleanO(true))))
    }
}

fun main() {
    val generation = Generation()
    finder("someAtom", expression) { handleExpression(generation) }
}
