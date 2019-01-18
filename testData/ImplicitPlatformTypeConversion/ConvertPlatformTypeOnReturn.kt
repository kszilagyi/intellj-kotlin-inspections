

class ConvertPlatformTypeOnReturn {
    fun getValue(): Value {
        <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">return JavaClass.value()</error>
    }

    fun getValueExpression(): Value =
        <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value()</error>

    fun getValueNullable(): Value? {
        return JavaClass.value()
    }

    fun getValueNullableExpression(): Value? = JavaClass.value()

    fun getValuePlatformExpression() = JavaClass.value()

    fun getValueExpressionComplex1(): Value = if (1.hashCode() > 2) <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">{
        JavaClass.value()
    }</error> else {
        Value()
    }

    fun getValueExpressionComplex2(): Value = if (1.hashCode() > 2) {
        Value()
    } else {
        JavaClass.value()
    }

    fun getValueExpressionComplex3(): Value = if (1.hashCode() > 2) {
        Value()
    } else if (1.hashCode() > 3){
        JavaClass.value()
    } else {
        JavaClass.value()
    }

}