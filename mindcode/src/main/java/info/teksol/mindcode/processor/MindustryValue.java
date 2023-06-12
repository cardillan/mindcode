package info.teksol.mindcode.processor;

public interface MindustryValue {
    MindustryValueType getMindustryValueType();

    double getDoubleValue();

    long getLongValue();

    Object getObject();

    default boolean isObject() {
        return getMindustryValueType() == MindustryValueType.OBJECT;
    }
}
