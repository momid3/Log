package com.momid.log

import com.momid.compiler.okOrReport
import com.momid.parser.expression.*
import net.objecthunter.exp4j.ExpressionBuilder
import javax.script.ScriptEngineManager

fun compile(text: String) {
    val generation = Generation()
    val finder = ExpressionFinder()
    finder.registerExpressions(listOf(info, print))
    finder.start(text.toList()).forEach {
        handleExpressionResult(finder, it, text.toList()) {
            with(this.expressionResult) {
                this.isOf(info) {
                    val eval = continueStraight(it) { handleInfo(generation) }.okOrReport {
                        return@handleExpressionResult it.to()
                    }
//                    println(execute(eval.output))
                }

                this.isOf(print) {
                    continueStraight(it) { handlePrint(generation) }.okOrReport {
                        return@handleExpressionResult it.to()
                    }
                }
                return@handleExpressionResult Ok(true)
            }
        }
    }
}

fun main() {
    compile(
        """
           areFriends(3) = 3 + 7 + 333;print(areFriends(3));
        """.trimIndent()
    )
}

fun <T> finder(text: String, registeredExpression: Expression, handle: ExpressionResultsHandlerContext.() -> Result<T>) {
    val finder = ExpressionFinder()
    finder.registerExpressions(listOf(registeredExpression))
    finder.start(text.toList()).forEach {
        handleExpressionResult(finder, it, text.toList()) {
            handle()
        }
    }
}

//fun execute(code: String): Int {
//    val scriptEngineManager = ScriptEngineManager()
//    scriptEngineManager.getEngineFactories().forEach {
//        println(it.names)
//    }
//    val scriptEngine = scriptEngineManager.getEngineByName("kotlin").also { println(it) }
//    return scriptEngine?.eval(code) as Int
//}

fun execute(code: String): Int {
    return ExpressionBuilder(code).build().evaluate().toInt()
}
