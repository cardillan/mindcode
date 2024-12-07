package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class GotoLabelInstruction extends BaseInstruction implements LabeledInstruction {

    GotoLabelInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
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
