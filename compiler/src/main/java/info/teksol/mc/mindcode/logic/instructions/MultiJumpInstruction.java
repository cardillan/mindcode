package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class MultiJumpInstruction extends BaseInstruction implements MultiTargetInstruction {

    MultiJumpInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.MULTIJUMP, args, params);
    }

    protected MultiJumpInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public MultiJumpInstruction copy() {
        return new MultiJumpInstruction(this, astContext, sideEffects);
    }

    @Override
    public MultiJumpInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new MultiJumpInstruction(this, astContext, sideEffects);
    }

    @Override
    public MultiJumpInstruction withSideEffects(SideEffects sideEffects) {
        return this.sideEffects == sideEffects ? this : new MultiJumpInstruction(this, astContext, sideEffects);
    }

    @Override
    public boolean affectsControlFlow() {
        return true;
    }

    public final LogicAddress getTarget() {
        return (LogicAddress) getArg(0);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(1);
    }

    public final LogicNumber getOffset() {
        return (LogicNumber) getArg(2);
    }

    public final LogicLabel getMarker() {
        return (LogicLabel) getArg(3);
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }
}
