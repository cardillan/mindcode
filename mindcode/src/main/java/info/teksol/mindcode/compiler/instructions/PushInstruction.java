package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PushInstruction extends PushOrPopInstruction {

    public PushInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params, String marker) {
        super(astContext, Opcode.PUSH, args, params, marker);
    }

    protected PushInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public PushInstruction copy() {
        return new PushInstruction(this, astContext, marker);
    }

    public PushInstruction withMarker(String marker) {
        return new PushInstruction(this, astContext, marker);
    }

    @Override
    public PushInstruction withContext(AstContext astContext) {
        return new PushInstruction(this, astContext, marker);
    }
}
