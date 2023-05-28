package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PackColorInstruction extends BaseInstruction implements LogicResultInstruction {
    PackColorInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params, String marker) {
        super(astContext, Opcode.PACKCOLOR, args, params, marker);
    }

    protected PackColorInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public PackColorInstruction copy() {
        return new PackColorInstruction(this, astContext, marker);
    }

    public PackColorInstruction withMarker(String marker) {
        return new PackColorInstruction(this, astContext, marker);
    }

    @Override
    public PackColorInstruction withContext(AstContext astContext) {
        return new PackColorInstruction(this, astContext, marker);
    }

    public SensorInstruction withResult(LogicVariable result) {
        return new SensorInstruction(astContext, List.of(result, getR(), getG(), getB(), getA()), getParams(), marker);
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
