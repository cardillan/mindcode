package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class ReturnInstruction extends BaseInstruction {

    ReturnInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.RETURN, args, params);
    }

    protected ReturnInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public boolean affectsControlFlow() {
        return true;
    }

    @Override
    public ReturnInstruction copy() {
        return new ReturnInstruction(this, astContext);
    }

    @Override
    public ReturnInstruction withContext(AstContext astContext) {
        return new ReturnInstruction(this, astContext);
    }

    public final LogicVariable getStack() {
        return (LogicVariable) getArg(0);
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }
}
