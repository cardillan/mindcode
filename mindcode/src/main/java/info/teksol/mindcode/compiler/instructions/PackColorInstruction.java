package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PackColorInstruction extends BaseInstruction implements LogicResultInstruction {
    PackColorInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.PACKCOLOR, args, params);
    }

    protected PackColorInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    @Override
    public PackColorInstruction copy() {
        return new PackColorInstruction(this, marker);
    }
    public PackColorInstruction withMarker(String marker) {
        return new PackColorInstruction(this, marker);
    }

    public SensorInstruction withResult(LogicVariable result) {
        return new SensorInstruction(getAstContext(), List.of(result, getR(), getG(), getB(), getA()), getParams()).withMarker(marker);
    }

    @Override
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
