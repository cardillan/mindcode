package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class PushInstruction extends PushOrPopInstruction {

    PushInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.PUSH, args, params);
    }

    protected PushInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public PushInstruction copy() {
        return new PushInstruction(this, astContext);
    }

    @Override
    public PushInstruction withContext(AstContext astContext) {
        return new PushInstruction(this, astContext);
    }
}
