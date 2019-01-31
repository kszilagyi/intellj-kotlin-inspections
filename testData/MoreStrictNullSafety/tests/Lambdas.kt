fun lambda(): Unit {
    fun takingLambda(f: () -> Value): Value {
        return f()
    }
    takingLambda <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">{ JavaClass.value() }</error>
}

fun lambdaReferenced(): Unit {
    fun takingLambda(f: () -> Value): Value {
        return f()
    }
    val lambda = { JavaClass.value() }
    takingLambda (<error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">lambda</error>)
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
    takingLambda <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">{ _ -> JavaClass.value() }</error>
}

fun lambdaMultiParam(): Unit {
    fun takingLambda(f: (Int, Int, Int, Int) -> Value): Value {
        return f(1, 2, 3, 4)
    }
    takingLambda <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">{ _, _, _, _ -> JavaClass.value() }</error>
}

private inline fun takingLambdaInline(f: () -> Value): Value {
    return f()
}

fun lambdaWithReturn(): Value {
    return this.takingLambdaInline { <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">return JavaClass.value()</error> }
}

private inline fun takingLambdaInlineNullable(f: () -> Value?): Value {
    return f() ?: Value()
}

fun lambdaWithReturnNullableLambda(): Value {
    return this.takingLambdaInlineNullable { <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">return JavaClass.value()</error> }
}

fun lambdaWithReturnNullableFunction(): Value? {
    return this.takingLambdaInline { return JavaClass.value() }
}

fun lambdaWithReturnLabeled(): Value {
    return this.takingLambdaInline <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">{ return@takingLambdaInline JavaClass.value() }</error>
}

fun lambdaWithReturnNullableLambdaLabeled(): Value {
    return this.takingLambdaInlineNullable { return@takingLambdaInlineNullable JavaClass.value() }
}

fun anonymousFunction(): Value {
    return takingLambdaInline ( fun(): Value { <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">return JavaClass.value()</error> } )
}

fun anonymousFunctionNullable(): Value {
    return takingLambdaInlineNullable ( fun(): Value? { return JavaClass.value() } )
}

fun anonymousFunctionLabeled(): Value {
    return takingLambdaInline ( fun(): Value { <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">return@anonymousFunctionLabeled JavaClass.value()</error> } )
}

fun anonymousFunctionNullableLabeled(): Value? {
    return takingLambdaInline ( fun(): Value { return@anonymousFunctionNullableLabeled JavaClass.value() } )
}

fun referredFun() = JavaClass.value()

fun functionReference(): Value {
    return takingLambdaInline ( <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">::referredFun</error> )
}

fun functionReferenceNullable(): Value {
    return takingLambdaInlineNullable(::referredFun)
}