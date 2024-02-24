package com.momid.log.output

import com.momid.compiler.forEveryIndexed
import com.momid.log.Eval
import com.momid.log.Evaluation

class Info(val name: String, val parameters: List<Eval>, val value: Eval)

class InfoParameter()

class InfoAccess(val name: String, val parameters: List<Evaluation>)

fun sameInfo(info: Info, otherInfo: Info): Boolean {
    return info.name == otherInfo.name && info.parameters.size == otherInfo.parameters.size && info.parameters.forEveryIndexed { index, parameter ->
        otherInfo.parameters[index] == parameter
    }
}
