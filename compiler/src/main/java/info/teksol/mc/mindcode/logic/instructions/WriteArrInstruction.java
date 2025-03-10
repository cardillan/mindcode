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
        return this.astContext == astContext ? this : new WriteArrInstruction(this, astContext, sideEffects);
    }

    @Override
    public WriteArrInstruction withSideEffects(SideEffects sideEffects) {
        return this.sideEffects == sideEffects ? this : new WriteArrInstruction(this, astContext, sideEffects);
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
        List<LogicVariable> elements = getArray().getElements().stream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .toList();

        return SideEffects.of(List.of(getArray().writeVal), List.of(), elements);
    }
}
