package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicAddress;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class WriteInstruction extends BaseInstruction {

    WriteInstruction(List<LogicArgument> args, List<LogicParameter> params) {
        super(Opcode.WRITE, args, params);
    }

    protected WriteInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    public WriteInstruction withMarker(String marker) {
        return new WriteInstruction(this, marker);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }

    public final LogicAddress getAddress() {
        return (LogicAddress) getArg(0);
    }

    public final LogicVariable getMemory() {
        return (LogicVariable) getArg(1);
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(2);
    }
}
