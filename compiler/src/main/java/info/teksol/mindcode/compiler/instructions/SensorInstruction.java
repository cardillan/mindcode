package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.*;

import java.util.List;

public class SensorInstruction extends BaseInstruction {

    SensorInstruction(List<LogicArgument> args, List<LogicParameter> params) {
        super(Opcode.SENSOR, args, params);
    }

    protected SensorInstruction(BaseInstruction other, String marker) {
        super(other, marker);
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
