
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

fun functionTakingList(l: List<Int>) = l
fun functionTakingListNullable(l: List<Int>?) = l

fun functionCallListNonNon(): Unit {
    functionTakingList(JavaClass.nonNullList())
}

fun functionCallListNonPlatform(): Unit {
    functionTakingList(<error descr="Implicit conversion of platform type to non-nullable">JavaClass.platformList()</error>)
}

fun functionCallListNullPlatform(): Unit {
    functionTakingListNullable(JavaClass.platformList())
}

fun functionCallListNullNon(): Unit {
    functionTakingListNullable(JavaClass.nonNullList())
}

fun functionCallListNullNull(): Unit {
    functionTakingListNullable(JavaClass.nullableList())
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

fun lambdaCallTakingListNotNot(): Unit {
    val lambda = {v: List<Int> -> v}
    lambda(JavaClass.nonNullList())
}

fun lambdaCallTakingListNotPlatform(): Unit {
    val lambda = {v: List<Int> -> v}
    lambda(<error descr="Implicit conversion of platform type to non-nullable">JavaClass.platformList()</error>)
}

fun lambdaCallTakingListNullNot(): Unit {
    val lambda = {v: List<Int>? -> v}
    lambda(JavaClass.nonNullList())
}

fun lambdaCallTakingListNullPlatform(): Unit {
    val lambda = {v: List<Int>? -> v}
    lambda(JavaClass.platformList())
}

fun lambdaCallTakingListNullNull(): Unit {
    val lambda = {v: List<Int>? -> v}
    lambda(JavaClass.nullableList())
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

fun functionCallReverseListNotNot() {
    val list: List<Int> = JavaClass.nonNullList()
    JavaClass.takesNonNullList(list)
}

fun functionCallReverseListNotPlatform() {
    JavaClass.takesNonNullList(<error descr="Implicit conversion of platform type to non-nullable">JavaClass.platformList()</error>)
}


fun functionCallReverseListPlatformNot() {
    val list: List<Int> = JavaClass.nonNullList()
    JavaClass.takesPlatformList(list)
}

fun functionCallReverseListPlatformPlatform() {
    JavaClass.takesPlatformList(<error descr="Passing platform type to Java code">JavaClass.platformList()</error>)
}

fun functionCallReverseListPlatformNull() {
    val list: List<Int>? = JavaClass.nonNullList()
    JavaClass.takesPlatformList(<error descr="Passing nullable to Java code">list</error>)
}

fun functionCallReverseListNullNot() {
    val list: List<Int> = JavaClass.nonNullList()
    JavaClass.takesNullList(list)
}

fun functionCallReverseListNullPlatform() {
    JavaClass.takesNullList(JavaClass.platformList())
}

fun functionCallReverseListNullNull() {
    val list: List<Int>? = JavaClass.nonNullList()
    JavaClass.takesNullList(list)
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

fun functionCallReverseLamdaList() {
    val list: List<Int> = JavaClass.nonNullList()
    JavaClass.operateNonNullList{_, _ -> list }
}

fun functionCallReverseLamdaListToNull() {
    val list: List<Int> = JavaClass.nonNullList()
    JavaClass.operateNullList{_, _ -> list }
}

fun functionCallReverseLamdaListToPlatform() {
    val list: List<Int> = JavaClass.nonNullList()
    JavaClass.operatePlatformList{_, _ -> list }
}

fun functionCallReverseLamdaListNullToPlatform() {
    val list: List<Int>? = JavaClass.nonNullList()
    JavaClass.operatePlatformList<error descr="Passing nullable to Java code">{_, _ -> list }</error>
}

fun functionCallReverseLamdaNullToNull() {
    val list: List<Int>? = JavaClass.nonNullList()
    JavaClass.operateNullList{_, _ -> list }
}

fun functionCallReverseLamdaPlatformToNull() {
    val list = JavaClass.platformList()
    JavaClass.operateNullList{_, _ -> list }
}