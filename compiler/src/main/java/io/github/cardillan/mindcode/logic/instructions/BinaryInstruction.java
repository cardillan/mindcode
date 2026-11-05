package io.github.cardillan.mindcode.logic.instructions;

import io.github.cardillan.mindcode.logic.arguments.LogicValue;

public interface BinaryInstruction extends LogicInstruction {
    LogicValue getX();
    LogicValue getY();
}
