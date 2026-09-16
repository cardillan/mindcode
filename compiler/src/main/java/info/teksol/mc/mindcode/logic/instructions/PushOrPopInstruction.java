package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.ContextFactory;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.generation.StackTracker;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

// Push and pop are always processed at the same time
@NullMarked
public abstract class PushOrPopInstruction extends BaseInstruction {
    protected final @Nullable StackTracker stackTracker;
    protected final @Nullable NameCreator nameCreator;

    PushOrPopInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, opcode, args, params);
        stackTracker =  ContextFactory.isMasterContextSet() ? ContextFactory.getMasterContext().stackTracker() : null;
        nameCreator =  ContextFactory.isMasterContextSet() ? ContextFactory.getMasterContext().nameCreator() : null;
    }

    public PushOrPopInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
        stackTracker =  ContextFactory.isMasterContextSet() ? ContextFactory.getMasterContext().stackTracker() : null;
        nameCreator =  ContextFactory.isMasterContextSet() ? ContextFactory.getMasterContext().nameCreator() : null;
    }

    public LogicVariable getVariable() {
        return (LogicVariable) getArg(0);
    }
}
