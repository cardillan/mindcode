package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class CallRecInstruction extends BaseInstruction implements CallingInstruction {

    CallRecInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.CALLREC, args, params);
    }

    protected CallRecInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public CallRecInstruction copy() {
        return new CallRecInstruction(this, astContext, sideEffects);
    }

    @Override
    public CallRecInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new CallRecInstruction(this, astContext, sideEffects);
    }

    @Override
    public CallRecInstruction withSideEffects(SideEffects sideEffects) {
        return this.sideEffects == sideEffects ? this : new CallRecInstruction(this, astContext, sideEffects);
    }

    public final LogicVariable getStack() {
        return (LogicVariable) getArg(0);
    }

    public final LogicLabel getCallAddr() {
        return (LogicLabel) getArg(1);
    }

    public final LogicLabel getRetAddr() {
        return (LogicLabel) getArg(2);
    }

    public final List<LogicLabel> getAddresses() {
        return List.of(getCallAddr(), getRetAddr());
    }
}
