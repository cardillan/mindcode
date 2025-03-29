package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@NullMarked
public class MultiJumpInstruction extends BaseInstruction implements MultiTargetInstruction {

    MultiJumpInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.MULTIJUMP, args, params);
    }

    protected MultiJumpInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public MultiJumpInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new MultiJumpInstruction(this, astContext);
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

    @Override
    public boolean endsCodePath() {
        return true;
    }

    public int getRealSize(@Nullable Map<String, Integer> sharedStructures) {
        return super.getRealSize(sharedStructures) +
                (astContext.getProfile().isSymbolicLabels()
                        && getTarget() instanceof LogicLabel
                        && getOffset().getIntValue() != 0 ? 1 : 0);
    }
}
