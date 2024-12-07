package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class LabelInstruction extends BaseInstruction implements LabeledInstruction {

    LabelInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
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
