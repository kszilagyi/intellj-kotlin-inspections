

class ReturnAndExpressions {
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

    fun getValueExpressionIf1(): Value = if (1.hashCode() > 1) <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">{
        JavaClass.value()
    }</error> else {
        Value()
    }

    fun getValueExpressionIf2(): Value = if (1.hashCode() > 2) {
        Value()
    } else <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">{
        JavaClass.value()
    }</error>

    fun getValueExpressionIf3(): Value = if (1.hashCode() > 3) {
        Value()
    } else <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">if (1.hashCode() > 4){
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
        2 ->  <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value()</error>
        else ->  <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value()</error>
    }

    fun getValueExpressionWhenNullable(): Value? = when (1.hashCode()) {
        1 -> Value()
        2 -> JavaClass.value()
        else -> JavaClass.value()
    }

    fun getValueExpressionTry(): Value = <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">try { JavaClass.value() }
        catch(e: Throwable) { Value() }</error>


    fun getValueExpressionCatch(): Value = <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">try { Value() }
        catch(e: Throwable) { JavaClass.value() }</error>


    fun getValueExpressionTryNullable(): Value? = try { JavaClass.value() }
        catch(e: Throwable) { Value() }


    fun getValueExpressionCatchNullable(): Value? = try { JavaClass.value() }
        catch(e: Throwable) { JavaClass.value() }

    fun binary(): Value = <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value()</error>  + Value()
    fun binaryNullable(): Value? = <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value()</error> + Value()

    fun binarySwitched(): Value = Value() + <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value()</error>
    fun binarySwitchedNullable(): Value? = Value() + <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value()</error>

    fun binaryNullableParameterInOperator(): Value = JavaClass.value() - Value()
    fun binaryNullableReceiverInOperator(): Value = Value() * JavaClass.value()
    // todo string concat shouldn'be highlighted

    fun stringAddition(): String = "hi " + Value().name() // it's weird that this fails as the plys has the right signature
    fun stringAdditionReverse(): String = Value().name() + " hi"

    fun binaryElvis(): Value = JavaClass.value() ?: Value()
    fun binaryElvisNullable(): Value? = JavaClass.value() ?: Value()
    fun binaryElvisNullableBothPlatform(): Value = JavaClass.value() ?: <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value()</error>

    fun binaryBoth(): Value = <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value()</error> + <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value()</error>
    fun binaryBothNullable(): Value? = <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value()</error> + <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value()</error>

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

    val property: Value = <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value()</error>
    val propertyNullable: Value? = JavaClass.value()

    val propertyGetter: Value
        get() {
            <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">return JavaClass.value()</error>
        }
    val propertyGetterNullable: Value?
        get() {
            return JavaClass.value()
        }

    val propertyGetterBlock: Value
        get() {
            <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">return JavaClass.value()</error>
        }
    val propertyGetterBlockNullable: Value?
        get() {
            return JavaClass.value()
        }


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

    fun dotExpression() {
        <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">JavaClass.value().name()</error>
    }

    fun dotExpressionNormal(){
        Value().name()
    }

    fun dotExpressionNullable(){
        JavaClass.value()?.name()
    }

}

//fun parseAndInc(number: String?): Int {
//    return number.let { Integer.parseInt(it) }
//            .let { it -> it + 1 } ?: 0
//}
//overrides
//generic functions