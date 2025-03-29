package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicAddress;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MultiTargetInstruction extends LogicInstruction {

    LogicAddress getTarget();

}
