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
    }

    @Override
    public double getDoubleValue() {
        return value;
    }

    @Override
    public void setDoubleValue(double value) {
        unsetObject();
        this.value = (int) value;
    }

    @Override
    public int getIntValue() {
        return value;
    }

    @Override
    public void setIntValue(int value) {
        unsetObject();
        this.value = value;
    }

    @Override
    public long getLongValue() {
        return value;
    }

    @Override
    public void setLongValue(long value) {
        unsetObject();
        this.value = (int) value;
    }
}
