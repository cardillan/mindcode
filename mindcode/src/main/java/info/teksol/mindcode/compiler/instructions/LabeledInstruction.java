package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicLabel;

public interface LabeledInstruction extends LogicInstruction {

    LogicLabel getLabel();
}
