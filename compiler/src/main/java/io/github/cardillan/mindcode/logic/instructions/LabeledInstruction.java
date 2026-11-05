package io.github.cardillan.mindcode.logic.instructions;

import io.github.cardillan.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface LabeledInstruction extends LogicInstruction {

    LogicLabel getLabel();

    default boolean affectsControlFlow() {
        return true;
    }
}
