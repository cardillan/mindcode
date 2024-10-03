package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

import java.util.List;

public class SensorInstruction extends BaseResultInstruction {

    SensorInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.SENSOR, args, params);
    }

    protected SensorInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public SensorInstruction copy() {
        return new SensorInstruction(this, astContext);
    }

    @Override
    public SensorInstruction withContext(AstContext astContext) {
        return new SensorInstruction(this, astContext);
    }

    @Override
    public SensorInstruction withResult(LogicVariable result) {
        return new SensorInstruction(astContext, List.of(result, getObject(), getProperty()), getParams());
    }

    public final LogicValue getObject() {
        return (LogicValue) getArg(1);
    }

    public final LogicValue getProperty() {
        return (LogicValue) getArg(2);
    }
}
