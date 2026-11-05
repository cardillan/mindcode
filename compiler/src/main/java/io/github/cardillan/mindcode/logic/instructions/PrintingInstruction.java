package io.github.cardillan.mindcode.logic.instructions;

import io.github.cardillan.mindcode.logic.arguments.LogicValue;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PrintingInstruction extends LogicInstruction {

    LogicValue getValue();

}
