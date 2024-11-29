package info.teksol.mindcode.logic;

import info.teksol.mindcode.compiler.instructions.InstructionProcessor;

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
    public boolean isObject() {
        return false;
    }

    @Override
    public boolean isNumericLiteral() {
        return true;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public String toMlog() {
        return literal;
    }

    @Override
    public double getDoubleValue() {
        return value;
    }

    @Override
    public long getLongValue() {
        return (long) value;
    }

    public int getIntValue() {
        return (int) value;
    }

    public boolean isInteger() {
        return value == (int) value;
    }

    @Override
    public Object getObject() {
        return null;
    }

    public LogicNumber negation() {
        if (literal.equals("0")) return this;
        if (literal.startsWith("-")) {
            return get(literal.substring(1), -value);
        } else {
            return get("-" + literal, -value);
        }
    }

    public LogicNumber reciprocal(InstructionProcessor instructionProcessor) {
        if (literal.equals("1")) return this;
        if (value == 0.0) return null;
        double r = 1 / value;
        return instructionProcessor.mlogFormat(r).map(s -> get(s, r)).orElse(null);
    }

    @Override
    public String format(InstructionProcessor instructionProcessor) {
        return instructionProcessor.formatNumber(value);
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

    private static LogicNumber create(String literal, double value) {
        return new LogicNumber(literal, value);
    }
}
