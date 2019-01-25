

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
        val values = Generic.from(JavaClass.value())
        fun <A>takingLambda(xs: Generic<A>, f: (A) -> Value): Generic<Value> {
            return xs.map(f)
        }
        takingLambda(values) <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">{ v -> v }</error>

    }
    //todo function reference instead of lambda
    //todo return from lambda
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


}
//Instantiating a function type
//There are several ways to obtain an instance of a function type:
//
//Using a code block within a function literal, in one of the forms:
//a lambda expression: { a, b -> a + b },
//an anonymous function: fun(s: String): Int { return s.toIntOrNull() ?: 0 }
//Function literals with receiver can be used as values of function types with receiver.
//
//Using a callable reference to an existing declaration:
//a top-level, local, member, or extension function: ::isOdd, String::toInt,
//a top-level, member, or extension property: List<Int>::size,
//a constructor: ::Regex
//These include bound callable references that point to a member of a particular instance: foo::toString.

//fun parseAndInc(number: String?): Int {
//    return number.let { Integer.parseInt(it) }
//            .let { it -> it + 1 } ?: 0
//}

//generic functions