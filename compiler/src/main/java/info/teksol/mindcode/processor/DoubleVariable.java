package info.teksol.mindcode.processor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class DoubleVariable extends AbstractVariable {
    private static final NumberFormat format = createNumberFormat();

    protected double value;

    public DoubleVariable(String name, MindustryObject object) {
        super(name, object);
    }

    public DoubleVariable(boolean fixed, String name, MindustryObject object) {
        super(fixed, name, object);
    }

    public DoubleVariable(String name, double value) {
        super(name);
        this.value = value;
    }

    public DoubleVariable(boolean fixed, String name, double value) {
        super(fixed, name);
        this.value = value;
    }

    @Override
    protected String valueToString() {
        return format.format(value);
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

    private static NumberFormat createNumberFormat() {
        NumberFormat format = DecimalFormat.getNumberInstance(Locale.US);
        format.setGroupingUsed(false);
        return format;
    }
}
