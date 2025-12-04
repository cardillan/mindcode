package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicValue;

public interface BinaryInstruction extends LogicInstruction {
    LogicValue getX();
    LogicValue getY();
}
