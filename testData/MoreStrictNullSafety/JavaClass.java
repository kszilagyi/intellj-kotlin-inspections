import java.util.function.BinaryOperator;

class JavaClass {
    static Value value() {
        return new Value();
    }

    static Value operate(BinaryOperator<Value> operator) { return operator.apply(new Value(), new Value()); }
}