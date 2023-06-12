package info.teksol.mindcode.logic;

import info.teksol.mindcode.processor.MindustryValue;

public interface LogicLiteral extends LogicValue, MindustryValue {

    default boolean isLiteral() {
        return true;
    }

    double getDoubleValue();

    String format();
}
