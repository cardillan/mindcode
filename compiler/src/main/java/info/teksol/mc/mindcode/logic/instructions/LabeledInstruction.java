package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface LabeledInstruction extends LogicInstruction {

    LogicLabel getLabel();

    default boolean affectsControlFlow() {
        return true;
    }
}
