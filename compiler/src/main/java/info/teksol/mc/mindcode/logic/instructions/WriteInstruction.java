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
public class WriteInstruction extends BaseInstruction {

    WriteInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.WRITE, args, params);
    }

    protected WriteInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public WriteInstruction copy() {
        return new WriteInstruction(this, astContext, sideEffects);
    }

    @Override
    public WriteInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new WriteInstruction(this, astContext, sideEffects);
    }

    @Override
    public WriteInstruction withSideEffects(SideEffects sideEffects) {
        return this.sideEffects == sideEffects ? this : new WriteInstruction(this, astContext, sideEffects);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }

    public final LogicVariable getMemory() {
        return (LogicVariable) getArg(1);
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(2);
    }
}
