package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;

public interface CallingInstruction extends LogicInstruction {

    LogicLabel getCallAddr();

    default boolean affectsControlFlow() {
        return true;
    }

}
