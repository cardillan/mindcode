package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class CallRecInstruction extends BaseInstruction {

    CallRecInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params, String marker) {
        super(astContext, Opcode.CALLREC, args, params, marker);
    }

    protected CallRecInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public CallRecInstruction copy() {
        return new CallRecInstruction(this, astContext, marker);
    }

    public CallRecInstruction withMarker(String marker) {
        return new CallRecInstruction(this, astContext, marker);
    }

    @Override
    public CallRecInstruction withContext(AstContext astContext) {
        return new CallRecInstruction(this, astContext, marker);
    }

    public final LogicVariable getStack() {
        return (LogicVariable) getArg(0);
    }

    public final LogicLabel getCallAddr() {
        return (LogicLabel) getArg(1);
    }

    public final LogicLabel getRetAddr() {
        return (LogicLabel) getArg(2);
    }

    public final List<LogicLabel> getAddresses() {
        return List.of(getCallAddr(), getRetAddr());
    }
}
