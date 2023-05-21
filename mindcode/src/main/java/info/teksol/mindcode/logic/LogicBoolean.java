package info.teksol.mindcode.logic;

public enum LogicBoolean implements LogicLiteral {
    FALSE("false"),
    TRUE("true"),
    ;

    private final String mlog;

    LogicBoolean(String mlog) {
        this.mlog = mlog;
    }

    @Override
    public ArgumentType getType() {
        return ArgumentType.BOOLEAN_LITERAL;
    }

    @Override
    public boolean isNumericLiteral() {
        return true;
    }

    @Override
    public String toMlog() {
        return mlog;
    }

    public LogicBoolean not() {
        return this == FALSE ? TRUE : FALSE;
    }

    @Override
    public String format() {
        return this == TRUE ? "1" : "0";
    }
}
