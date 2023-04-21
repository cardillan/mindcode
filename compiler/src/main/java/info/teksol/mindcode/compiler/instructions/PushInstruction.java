package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PushInstruction extends PushOrPopInstruction {

    public PushInstruction(List<LogicArgument> args, List<LogicParameter> params) {
        super(Opcode.PUSH, args, params);
    }

    protected PushInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    public PushInstruction withMarker(String marker) {
        return new PushInstruction(this, marker);
    }
}
