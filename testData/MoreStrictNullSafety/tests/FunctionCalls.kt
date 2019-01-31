
fun function(value: Value) = value
fun functionNullable(value: Value?) = value

fun functionCall(): Unit {
    function(<error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value()</error>)
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
    lambda(<error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value()</error>)
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
    Integer.parseInt(<error descr="You are implicitly converting a nullable (or platform) type into platform type.">number</error>)
}

fun functionCallReverseNonNull() {
    val number: String = "1"
    Integer.parseInt(number)
}

fun functionCallReversePlatform() {
    Integer.parseInt(<error descr="You are implicitly converting a nullable (or platform) type into platform type.">Value().name()</error>)
}

public inline fun <T, R> T.let(block: (T) -> R): R {
    return block(this)
}


fun functionCallReverseLet() {
    fun parse(number: String?): Int {
        return number.let { Integer.parseInt(<error descr="You are implicitly converting a nullable (or platform) type into platform type.">it</error>) }
    }
}

fun functionCallReverseLetNullable() {
    fun parse(number: String?): Int {
        return number?.let { Integer.parseInt(it) } ?: 0
    }
}

fun functionCallReverseLamda() {
    val value: Value? = null
    JavaClass.operate{_, _ -> <error descr="You are implicitly converting a nullable (or platform) type into platform type.">value</error> }
}