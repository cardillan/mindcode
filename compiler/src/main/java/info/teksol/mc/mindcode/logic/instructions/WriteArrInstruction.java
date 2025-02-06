package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public class WriteArrInstruction extends BaseInstruction implements ArrayAccessInstruction {

    WriteArrInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.WRITEARR, args, params);
    }

    protected WriteArrInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public WriteArrInstruction copy() {
        return new WriteArrInstruction(this, astContext, sideEffects);
    }

    @Override
    public WriteArrInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new WriteArrInstruction(this, astContext, sideEffects);
    }

    @Override
    public WriteArrInstruction withSideEffects(SideEffects sideEffects) {
        return Objects.equals(this.sideEffects, sideEffects) ? this : new WriteArrInstruction(this, astContext, sideEffects);
    }

    @Override
    public String getJumpTableId() {
        return getArray().getWriteJumpTableId();
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }

    @Override
    public SideEffects sideEffects() {
        return SideEffects.readsAndWrites(List.of(getArray().writeVal), getArray().getElements());
    }
}
