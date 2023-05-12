package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class CallRecInstruction extends BaseInstruction {

    CallRecInstruction(List<LogicArgument> args, List<LogicParameter> params) {
        super(Opcode.CALLREC, args, params);
    }

    protected CallRecInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    public CallRecInstruction withMarker(String marker) {
        return new CallRecInstruction(this, marker);
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
