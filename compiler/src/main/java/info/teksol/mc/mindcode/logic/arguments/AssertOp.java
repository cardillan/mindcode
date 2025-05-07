package info.teksol.mc.mindcode.logic.arguments;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public enum AssertOp {
    lessThan("<", (a, b) -> a < b),
    lessThanEq("<=", (a, b) -> a <= b),
    ;

    public static final AssertOp[] all = values();

    public final AssertOpLambda function;
    public final String symbol;

    AssertOp(String symbol, AssertOpLambda function){
        this.symbol = symbol;
        this.function = function;
    }

    @Override
    public String toString(){
        return symbol;
    }

    public interface AssertOpLambda {
        boolean get(double a, double b);
    }
    
    private static final Map<String, AssertOp> VALUE_MAP = createValueMap();

    private static Map<String, AssertOp> createValueMap() {
        return Stream.of(AssertOp.values())
                .collect(Collectors.toMap(Enum::name, e -> e));
    }

    public static @Nullable AssertOp byName(String value) {
        return VALUE_MAP.get(value);
    }

    public static AssertOp byName(String value, AssertOp defaultValue) {
        return VALUE_MAP.getOrDefault(value.toLowerCase(), defaultValue);
    }
}
