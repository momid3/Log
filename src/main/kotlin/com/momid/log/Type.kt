package com.momid.log

import com.momid.log.output.InfoAccess
import com.momid.log.output.Unknown

open class Type()

class AtomType(val atom: Atom): Type()

class StringType(val string: Text): Type()

class BooleanType(val boolean: BooleanO): Type()

class NumberType(val number: Int): Type()

class UnknownType(): Type()

class RuleUnknownType(val unknown: Unknown): Type()

class OperatorType(val operator: Operator): Type()

class InfoAccessType(val infoAccess: InfoAccess): Type()

class Text(val text: String)

class Atom(val name: String)

class BooleanO(val value: Boolean)

enum class Operator {
    Operator, Plus, Minus, Multiply, Divide
}

class Eval(val output: String, val type: Type)

class Evaluation(val output: String, val type: Type, val items: ArrayList<Evaluation> = arrayListOf()) :
    List<Evaluation> by items {
    override fun equals(other: Any?): Boolean {
        return other is Evaluation && other.output == this.output
    }

    override fun hashCode(): Int {
        var result = output.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + items.hashCode()
        return result
    }
}

operator fun Evaluation.plusAssign(item: Evaluation) {
    this.items.add(item)
}
