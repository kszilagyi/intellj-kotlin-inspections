//todo add list tests

fun lambda(): Unit {
    fun takingLambda(f: () -> Value): Value {
        return f()
    }
    takingLambda <error descr="Implicit conversion of platform type to non-nullable">{ JavaClass.value() }</error>
}

fun lambdaReferenced(): Unit {
    fun takingLambda(f: () -> Value): Value {
        return f()
    }
    val lambda = { JavaClass.value() }
    takingLambda (<error descr="Implicit conversion of platform type to non-nullable">lambda</error>)
}

fun lambdaNullable(): Unit {
    fun takingLambda(f: () -> Value?): Value? {
        return f()
    }
    takingLambda{ JavaClass.value() }
}

fun lambdaOneParam(): Unit {
    fun takingLambda(f: (Int) -> Value): Value {
        return f(1)
    }
    takingLambda <error descr="Implicit conversion of platform type to non-nullable">{ _ -> JavaClass.value() }</error>
}

fun lambdaMultiParam(): Unit {
    fun takingLambda(f: (Int, Int, Int, Int) -> Value): Value {
        return f(1, 2, 3, 4)
    }
    takingLambda <error descr="Implicit conversion of platform type to non-nullable">{ _, _, _, _ -> JavaClass.value() }</error>
}

private inline fun takingLambdaInline(f: () -> Value): Value {
    return f()
}

fun lambdaWithReturn(): Value {
    return takingLambdaInline { <error descr="Implicit conversion of platform type to non-nullable">return JavaClass.value()</error> }
}

private inline fun takingLambdaInlineNullable(f: () -> Value?): Value {
    return f() ?: Value()
}

fun lambdaWithReturnNullableLambda(): Value {
    return takingLambdaInlineNullable { <error descr="Implicit conversion of platform type to non-nullable">return JavaClass.value()</error> }
}

fun lambdaWithReturnNullableFunction(): Value? {
    return takingLambdaInline { return JavaClass.value() }
}

fun lambdaWithReturnLabeled(): Value {
    return takingLambdaInline <error descr="Implicit conversion of platform type to non-nullable">{ return@takingLambdaInline JavaClass.value() }</error>
}

fun lambdaWithReturnNullableLambdaLabeled(): Value {
    return takingLambdaInlineNullable { return@takingLambdaInlineNullable JavaClass.value() }
}

fun anonymousFunction(): Value {
    return takingLambdaInline ( fun(): Value { <error descr="Implicit conversion of platform type to non-nullable">return JavaClass.value()</error> } )
}

fun anonymousFunctionNullable(): Value {
    return takingLambdaInlineNullable ( fun(): Value? { return JavaClass.value() } )
}

fun anonymousFunctionLabeled(): Value {
    return takingLambdaInline ( fun(): Value { <error descr="Implicit conversion of platform type to non-nullable">return@anonymousFunctionLabeled JavaClass.value()</error> } )
}

fun anonymousFunctionNullableLabeled(): Value? {
    return takingLambdaInline ( fun(): Value { return@anonymousFunctionNullableLabeled JavaClass.value() } )
}

fun referredFun() = JavaClass.value()

fun functionReference(): Value {
    return takingLambdaInline ( <error descr="Implicit conversion of platform type to non-nullable">::referredFun</error> )
}

fun functionReferenceNullable(): Value {
    return takingLambdaInlineNullable(::referredFun)
}