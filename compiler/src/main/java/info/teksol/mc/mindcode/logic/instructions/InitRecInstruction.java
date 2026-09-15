package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.ContextFactory;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.generation.StackTracker;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicBoolean;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@NullMarked
public class InitRecInstruction extends BaseInstruction {
    private final @Nullable StackTracker stackTracker;

    InitRecInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.INITREC, args, params);
        stackTracker =  ContextFactory.isMasterContextSet() ? ContextFactory.getMasterContext().stackTracker() : null;
    }

    protected InitRecInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
        stackTracker =  ContextFactory.isMasterContextSet() ? ContextFactory.getMasterContext().stackTracker() : null;
    }

    public LogicBoolean isInlined() {
        return (LogicBoolean) getArg(0);
    }

    @Override
    public InitRecInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new InitRecInstruction(this, astContext);
    }

    public InitRecInstruction withInlined(LogicBoolean inlined) {
        assert getArgumentTypes() != null;
        return new InitRecInstruction(astContext, List.of(inlined), getArgumentTypes()).copyInfo(this);
    }

    @Override
    public int getSharedSize(@Nullable Map<String, Integer> sharedStructures) {
        int stackOperation = isInlined().getBooleanValue() || stackTracker == null ? 0 :
                stackTracker.largeStack() ? 4 : stackTracker.externalStack() ? 0 : 1;

        return stackOperation + getExistingFunction().getArrays().size();
    }
}
