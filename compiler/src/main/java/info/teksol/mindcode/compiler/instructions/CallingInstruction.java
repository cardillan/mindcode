package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicLabel;

public interface CallingInstruction extends LogicInstruction {

    LogicLabel getCallAddr();
}
