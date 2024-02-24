package com.momid.log
import com.momid.compiler.okOrReport
import com.momid.compiler.standard_library.continueGiven
import com.momid.compiler.terminal.blue
import com.momid.log.output.Info
import com.momid.log.output.Rule
import com.momid.log.output.RuleInfo
import com.momid.log.output.Unknown
import com.momid.parser.expression.*
import com.momid.parser.not

val forwardInfer =
    !"forward();"

fun ExpressionResultsHandlerContext.handleForwardFunction(generation: Generation): Result<Boolean> {
    with(this.expressionResult) {
        println("forward")
        val substitutions = HashMap<Unknown, Eval>()
        forward(generation, substitutions)
        return Ok(true)
    }
}

fun ExpressionResultsHandlerContext.forward(generation: Generation, substitutions: HashMap<Unknown, Eval>, ruleIndexF: Range? = null, ruleInfoIndexF: Range? = null, infoIndexF: Range? = null): Result<Boolean> {
    val infos = generation.infos
    generation.rules.each(ruleIndexF) rule@ { ruleIndex, rule ->
        println("has some rules")
        rule.ifs.each(ruleInfoIndexF) ruleInfo@ { ruleInfoIndex, ruleInfo ->
            infos.each(infoIndexF) info@ { infoIndex, info ->
                val substitutionsClone = HashMap(substitutions)
                if (matches(info, ruleInfo, substitutionsClone)) {
                    forward(generation, substitutionsClone, only(ruleIndex), from(ruleInfoIndex + 1), null)
                }
            }
            return@rule
        }
        println("this thens is present\n" + rule.thens.map {
            substitutions.forEach { (unknown, substitution) ->
                println(unknown.name to substitution.output)
            }
            var infoText = it.text

            substitutions.forEach { (unknown, substitution) ->
                infoText = infoText.replace(unknown.name, substitution.output)
            }

            val evaledRuleInfo = continueGiven(infoText + ";", info) { handleInfo(generation, false) }.okOrReport {
                println(it.error)
                return it.to()
            }
            blue(evaledRuleInfo.text)
        }.joinToString("\n"))
    }
    return Ok(true)
}

fun matches(info: Info, ruleInfo: RuleInfo, substitutions: HashMap<Unknown, Eval>): Boolean {
    println("matching infos")
    if (info.parameters.size != ruleInfo.parameters.size) {
        return false
    }
    ruleInfo.parameters.forEachIndexed { index, ruleInfoParameter ->
        if (matches(info.parameters[index], ruleInfoParameter, substitutions)) {
            return@forEachIndexed
        }
        return false
    }
    return true
}

fun matches(eval: Eval, evaluation: Evaluation, substitutions: HashMap<Unknown, Eval>): Boolean {
    println("matching evaluations")
    if (evaluation.size > 1) {
        println("multi evaluation not available yet")
        return false
    }
    val evaluationItem = evaluation[0]
    if (evaluation.type is RuleUnknownType) {
        val unknown = (evaluationItem.type as RuleUnknownType).unknown
        if (substitutions[unknown] != null && substitutions[unknown]!!.output != eval.output) {
            println("already contains unknown")
            return false
        } else {
            substitutions[unknown] = eval
            return true
        }
    } else {
        return true
    }
}

inline fun <T> List<T>.each(range: Range?, block: (index: Int, item: T) -> Unit) {
    val fromIndex = range?.first ?: 0
    val untilIndex = range?.last ?: this.lastIndex
    val onlyIndex = range?.only
    if (untilIndex > this.lastIndex) {
        return
    }
    if (onlyIndex != null) {
        block(onlyIndex, this[onlyIndex])
        return
    }
    for (index in fromIndex..untilIndex) {
        val item = this[index]
        block(index, item)
    }
}

class Range(val first: Int?, val last: Int?, val only: Int? = null)

fun <T> List<T>.range(first: Int = 0, last: Int = this.size, only: Int? = null): Range {
    return Range(first, last, only)
}

fun <T> List<T>.defaultRange(): Range {
    return Range(0, this.lastIndex, null)
}

fun only(onlyIndex: Int): Range {
    return Range(null, null, onlyIndex)
}

fun from(fromIndex: Int): Range {
    return Range(fromIndex, null)
}

fun until(untilIndex: Int): Range {
    return Range(null, untilIndex)
}
