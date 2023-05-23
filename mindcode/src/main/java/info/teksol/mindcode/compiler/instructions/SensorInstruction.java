package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class SensorInstruction extends BaseInstruction {

    SensorInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.SENSOR, args, params);
    }

    protected SensorInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    @Override
    public SensorInstruction copy() {
        return new SensorInstruction(this, marker);
    }

    public SensorInstruction withMarker(String marker) {
        return new SensorInstruction(this, marker);
    }

    public final LogicVariable getResult() {
        return (LogicVariable) getArg(0);
    }

    public final LogicVariable getTarget() {
        return (LogicVariable) getArg(1);
    }

    public final LogicValue getProperty() {
        return (LogicValue) getArg(2);
    }
}
