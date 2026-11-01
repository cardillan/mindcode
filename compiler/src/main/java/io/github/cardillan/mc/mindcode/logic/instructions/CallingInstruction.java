package io.github.cardillan.mc.mindcode.logic.instructions;

import io.github.cardillan.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CallingInstruction extends LogicInstruction {

    LogicLabel getCallAddr();

    default boolean affectsControlFlow() {
        return true;
    }

}
