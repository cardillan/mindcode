package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class ReadInstruction extends BaseInstruction {

    ReadInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.READ, args, params);
    }

    @Override
    public ReadInstruction copy() {
        return new ReadInstruction(this, marker);
    }

    protected ReadInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    public ReadInstruction withMarker(String marker) {
        return new ReadInstruction(this, marker);
    }

    public final LogicVariable getResult() {
        return (LogicVariable) getArg(0);
    }

    public final LogicVariable getMemory() {
        return (LogicVariable) getArg(1);
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(2);
    }
}
