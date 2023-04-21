package info.teksol.mindcode.logic;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LogicNumber extends AbstractArgument implements LogicLiteral {
    private static final Map<Integer, LogicNumber> INTEGER_MAP = IntStream.range(-10, 256).boxed()
            .collect(Collectors.toMap(i -> i, i -> new LogicNumber(String.valueOf(i), i)));

    private static final Map<String, LogicNumber> LITERAL_MAP = INTEGER_MAP.entrySet().stream()
            .collect(Collectors.toMap(e -> String.valueOf(e.getKey()), Map.Entry::getValue));

    public static final LogicNumber ZERO = get(0);
    public static final LogicNumber ONE = get(1);

    private final String literal;
    private final double value;

    private LogicNumber(String literal, double value) {
        super(ArgumentType.NUMERIC_LITERAL);
        this.literal = literal;
        this.value = value;
    }

    @Override
    public String toMlog() {
        return literal;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public double getDoubleValue() {
        return value;
    }

    @Override
    public String format() {
        // TODO implement Mindustry-like numeric format
        throw new UnsupportedOperationException("Not yet");
    }

    @Override
    public String toString() {
        return "LogicNumber{" +
                "literal='" + literal + '\'' +
                ", value=" + value +
                '}';
    }

    public static LogicNumber get(String literal, double value) {
        return LITERAL_MAP.containsKey(literal) ? LITERAL_MAP.get(literal) : create(literal, value);
    }

    public static LogicNumber get(int value) {
        return INTEGER_MAP.containsKey(value) ? INTEGER_MAP.get(value) : create(value);
    }

    private static LogicNumber create(int value) {
        return new LogicNumber(String.valueOf(value), value);
    }

    public static LogicNumber create(String literal, double value) {
        return new LogicNumber(literal, value);
    }
}
