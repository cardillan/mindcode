package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class SensorInstruction extends BaseInstruction implements LogicResultInstruction {

    SensorInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params, String marker) {
        super(astContext, Opcode.SENSOR, args, params, marker);
    }

    protected SensorInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public SensorInstruction copy() {
        return new SensorInstruction(this, astContext, marker);
    }

    public SensorInstruction withMarker(String marker) {
        return new SensorInstruction(this, astContext, marker);
    }

    @Override
    public SensorInstruction withContext(AstContext astContext) {
        return new SensorInstruction(this, astContext, marker);
    }

    public SensorInstruction withResult(LogicVariable result) {
        return new SensorInstruction(astContext, List.of(result, getObject(), getProperty()), getParams(), marker);
    }

    public final LogicVariable getResult() {
        return (LogicVariable) getArg(0);
    }

    public final LogicVariable getObject() {
        return (LogicVariable) getArg(1);
    }

    public final LogicValue getProperty() {
        return (LogicValue) getArg(2);
    }
}
