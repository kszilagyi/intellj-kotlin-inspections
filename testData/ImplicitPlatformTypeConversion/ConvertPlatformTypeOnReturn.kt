

class ConvertPlatformTypeOnReturn {
    fun getValue(): Value {
        return MyClass.<error descr="Implicit conversion of platform type to non-nullable">value</error>();
    }
}