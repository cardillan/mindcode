package io.github.cardillan.mindcode.logic.instructions;

import io.github.cardillan.mindcode.compiler.MindcodeInternalError;
import io.github.cardillan.mindcode.logic.arguments.Condition;
import io.github.cardillan.mindcode.logic.arguments.LogicBoolean;
import io.github.cardillan.mindcode.logic.arguments.LogicNumber;
import io.github.cardillan.mindcode.logic.arguments.LogicValue;
import io.github.cardillan.mindcode.profile.GlobalCompilerProfile;

public interface ConditionalInstruction extends BinaryInstruction {
    Condition getCondition();

    default boolean isConditional() {
        return getCondition() != Condition.ALWAYS;
    }

    default boolean isUnconditional() {
        return getCondition() == Condition.ALWAYS;
    }

    ConditionalInstruction withOperands(Condition condition, LogicValue x, LogicValue y);

    default boolean supportsAlways() {
        return true;
    }

    default ConditionalInstruction withAlways() {
        return getCondition() == Condition.ALWAYS ? this : withOperands(Condition.ALWAYS, LogicNumber.ZERO, LogicNumber.ZERO);
    }

    default boolean isPlainComparison() {
        Condition c = getCondition();
        return (c == Condition.EQUAL || c == Condition.NOT_EQUAL) && (getX() == LogicBoolean.FALSE || getY() == LogicBoolean.FALSE);
    }

    default boolean isInvertible(GlobalCompilerProfile profile) {
        return getCondition().hasInverse(profile);
    }

    default ConditionalInstruction invert(GlobalCompilerProfile profile) {
        assert getArgumentTypes() != null;
        if (!isInvertible(profile)) {
            throw new MindcodeInternalError("Condition is not invertible. " + this);
        }
        return withOperands(getCondition().inverse(true), getX(), getY());
    }
}
