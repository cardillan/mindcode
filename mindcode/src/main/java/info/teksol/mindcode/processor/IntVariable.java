package info.teksol.mindcode.processor;

public class IntVariable extends AbstractVariable {
    protected int value;

    private IntVariable(boolean fixed, String name, MindustryObject object, MindustryValueType valueType, int value) {
        super(fixed, name, object, valueType);
        this.value = value;
    }

    public static IntVariable newNullValue(boolean fixed, String name) {
        return new IntVariable(fixed, name, null, MindustryValueType.NULL, 0);
    }

    public static IntVariable newBooleanValue(boolean fixed, String name, boolean value) {
        return new IntVariable(fixed, name, null, MindustryValueType.BOOLEAN, value ? 1 : 0);
    }

    public static IntVariable newIntValue(boolean fixed, String name, int value) {
        return new IntVariable(fixed, name, null, MindustryValueType.LONG, value);
    }

    public static IntVariable newStringValue(boolean fixed, String name, String value) {
        return new IntVariable(fixed, name, new MindustryObject(value, value), MindustryValueType.OBJECT, 0);
    }

    public static IntVariable newObjectValue(boolean fixed, String name, MindustryObject value) {
        return new IntVariable(fixed, name, value, MindustryValueType.OBJECT, 0);
    }

    @Override
    protected String valueToString() {
        return String.valueOf(value);
    }

    @Override
    public void assign(Variable var) {
        MindustryValueType type = var.getMindustryValueType();
        switch (type) {
            case NULL, OBJECT -> setObject(var.getObject());
            default -> setDoubleValue(var.getDoubleValue());
        }
        setType(type);
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
            setType(MindustryValueType.DOUBLE);
        }
    }

    @Override
    public int getIntValue() {
        return value;
    }

    @Override
    public void setIntValue(int value) {
        this.value = value;
        setType(MindustryValueType.LONG);
    }

    @Override
    public long getLongValue() {
        return value;
    }

    @Override
    public void setLongValue(long value) {
        this.value = (int) value;
        setType(MindustryValueType.LONG);
    }
}
