package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class LabelInstruction extends BaseInstruction implements LabeledInstruction {

    LabelInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.LABEL, args, params);
    }

    protected LabelInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public LabelInstruction copy() {
        return new LabelInstruction(this, astContext);
    }

    @Override
    public LabelInstruction withContext(AstContext astContext) {
        return new LabelInstruction(this, astContext);
    }

    @Override
    public LogicLabel getLabel() {
        return (LogicLabel) getArg(0);
    }
}
