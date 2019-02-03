

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

//todo add assignment tests
//todo add overrides