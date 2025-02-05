package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class SensorInstruction extends BaseResultInstruction {

    SensorInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.SENSOR, args, params);
    }

    protected SensorInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public SensorInstruction copy() {
        return new SensorInstruction(this, astContext, sideEffects);
    }

    @Override
    public SensorInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new SensorInstruction(this, astContext, sideEffects);
    }

    @Override
    public SensorInstruction withSideEffects(SideEffects sideEffects) {
        return this.sideEffects == sideEffects ? this : new SensorInstruction(this, astContext, sideEffects);
    }

    @Override
    public SensorInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new SensorInstruction(astContext, List.of(result, getObject(), getProperty()), getArgumentTypes());
    }

    public final LogicValue getObject() {
        return (LogicValue) getArg(1);
    }

    public final LogicValue getProperty() {
        return (LogicValue) getArg(2);
    }
}
