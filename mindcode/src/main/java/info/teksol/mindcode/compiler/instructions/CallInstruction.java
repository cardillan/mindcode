package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class CallInstruction extends BaseInstruction {

    CallInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.CALL, args, params);
    }

    protected CallInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    @Override
    public CallInstruction copy() {
        return new CallInstruction(this, marker);
    }

    public CallInstruction withMarker(String marker) {
        return new CallInstruction(this, marker);
    }

    public final LogicLabel getCallAddr() {
        return (LogicLabel) getArg(0);
    }
}
