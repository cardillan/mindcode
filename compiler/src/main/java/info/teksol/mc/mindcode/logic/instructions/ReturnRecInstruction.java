package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class ReturnRecInstruction extends BaseInstruction {

    ReturnRecInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.RETURNREC, args, params);
    }

    protected ReturnRecInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public boolean affectsControlFlow() {
        return true;
    }

    @Override
    public ReturnRecInstruction copy() {
        return new ReturnRecInstruction(this, astContext);
    }

    @Override
    public ReturnRecInstruction withContext(AstContext astContext) {
        return new ReturnRecInstruction(this, astContext);
    }

    public final LogicVariable getStack() {
        return (LogicVariable) getArg(0);
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }
}
