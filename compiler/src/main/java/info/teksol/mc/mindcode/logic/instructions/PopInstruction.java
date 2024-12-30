package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class PopInstruction extends PushOrPopInstruction {

    PopInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.POP, args, params);
    }

    protected PopInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public PopInstruction copy() {
        return new PopInstruction(this, astContext);
    }

    @Override
    public PopInstruction withContext(AstContext astContext) {
        return new PopInstruction(this, astContext);
    }
}
