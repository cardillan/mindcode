package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.*;

import java.util.List;

public class ReadInstruction extends BaseInstruction {

    ReadInstruction(List<LogicArgument> args, List<LogicParameter> params) {
        super(Opcode.READ, args, params);
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
