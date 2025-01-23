package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CallingInstruction extends LogicInstruction {

    LogicLabel getCallAddr();

    default boolean affectsControlFlow() {
        return true;
    }

}
