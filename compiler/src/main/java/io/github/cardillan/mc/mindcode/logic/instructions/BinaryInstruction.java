package io.github.cardillan.mc.mindcode.logic.instructions;

import io.github.cardillan.mc.mindcode.logic.arguments.LogicValue;

public interface BinaryInstruction extends LogicInstruction {
    LogicValue getX();
    LogicValue getY();
}
