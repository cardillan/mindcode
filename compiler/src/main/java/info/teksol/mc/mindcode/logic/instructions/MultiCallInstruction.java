package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicAddress;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class MultiCallInstruction extends BaseInstruction implements MultiTargetInstruction {

    MultiCallInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.MULTICALL, args, params);
    }

    protected MultiCallInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public MultiCallInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new MultiCallInstruction(this, astContext);
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

    @Override
    public boolean endsCodePath() {
        return true;
    }
}
