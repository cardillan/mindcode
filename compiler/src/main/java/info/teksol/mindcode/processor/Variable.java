package info.teksol.mindcode.processor;

/**
 * Mindustry processor variable.
 */
public interface Variable {
    String getName();
    void assign(Variable var);
    boolean isObject();

    MindustryObject getObject();
    MindustryObject getExistingObject();
    void setObject(MindustryObject object);

    double getDoubleValue();
    void setDoubleValue(double value);

    long getLongValue();
    void setLongValue(long value);

    int getIntValue();
    void setIntValue(int value);

    default void setBooleanValue(boolean value) {
        setIntValue(value ? 1 : 0);
    }
}
