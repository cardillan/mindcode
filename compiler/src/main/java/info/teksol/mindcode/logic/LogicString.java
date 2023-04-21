package info.teksol.mindcode.logic;

import java.util.Objects;

public class LogicString extends AbstractArgument implements LogicLiteral {

    public static final LogicString NEW_LINE = create("\\n");

    private final String stringValue;
    private final String literal;

    private LogicString(String stringValue) {
        super(ArgumentType.STRING_LITERAL);
        this.stringValue = Objects.requireNonNull(stringValue);
        this.literal = "\"" + stringValue + "\"";
    }

    @Override
    public boolean isLiteral() {
        return true;
    }

    @Override
    public String format() {
        return stringValue;
    }

    public int length() {
        return stringValue.length();
    }

    @Override
    public String toMlog() {
        return literal;
    }

    @Override
    public String toString() {
        return "LogicString{" +
                "stringValue='" + stringValue + '\'' +
                '}';
    }

    public static LogicString create(String value) {
        return new LogicString(value);
    }

    public static LogicString concat(LogicLiteral value1, LogicLiteral value2) {
        return new LogicString(value1.format() + value2.format());
    }
}

