package info.teksol.mindcode.logic;

import info.teksol.mindcode.processor.MindustryValueType;

public enum LogicNull implements LogicLiteral {
    NULL;

    @Override
    public ArgumentType getType() {
        return ArgumentType.NULL_LITERAL;
    }

    @Override
    public MindustryValueType getMindustryValueType() {
        return MindustryValueType.NULL;
    }

    @Override
    public boolean isNull() {
        return true;
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

    @Override
    public long getLongValue() {
        return 0;
    }

    @Override
    public Object getObject() {
        return null;
    }
}
