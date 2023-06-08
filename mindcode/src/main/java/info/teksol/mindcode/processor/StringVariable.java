package info.teksol.mindcode.processor;

import info.teksol.mindcode.MindcodeException;

public class StringVariable extends AbstractVariable {
    protected String value;

    private StringVariable(boolean fixed, String name, MindustryObject object, ValueType valueType, String value) {
        super(fixed, name, object, valueType);
        this.value = value;
    }

    public static StringVariable newStringValue(boolean fixed, String name, String value) {
        return new StringVariable(fixed, name, new MindustryObject(value, value), ValueType.OBJECT, value);
    }

    public String getStringValue() {
        return value;
    }

    @Override
    protected String valueToString() {
        return value;
    }

    @Override
    public void assign(Variable var) {
        if (var instanceof StringVariable other) {
            value = other.value;
            setObject(other.getObject());
        } else {
            throw new MindcodeException("Unsupported string expression.");
        }
    }

    @Override
    public double getDoubleValue() {
        throw new MindcodeException("Unsupported string expression.");
    }

    @Override
    public void setDoubleValue(double value) {
        throw new MindcodeException("Unsupported string expression.");
    }

    @Override
    public int getIntValue() {
        throw new MindcodeException("Unsupported string expression.");
    }

    @Override
    public void setIntValue(int value) {
        throw new MindcodeException("Unsupported string expression.");
    }

    @Override
    public long getLongValue() {
        throw new MindcodeException("Unsupported string expression.");
    }

    @Override
    public void setLongValue(long value) {
        throw new MindcodeException("Unsupported string expression.");
    }
}
