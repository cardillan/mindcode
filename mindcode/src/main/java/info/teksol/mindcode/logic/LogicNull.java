package info.teksol.mindcode.logic;

public enum LogicNull implements LogicLiteral {
    NULL;

    @Override
    public ArgumentType getType() {
        return ArgumentType.NULL_LITERAL;
    }

    @Override
    public boolean isNumericLiteral() {
        return true;
    }

    @Override
    public String toMlog() {
        return "null";
    }

    @Override
    public String format() {
        return "null";
    }

    @Override
    public double getDoubleValue() {
        return 0.0;
    }
}
