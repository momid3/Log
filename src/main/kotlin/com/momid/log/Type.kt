package com.momid.log

open class Type()

class AtomType(val atom: Atom): Type()

class StringType(val string: Text): Type()

class BooleanType(val boolean: BooleanO): Type()

class NumberType(val number: Int): Type()

class UnknownType(): Type()

class Text(val text: String)

class Atom(val name: String)

class BooleanO(val value: Boolean)

class Eval(val output: String, val type: Type)

class Evaluation(val output: String, val type: Type)
