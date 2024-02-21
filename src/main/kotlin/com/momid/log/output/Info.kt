package com.momid.log.output

import com.momid.log.Eval
import com.momid.log.Evaluation

class Info(val name: String, val parameters: List<Eval>, val value: Eval)

class InfoParameter()

class InfoAccess(val name: String, val parameters: List<Evaluation>)
