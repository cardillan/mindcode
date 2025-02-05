package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class ReturnRecInstruction extends BaseInstruction {

    ReturnRecInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.RETURNREC, args, params);
    }

    protected ReturnRecInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public ReturnRecInstruction copy() {
        return new ReturnRecInstruction(this, astContext, sideEffects);
    }

    @Override
    public ReturnRecInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new ReturnRecInstruction(this, astContext, sideEffects);
    }

    @Override
    public ReturnRecInstruction withSideEffects(SideEffects sideEffects) {
        return this.sideEffects == sideEffects ? this : new ReturnRecInstruction(this, astContext, sideEffects);
    }

    @Override
    public boolean affectsControlFlow() {
        return true;
    }

    public final LogicVariable getStack() {
        return (LogicVariable) getArg(0);
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }
}
