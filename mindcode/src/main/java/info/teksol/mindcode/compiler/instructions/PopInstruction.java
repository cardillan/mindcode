package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PopInstruction extends PushOrPopInstruction {

    public PopInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.POP, args, params);
    }

    @Override
    public PopInstruction copy() {
        return new PopInstruction(this, marker);
    }

    protected PopInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    public PopInstruction withMarker(String marker) {
        return new PopInstruction(this, marker);
    }
}
