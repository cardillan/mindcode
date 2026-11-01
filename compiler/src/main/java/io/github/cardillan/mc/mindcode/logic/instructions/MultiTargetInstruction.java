package io.github.cardillan.mc.mindcode.logic.instructions;

import io.github.cardillan.mc.mindcode.logic.arguments.LogicAddress;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MultiTargetInstruction extends LogicInstruction {

    LogicAddress getTarget();

}
