package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class DrawflushInstruction extends BaseInstruction {

    DrawflushInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.DRAWFLUSH, args, params);
    }

    protected DrawflushInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public DrawflushInstruction copy() {
        return new DrawflushInstruction(this, astContext);
    }

    @Override
    public DrawflushInstruction withContext(AstContext astContext) {
        return new DrawflushInstruction(this, astContext);
    }

    public final LogicVariable getDisplay() {
        return (LogicVariable) getArg(0);
    }
}