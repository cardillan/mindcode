package info.teksol.mindcode.logic;

import info.teksol.evaluator.LogicReadable;

public interface LogicLiteral extends LogicValue, LogicReadable {

    @Override
    default boolean isLiteral() {
        return true;
    }

    @Override
    default boolean isConstant() {
        return true;
    }

    @Override
    default boolean canEvaluate() {
        return true;
    }

    boolean isNull();

    double getDoubleValue();
}
