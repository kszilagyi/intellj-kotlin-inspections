import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BinaryOperator;
import java.util.List;
import java.util.ArrayList;


public class JavaClass {
    static Value value() {
        return new Value();
    }

    static Value operate(BinaryOperator<Value> operator) { return operator.apply(new Value(), new Value()); }

    public void overridable(Value v) {}

    @NotNull
    static public List<Integer> nonNullList() { return new ArrayList<Integer>(); }

    static public List<Integer> platformList() { return null; }

    @Nullable
    static public List<Integer> nullableList() { return null; }
}