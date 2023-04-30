package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.*;

import java.util.List;

public class PackColorInstruction extends BaseInstruction {
    PackColorInstruction(List<LogicArgument> args, List<LogicParameter> params) {
        super(Opcode.PACKCOLOR, args, params);
    }

    protected PackColorInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    public PackColorInstruction withMarker(String marker) {
        return new PackColorInstruction(this, marker);
    }

    public final LogicVariable getResult() {
        return (LogicVariable) getArg(0);
    }

    public final LogicValue getR() {
        return (LogicValue) getArg(1);
    }

    public final LogicValue getG() {
        return (LogicValue) getArg(2);
    }

    public final LogicValue getB() {
        return (LogicValue) getArg(3);
    }

    public final LogicValue getA() {
        return (LogicValue) getArg(4);
    }
}
