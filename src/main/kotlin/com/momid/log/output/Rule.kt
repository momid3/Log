package com.momid.log.output

import com.momid.log.Eval
import com.momid.log.Evaluation

class Rule(var ifs: List<RuleInfo>, var thens: List<RuleInfo>, val unknowns: ArrayList<Unknown>)

class RuleInfo(val name: String, val parameters: List<Evaluation>, val value: Evaluation)

class Unknown(val name: String) {
    override fun equals(other: Any?): Boolean {
        return other is Unknown && other.name == this.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
