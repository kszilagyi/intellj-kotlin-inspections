

class ConvertPlatformTypeOnReturn {
    fun getValue(): Value {
        <error descr="You are implicitly converting a platform type into a non-nullable type. This code might throw.">return MyClass.value()</error>;
    }
}