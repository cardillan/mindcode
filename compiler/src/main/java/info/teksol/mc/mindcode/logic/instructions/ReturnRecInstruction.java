package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.ContextFactory;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.generation.StackTracker;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@NullMarked
public class ReturnRecInstruction extends BaseInstruction {
    private final @Nullable StackTracker stackTracker;

    ReturnRecInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.RETURNREC, args, params);
        stackTracker =  ContextFactory.isMasterContextSet() ? ContextFactory.getMasterContext().stackTracker() : null;
    }

    protected ReturnRecInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
        stackTracker =  ContextFactory.isMasterContextSet() ? ContextFactory.getMasterContext().stackTracker() : null;
    }

    @Override
    public ReturnRecInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new ReturnRecInstruction(this, astContext);
    }

    @Override
    public boolean affectsControlFlow() {
        return true;
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }

    @Override
    public int getSharedSize(@Nullable Map<String, Integer> sharedStructures) {
        return getExistingFunction().getArrays().size() + 2 + (stackTracker != null && stackTracker.largeStack() ? 3 : 0);
    }
}
