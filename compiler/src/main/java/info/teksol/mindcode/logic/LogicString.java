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
    public String format() {
        return stringValue;
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
}

