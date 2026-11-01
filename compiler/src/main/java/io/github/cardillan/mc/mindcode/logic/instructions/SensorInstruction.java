package io.github.cardillan.mc.mindcode.logic.instructions;

import io.github.cardillan.mc.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicValue;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mc.mindcode.logic.opcodes.InstructionParameterType;
import io.github.cardillan.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class SensorInstruction extends BaseResultInstruction {

    SensorInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.SENSOR, args, params);
    }

    protected SensorInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public SensorInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new SensorInstruction(this, astContext);
    }

    @Override
    public SensorInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new SensorInstruction(astContext, List.of(result, getObject(), getProperty()), getArgumentTypes()).copyInfo(this);
    }

    public final LogicValue getObject() {
        return (LogicValue) getArg(1);
    }

    public final LogicValue getProperty() {
        return (LogicValue) getArg(2);
    }
}
