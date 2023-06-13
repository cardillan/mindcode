package info.teksol.mindcode.logic;

import info.teksol.mindcode.processor.MindustryValueType;

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
    public MindustryValueType getMindustryValueType() {
        return MindustryValueType.BOOLEAN;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public double getDoubleValue() {
        return this == TRUE ? 1.0 : 0.0;
    }

    @Override
    public long getLongValue() {
        return this == TRUE ? 1 : 0;
    }

    @Override
    public Object getObject() {
        return null;
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

    public static LogicBoolean get(boolean value) {
        return value ? TRUE : FALSE;
    }
}
