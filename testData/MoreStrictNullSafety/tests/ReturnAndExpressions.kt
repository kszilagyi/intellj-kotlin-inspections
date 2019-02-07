//todo add list tests

class ReturnAndExpressions {
    fun getValue(): Value {
        <error descr="Implicit conversion of platform type to non-nullable">return JavaClass.value()</error>
    }

    fun getValueExpression(): Value =
        <error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error>

    fun getValueNullable(): Value? {
        return JavaClass.value()
    }

    fun getValueNullableExpression(): Value? = JavaClass.value()

    fun getValuePlatformExpression() = JavaClass.value()

    fun getValueExpressionIf1(): Value = if (1.hashCode() > 1) <error descr="Implicit conversion of platform type to non-nullable">{
        JavaClass.value()
    }</error> else {
        Value()
    }

    fun getValueExpressionIf2(): Value = if (1.hashCode() > 2) {
        Value()
    } else <error descr="Implicit conversion of platform type to non-nullable">{
        JavaClass.value()
    }</error>

    fun getValueExpressionIf3(): Value = if (1.hashCode() > 3) {
        Value()
    } else <error descr="Implicit conversion of platform type to non-nullable">if (1.hashCode() > 4){
        JavaClass.value()
    } else {
        JavaClass.value()
    }</error>

    fun getValueExpressionIfNullable(): Value? = if (1.hashCode() > 3) {
        Value()
    } else if (1.hashCode() > 4) {
        JavaClass.value()
    } else {
        JavaClass.value()
    }

    fun getValueExpressionWhen(): Value = when (1.hashCode()) {
        1 -> Value()
        2 ->  <error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error>
        else ->  <error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error>
    }

    fun getValueExpressionWhenNullable(): Value? = when (1.hashCode()) {
        1 -> Value()
        2 -> JavaClass.value()
        else -> JavaClass.value()
    }

    fun getValueExpressionTry(): Value = <error descr="Implicit conversion of platform type to non-nullable">try { JavaClass.value() }
        catch(e: Throwable) { Value() }</error>


    fun getValueExpressionCatch(): Value = <error descr="Implicit conversion of platform type to non-nullable">try { Value() }
        catch(e: Throwable) { JavaClass.value() }</error>


    fun getValueExpressionTryNullable(): Value? = try { JavaClass.value() }
        catch(e: Throwable) { Value() }


    fun getValueExpressionCatchNullable(): Value? = try { JavaClass.value() }
        catch(e: Throwable) { JavaClass.value() }

    fun binary(): Value = <error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error>  + Value()
    fun binaryNullable(): Value? = <error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error> + Value()

    fun binarySwitched(): Value = Value() + <error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error>
    fun binarySwitchedNullable(): Value? = Value() + <error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error>

    fun binaryNullableParameterInOperator(): Value = JavaClass.value() - Value()
    fun binaryNullableReceiverInOperator(): Value = Value() * JavaClass.value()

    fun stringAddition(): String = "hi " + Value().name()
    fun stringAdditionReverse(): String = Value().name() + " hi"

    fun binaryElvis(): Value = JavaClass.value() ?: Value()
    fun binaryElvisNullable(): Value? = JavaClass.value() ?: Value()
    fun binaryElvisNullableBothPlatform(): Value = JavaClass.value() ?: <error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error>

    fun binaryBoth(): Value = <error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error> + <error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error>
    fun binaryBothNullable(): Value? = <error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error> + <error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error>

    fun binaryNullCheck() {
        if(JavaClass.value() == null) {}
    }

    fun binaryNonNullCheck() {
        if(JavaClass.value() != null) {}
    }

    fun binaryNullCheckReverse() {
        if(null == JavaClass.value()) {}
    }

    fun binaryNonNullCheckReverse() {
        if(null != JavaClass.value()) {}
    }

    fun binaryNullCheckReference() {
        if(JavaClass.value() === null) {}
    }

    fun binaryNonNullCheckReference() {
        if(JavaClass.value() !== null) {}
    }

    fun binaryNullCheckReverseReference() {
        if(null === JavaClass.value()) {}
    }

    fun binaryNonNullCheckReverseReference() {
        if(null !== JavaClass.value()) {}
    }


    val property: Value = <error descr="Implicit conversion of platform type to non-nullable">JavaClass.value()</error>
    val propertyNullable: Value? = JavaClass.value()

    val propertyGetter: Value
        get() {
            <error descr="Implicit conversion of platform type to non-nullable">return JavaClass.value()</error>
        }
    val propertyGetterNullable: Value?
        get() {
            return JavaClass.value()
        }

    val propertyGetterBlock: Value
        get() {
            <error descr="Implicit conversion of platform type to non-nullable">return JavaClass.value()</error>
        }
    val propertyGetterBlockNullable: Value?
        get() {
            return JavaClass.value()
        }

    fun dotExpression() {
        JavaClass.value().<error descr="Unsafe call on platform type">name</error>()
    }

    fun dotExpressionNormal(){
        Value().name()
    }

    fun dotExpressionNullable(){
        JavaClass.value()?.name()
    }


}

//generic functions
//assigning to variable