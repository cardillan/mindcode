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
public class SetInstruction extends BaseResultInstruction {

    SetInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.SET, args, params);
    }

    protected SetInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public SetInstruction copy() {
        return new SetInstruction(this, astContext, sideEffects);
    }

    @Override
    public SetInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new SetInstruction(this, astContext, sideEffects);
    }

    @Override
    public SetInstruction withSideEffects(SideEffects sideEffects) {
        return this.sideEffects == sideEffects ? this : new SetInstruction(this, astContext, sideEffects);
    }

    @Override
    public SetInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new SetInstruction(astContext, List.of(result, getValue()), getArgumentTypes());
    }

    public SetInstruction withValue(LogicValue value) {
        assert getArgumentTypes() != null;
        return new SetInstruction(astContext, List.of(getResult(), value), getArgumentTypes());
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(1);
    }

    @Override
    public boolean isUpdating() {
        // SET instruction is not self-modifying, even when it assigns the same value to self.
        return false;
    }
}
