package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PopInstruction extends PushOrPopInstruction {

    public PopInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params, String marker) {
        super(astContext, Opcode.POP, args, params, marker);
    }

    protected PopInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public PopInstruction copy() {
        return new PopInstruction(this, astContext, marker);
    }

    public PopInstruction withMarker(String marker) {
        return new PopInstruction(this, astContext, marker);
    }

    @Override
    public PopInstruction withContext(AstContext astContext) {
        return new PopInstruction(this, astContext, marker);
    }
}
