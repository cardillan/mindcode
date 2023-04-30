package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class LabelInstruction extends BaseInstruction {

    LabelInstruction(List<LogicArgument> args, List<LogicParameter> params) {
        super(Opcode.LABEL, args, params);
    }

    protected LabelInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    public LabelInstruction withMarker(String marker) {
        return new LabelInstruction(this, marker);
    }

    public final LogicLabel getLabel() {
        return (LogicLabel) getArg(0);
    }
}
