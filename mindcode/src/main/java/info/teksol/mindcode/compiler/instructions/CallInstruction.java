package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.*;

import java.util.List;

public class CallInstruction extends BaseInstruction {

    CallInstruction(List<LogicArgument> args, List<LogicParameter> params) {
        super(Opcode.CALL, args, params);
    }

    protected CallInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    public CallInstruction withMarker(String marker) {
        return new CallInstruction(this, marker);
    }

    public final LogicLabel getCallAddr() {
        return (LogicLabel) getArg(0);
    }
}
