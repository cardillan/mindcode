package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicAddress;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class WriteInstruction extends BaseInstruction {

    WriteInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params, String marker) {
        super(astContext, Opcode.WRITE, args, params, marker);
    }

    protected WriteInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public WriteInstruction copy() {
        return new WriteInstruction(this, astContext, marker);
    }

    public WriteInstruction withMarker(String marker) {
        return new WriteInstruction(this, astContext, marker);
    }

    @Override
    public WriteInstruction withContext(AstContext astContext) {
        return new WriteInstruction(this, astContext, marker);
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
