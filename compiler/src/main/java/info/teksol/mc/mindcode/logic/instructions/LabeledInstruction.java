package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;

public interface LabeledInstruction extends LogicInstruction {

    LogicLabel getLabel();
}
