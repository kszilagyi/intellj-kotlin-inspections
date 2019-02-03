

fun assinment() {
    val value: Value = <error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error>
    value.toString()
}

fun assinmentNullable() {
    val value: Value? = JavaClass.value()
    value.toString()
}

fun assinmentNonPlatform() {
    val value: Value = Value()
    value.toString()
}

fun assinmentInferred() {
    val value = JavaClass.value()
    val value2: Value = <error descr="Implicit conversion of platform type to non-nullable">value</error>
    value2.toString()
}

fun assingmentList() {
    val list: List<Int> = <error descr="Implicit conversion of platform type to non-nullable">JavaClass.platformList()</error>
    list.toString()
}

fun assingmentListNullable() {
    val list: List<Int>? = JavaClass.platformList()
    list.toString()
}

fun assingmentListNonNullable() {
    val list: List<Int> = JavaClass.nonNullList()
    list.toString()
}

fun assingmentListNullableFine() {
    val list: List<Int>? = JavaClass.nullableList()
    list.toString()
}

//todo add overrides