package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public class ReadArrInstruction extends BaseResultInstruction implements ArrayAccessInstruction {

    ReadArrInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.READARR, args, params);
    }

    protected ReadArrInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public ReadArrInstruction copy() {
        return new ReadArrInstruction(this, astContext, sideEffects);
    }

    @Override
    public ReadArrInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new ReadArrInstruction(this, astContext, sideEffects);
    }

    @Override
    public ReadArrInstruction withSideEffects(SideEffects sideEffects) {
        return Objects.equals(this.sideEffects, sideEffects) ? this : new ReadArrInstruction(this, astContext, sideEffects);
    }

    @Override
    public String getJumpTableId() {
        return getArray().getReadJumpTableId();
    }

    @Override
    public ReadArrInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new ReadArrInstruction(astContext, List.of(result, getArray(), getIndex()), getArgumentTypes());
    }

    @Override
    public SideEffects sideEffects() {
        return SideEffects.readsAndWrites(getArray().getElements(), List.of(getArray().readVal));
    }
}
