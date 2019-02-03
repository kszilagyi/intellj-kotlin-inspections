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

    @NotNull
    static List<Integer> operateNonNullList(BinaryOperator<@NotNull List<Integer>> operator) { return operator.apply(new ArrayList(), new ArrayList()); }
    static List<Integer> operatePlatformList(BinaryOperator<List<Integer>> operator) { return operator.apply(null, null); }
    @Nullable
    static List<Integer> operateNullList(BinaryOperator<@Nullable List<Integer>> operator) { return operator.apply(null, null); }

    public void overridable(Value v) {}

    @NotNull
    static public List<Integer> nonNullList() { return new ArrayList<Integer>(); }

    static public List<Integer> platformList() { return null; }

    @Nullable
    static public List<Integer> nullableList() { return null; }

    static public void takesNonNullList(@NotNull List<Integer> a) {}
    static public void takesPlatformList(List<Integer> a) {}
    static public void takesNullList(@Nullable List<Integer> a) {}
}