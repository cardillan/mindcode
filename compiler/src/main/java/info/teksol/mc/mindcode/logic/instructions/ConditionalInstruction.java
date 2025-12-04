package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.Condition;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;

public interface ConditionalInstruction extends BinaryInstruction {
    Condition getCondition();

    default boolean isConditional() {
        return getCondition() != Condition.ALWAYS;
    }

    default boolean isUnconditional() {
        return getCondition() == Condition.ALWAYS;
    }

    ConditionalInstruction withOperands(Condition condition, LogicValue x, LogicValue y);
}
