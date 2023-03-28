package info.teksol.mindcode.processor;

public class IntVariable extends AbstractVariable {
    protected int value;

    public IntVariable(String name, int value) {
        super(name);
        this.value = value;
    }

    public IntVariable(boolean fixed, String name, int value) {
        super(fixed, name);
        this.value = value;
    }

    @Override
    protected String valueToString() {
        return String.valueOf(value);
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
            this.value = (int) value;
            setType(ValueType.DOUBLE);
        }
    }

    @Override
    public int getIntValue() {
        return value;
    }

    @Override
    public void setIntValue(int value) {
        this.value = value;
        setType(ValueType.LONG);
    }

    @Override
    public long getLongValue() {
        return value;
    }

    @Override
    public void setLongValue(long value) {
        this.value = (int) value;
        setType(ValueType.LONG);
    }
}
