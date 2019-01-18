

class ConvertPlatformTypeOnReturn {
    fun getValue(): Value {
        <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">return MyClass.value()</error>
    }

    fun getValueExpression(): Value =
        <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">MyClass.value()</error>

    fun getValueNullable(): Value? {
        return MyClass.value()
    }

    fun getValueNullableExpression(): Value? = MyClass.value()

    fun getValuePlatformExpression() = MyClass.value()

    fun getValueExpressionComplex(): Value = if (1.hashCode() > 2) {
        MyClass.value()
    } else {
        Value()
    }

}
