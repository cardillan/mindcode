package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.logic.arguments.Condition;
import info.teksol.mc.mindcode.logic.arguments.LogicBoolean;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.profile.GlobalCompilerProfile;

public interface ConditionalInstruction extends BinaryInstruction {
    Condition getCondition();

    default boolean isConditional() {
        return getCondition() != Condition.ALWAYS;
    }

    default boolean isUnconditional() {
        return getCondition() == Condition.ALWAYS;
    }

    ConditionalInstruction withOperands(Condition condition, LogicValue x, LogicValue y);

    default boolean isPlainComparison() {
        Condition c = getCondition();
        return (c == Condition.EQUAL || c == Condition.NOT_EQUAL) && (getX() == LogicBoolean.FALSE || getY() == LogicBoolean.FALSE);
    }

    default boolean isInvertible(GlobalCompilerProfile profile) {
        return getCondition().hasInverse(profile);
    }

    default ConditionalInstruction invert(GlobalCompilerProfile profile) {
        if (!isInvertible(profile)) {
            throw new MindcodeInternalError("Condition is not invertible. " + this);
        }
        return forceInvert();
    }

    default ConditionalInstruction forceInvert() {
        assert getArgumentTypes() != null;
        return withOperands(getCondition().inverse(true), getX(), getY());
    }
}
