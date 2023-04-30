package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.*;

import java.util.List;

public class PopInstruction extends PushOrPopInstruction {

    public PopInstruction(List<LogicArgument> args, List<LogicParameter> params) {
        super(Opcode.POP, args, params);
    }

    protected PopInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    public PopInstruction withMarker(String marker) {
        return new PopInstruction(this, marker);
    }
}
