package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class LabelInstruction extends BaseInstruction {

    LabelInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params, String marker) {
        super(astContext, Opcode.LABEL, args, params, marker);
    }

    protected LabelInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public LabelInstruction copy() {
        return new LabelInstruction(this, astContext, marker);
    }

    public LabelInstruction withMarker(String marker) {
        return new LabelInstruction(this, astContext, marker);
    }

    @Override
    public LabelInstruction withContext(AstContext astContext) {
        return new LabelInstruction(this, astContext, marker);
    }

    public final LogicLabel getLabel() {
        return (LogicLabel) getArg(0);
    }
}
