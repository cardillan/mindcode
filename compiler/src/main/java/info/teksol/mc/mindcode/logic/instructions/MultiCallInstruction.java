package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicAddress;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public class MultiCallInstruction extends BaseInstruction implements MultiTargetInstruction {

    MultiCallInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.MULTICALL, args, params);
    }

    protected MultiCallInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public MultiCallInstruction copy() {
        return new MultiCallInstruction(this, astContext, sideEffects);
    }

    @Override
    public MultiCallInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new MultiCallInstruction(this, astContext, sideEffects);
    }

    @Override
    public MultiCallInstruction withSideEffects(SideEffects sideEffects) {
        return Objects.equals(this.sideEffects, sideEffects) ? this : new MultiCallInstruction(this, astContext, sideEffects);
    }

    @Override
    public boolean affectsControlFlow() {
        return true;
    }

    public final LogicAddress getTarget() {
        return (LogicAddress) getArg(0);
    }

    public final LogicValue getOffset() {
        return (LogicValue) getArg(1);
    }

    public final LogicLabel getMarker() {
        return (LogicLabel) getArg(2);
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }
}
