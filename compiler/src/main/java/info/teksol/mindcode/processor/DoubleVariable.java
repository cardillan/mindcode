package info.teksol.mindcode.processor;

public class DoubleVariable extends AbstractVariable {
    protected double value;

    private DoubleVariable(boolean fixed, String name, MindustryObject object, ValueType valueType, double value) {
        super(fixed, name, object, valueType);
        this.value = value;
    }

    public static DoubleVariable newNullValue(boolean fixed, String name) {
        return new DoubleVariable(fixed, name, null, ValueType.NULL, 0.0);
    }


    public static DoubleVariable newBooleanValue(boolean fixed, String name, boolean value) {
        return new DoubleVariable(fixed, name, null, ValueType.BOOLEAN, value ? 1.0 : 0.0);
    }

    public static DoubleVariable newLongValue(boolean fixed, String name, long value) {
        return new DoubleVariable(fixed, name, null, ValueType.LONG, value);
    }
    public static DoubleVariable newDoubleValue(boolean fixed, String name, double value) {
        return new DoubleVariable(fixed, name, null, ValueType.DOUBLE, value);
    }

    public static DoubleVariable newStringValue(boolean fixed, String name, String value) {
        return new DoubleVariable(fixed, name, new MindustryObject(value, value), ValueType.OBJECT, 0.0);
    }

    public static DoubleVariable newObjectValue(boolean fixed, String name, MindustryObject value) {
        return new DoubleVariable(fixed, name, value, ValueType.OBJECT, 0.0);
    }

    @Override
    protected String valueToString() {
        if(Math.abs(value - (long) value) < 0.00001) {
            return String.valueOf((long) value);
        } else {
            return String.valueOf(value);
        }
    }

    @Override
    public void assign(Variable var) {
        if (var.isObject()) {
            setObject(var.getObject());
        } else {
            setDoubleValue(var.getDoubleValue());
        }
        setType(var.getType());
    }

    @Override
    public double getDoubleValue() {
        return value;
    }

    @Override
    public void setDoubleValue(double value) {
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            setObject(null);
        } else {
            this.value = value;
            setType(ValueType.DOUBLE);
        }
    }

    @Override
    public int getIntValue() {
        return (int) value;
    }

    @Override
    public void setIntValue(int value) {
        this.value = value;
        setType(ValueType.LONG);
    }

    @Override
    public long getLongValue() {
        return (long) value;
    }

    @Override
    public void setLongValue(long value) {
        this.value = value;
        setType(ValueType.LONG);
    }
}
