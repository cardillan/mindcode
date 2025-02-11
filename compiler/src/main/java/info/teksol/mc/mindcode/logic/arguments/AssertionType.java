package info.teksol.mc.mindcode.logic.arguments;

import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum AssertionType {
    any(num -> true, obj -> true),
    notNull(num -> true, Objects::nonNull),
    decimal(num -> true),
    integer(num -> num == (long) num),
    multiple(num -> num == (long) num),
    ;

    public static final AssertionType[] all = values();
    public final AssertionTypeObjLambda objFunction;
    public final AssertionTypeLambda function;

    AssertionType(AssertionTypeLambda function) {
        this(function, obj -> false);
    }

    AssertionType(AssertionTypeLambda function, AssertionTypeObjLambda objFunction) {
        this.function = function;
        this.objFunction = objFunction;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public interface AssertionTypeObjLambda {
        boolean get(Object obj);
    }

    public interface AssertionTypeLambda {
        boolean get(double val);
    }

    private static final Map<String, AssertionType> VALUE_MAP = createValueMap();

    private static Map<String, AssertionType> createValueMap() {
        return Stream.of(AssertionType.values())
                .collect(Collectors.toMap(Enum::name, e -> e));
    }

    public static @Nullable AssertionType byName(String value) {
        return VALUE_MAP.get(value);
    }

    public static AssertionType byName(String value, AssertionType defaultValue) {
        return VALUE_MAP.getOrDefault(value.toLowerCase(), defaultValue);
    }
}
