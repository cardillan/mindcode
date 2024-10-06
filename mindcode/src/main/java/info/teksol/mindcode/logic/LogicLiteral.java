package info.teksol.mindcode.logic;

import info.teksol.emulator.processor.LogicReadable;

public interface LogicLiteral extends LogicValue, LogicReadable {

    default boolean isLiteral() {
        return true;
    }

    default boolean isConstant() {
        return true;
    }

    boolean isNull();

    double getDoubleValue();
}
