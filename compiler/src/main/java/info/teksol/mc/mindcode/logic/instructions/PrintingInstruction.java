package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PrintingInstruction extends LogicInstruction {

    LogicValue getValue();

}
