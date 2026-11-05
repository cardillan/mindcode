package io.github.cardillan.mindcode.logic.instructions;

import io.github.cardillan.mindcode.logic.arguments.LogicAddress;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MultiTargetInstruction extends LogicInstruction {

    LogicAddress getTarget();

}
