
fun function(value: Value) = value
fun functionNullable(value: Value?) = value

fun functionCall(): Unit {
    function(<error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error>)
}

fun functionCallNormal(): Unit {
    function(Value())
}

fun functionCallNullable(): Unit {
    functionNullable(JavaClass.value())
}

fun functionCallNormalNullable(): Unit {
    functionNullable(Value())
}

fun lambdaCall(): Unit {
    val lambda = {v: Value -> v}
    lambda(<error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error>)
}

fun lambdaCallNormal(): Unit {
    val lambda = {v: Value -> v}
    lambda(Value())
}

fun lambdaCallNullable(): Unit {
    val lambda = {v: Value? -> v}
    lambda(JavaClass.value())
}

fun functionCallReverse() {
    val number: String? = "1"
    Integer.parseInt(<error descr="Passing nullable to Java code">number</error>)
}

fun functionCallReverseNonNull() {
    val number: String = "1"
    Integer.parseInt(number)
}

fun functionCallReversePlatform() {
    Integer.parseInt(<error descr="Passing platform type to Java code">Value().name()</error>)
}

public inline fun <T, R> T.let(block: (T) -> R): R {
    return block(this)
}


fun functionCallReverseLet() {
    fun parse(number: String?): Int {
        return number.let { Integer.parseInt(<error descr="Passing nullable to Java code">it</error>) }
    }
}

fun functionCallReverseLetNullable() {
    fun parse(number: String?): Int {
        return number?.let { Integer.parseInt(it) } ?: 0
    }
}

fun functionCallReverseLamda() {
    val value: Value? = null
    JavaClass.operate<error descr="Passing nullable to Java code">{_, _ -> value }</error>
}


fun functionCallReverseLambdaPlatform() {
    val value = JavaClass.value()
    JavaClass.operate<error descr="Passing platform type to Java code">{_, _ -> value }</error>
}

fun functionCallReverseLamdaSafe() {
    val value = Value()
    JavaClass.operate{_, _ -> value }
}