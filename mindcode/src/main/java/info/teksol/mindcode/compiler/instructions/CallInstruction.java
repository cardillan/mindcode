package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class CallInstruction extends BaseInstruction {

    CallInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params, String marker) {
        super(astContext, Opcode.CALL, args, params, marker);
    }

    protected CallInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public CallInstruction copy() {
        return new CallInstruction(this, astContext, marker);
    }

    public CallInstruction withMarker(String marker) {
        return new CallInstruction(this, astContext, marker);
    }

    @Override
    public CallInstruction withContext(AstContext astContext) {
        return new CallInstruction(this, astContext, marker);
    }

    public final LogicLabel getCallAddr() {
        return (LogicLabel) getArg(0);
    }
}
