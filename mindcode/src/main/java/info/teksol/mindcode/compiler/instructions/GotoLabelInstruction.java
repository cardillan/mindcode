package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class GotoLabelInstruction extends BaseInstruction implements LabeledInstruction {

    GotoLabelInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.GOTOLABEL, args, params);
    }

    protected GotoLabelInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public GotoLabelInstruction copy() {
        return new GotoLabelInstruction(this, astContext);
    }

    @Override
    public GotoLabelInstruction withContext(AstContext astContext) {
        return new GotoLabelInstruction(this, astContext);
    }

    public boolean matches(LogicInstruction instruction) {
        return getMarker().equals(instruction.getMarker());
    }

    @Override
    public final LogicLabel getLabel() {
        return (LogicLabel) getArg(0);
    }

    public final LogicLabel getMarker() {
        return (LogicLabel) getArg(1);
    }
}
