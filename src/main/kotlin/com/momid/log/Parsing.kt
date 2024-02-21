package com.momid.log

import com.momid.compiler.*
import com.momid.compiler.terminal.yellow
import com.momid.log.output.RuleDefinitionContext
import com.momid.log.output.Unknown
import com.momid.parser.expression.*
import com.momid.parser.not

val atomName =
    condition { it.isLetter() && it.isLowerCase() } + some0(condition { it.isLetterOrDigit() })

val unknownName =
    condition { it.isLetter() && it.isUpperCase() } + some0(condition { it.isLetterOrDigit() })

val unknown =
    unknownName["unknown"] + not(!"(")

val atom =
    atomName["atom"] + not(!"(")

val plus =
    !"+"

val minus =
    !"-" + not(!">")

val operator =
    anyOf(plus, minus, !"*", !"/")["operator"]

val booleanLiteral =
    anyOf(trueValue, falseValue)

val complexExpression by lazy {
    some(
        spaces + anyOf(
            booleanLiteral["booleanLiteral"],
            atom["atom"],
            operator["operator"],
            unknown["unknown"],
            number["number"],
            stringLiteral["stringLiteral"],
            infoAccess["infoAccess"]
        )["atomicExp"] + spaces
    )
}

fun ExpressionResultsHandlerContext.handleExpression(generation: Generation): Result<Eval> {
    with(this.expressionResult) {
        var output = ""
        var type: Type? = null
        this.forEach {
            with(it["atomicExp"].content) {
                this.isOf(atom) {
                    println("atom " + it.tokens)
                    val atom = it.tokens
                    return Ok(Eval(atom, AtomType(Atom(atom))))
                }

                this.isOf(operator) {
                    println("operator " + it.tokens)
                    val operator = it.tokens
                    output += operator + " "
                }

                this.isOf(number) {
                    println("number " + it.tokens)
                    val number = it.tokens
                    output += number + " "
                    type = NumberType(number.toInt())
                }

                this.isOf(infoAccess) {
                    println("info access " + it.tokens)
                    val eval = continueStraight(it) { handleInfoAccess(generation) }.okOrReport {
                        return it.to()
                    }
                    output += eval.output + " "
                    type = eval.type
                }

                this.isOf(booleanLiteral) {
                    println("boolean literal " + it.tokens)
                    it.content.isOf(trueValue) {
                        val trueValue = it.tokens
                        output += trueValue + " "
                        type = BooleanType(BooleanO(true))
                    }

                    it.content.isOf(falseValue) {
                        val trueValue = it.tokens
                        output += trueValue + " "
                        type = BooleanType(BooleanO(false))
                    }
                }
            }
        }

        if (type == null) {
            println("could not determine this expression type")
            return Error("could not determine this expression type", this.range)
        } else {
            if (type is NumberType) {
                output = execute(output).toString()
            }
            return Ok(Eval(output, type!!))
        }
    }
}

fun ExpressionResultsHandlerContext.handleExpressionEvaluation(generation: Generation): Result<Evaluation> {
    with(this.expressionResult) {
        var output = ""
        var type: Type? = null
        val evaluation = Evaluation(this.tokens, UnknownType())
        this.forEach {
            with(it["atomicExp"].content) {
                this.isOf(atom) {
                    println("atom " + it.tokens)
                    val atomTokens = it.tokens
                    val atom = Atom(atomTokens)
                    evaluation += Evaluation(atomTokens, AtomType(atom))
                    return Ok(evaluation)
                }

                this.isOf(operator) {
                    println("operator " + it.tokens)
                    val operatorTokens = it.tokens
                    val operator = OperatorType(Operator.Operator)
                    evaluation += Evaluation(operatorTokens, operator)
                    output += operatorTokens + " "
                }

                this.isOf(number) {
                    println("number " + it.tokens)
                    val numberTokens = it.tokens
                    output += numberTokens + " "
                    type = NumberType(numberTokens.toInt())
                    evaluation += Evaluation(numberTokens, type!!)
                }

                this.isOf(infoAccess) {
                    println("info access " + it.tokens)
                    val eval = continueStraight(it) { handleInfoAccessEvaluation(generation) }.okOrReport {
                        return it.to()
                    }
                    output += it.tokens + " "
                    type = InfoAccessType(eval)
                    evaluation += Evaluation(it.tokens, type!!)
                }

                this.isOf(booleanLiteral) {
                    println("boolean literal " + it.tokens)
                    it.content.isOf(trueValue) {
                        val trueValue = it.tokens
                        output += trueValue + " "
                        type = BooleanType(BooleanO(true))
                        evaluation += Evaluation(trueValue, type!!)
                    }

                    it.content.isOf(falseValue) {
                        val trueValue = it.tokens
                        output += trueValue + " "
                        type = BooleanType(BooleanO(false))
                        evaluation += Evaluation(trueValue, type!!)
                    }
                }

                this.isOf(unknown) {
                    val unknownTokens = it.tokens
                    val unknown = Unknown(unknownTokens)
                    output += unknownTokens + " "
                    if (type == null) {
                        type = RuleUnknownType(unknown)
                    }

                    evaluation += Evaluation(unknownTokens, RuleUnknownType(unknown))

                    if (generation.currentScope.context is RuleDefinitionContext) {
                        val ruleDefinitionContext = (generation.currentScope.context as RuleDefinitionContext)
                        if (!ruleDefinitionContext.rule.unknowns.contains(unknown)) {
                            println("unknown " + yellow("[new] ") + yellow(unknownTokens))
                        } else {
                            println("unknown " + yellow(unknownTokens))
                        }

                        (generation.currentScope.context as RuleDefinitionContext).rule.unknowns.add(unknown)
                    } else {
                        return Error("unknown should only be inside a rule declaration: " + it.tokens, it.range)
                    }
                }
            }
        }

        if (type == null) {
            println("could not determine this expression type")
            return Error("could not determine this expression type", this.range)
        } else {
            if (type is NumberType) {
                output = execute(output).toString()
            }
            return Ok(Evaluation(this.tokens, type!!))
        }
    }
}

fun main() {
    val generation = Generation()
    finder("someAtom", complexExpression) { handleExpression(generation) }
}
