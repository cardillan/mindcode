package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.InstructionParameterType;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PopInstruction extends PushOrPopInstruction {

    public PopInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
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
