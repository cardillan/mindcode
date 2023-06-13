package info.teksol.mindcode.logic;

import info.teksol.mindcode.processor.MindustryValue;

public interface LogicLiteral extends LogicValue, MindustryValue {

    default boolean isLiteral() {
        return true;
    }

    default boolean isConstant() {
        return true;
    }

    boolean isNull();

    double getDoubleValue();

    String format();
}
