import java.util.function.BinaryOperator;

public class JavaClass {
    static Value value() {
        return new Value();
    }

    static Value operate(BinaryOperator<Value> operator) { return operator.apply(new Value(), new Value()); }

    public void overridable(Value v);
}