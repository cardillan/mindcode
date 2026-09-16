package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.ContextFactory;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.StackTracker;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static info.teksol.mc.mindcode.logic.arguments.Operation.ADD;
import static info.teksol.mc.mindcode.logic.opcodes.Opcode.SET;
import static info.teksol.mc.mindcode.logic.opcodes.Opcode.WRITE;

@NullMarked
public class CallRecInstruction extends BaseInstruction implements CallingInstruction {
    private final @Nullable StackTracker stackTracker;

    CallRecInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.CALLREC, args, params);
        stackTracker =  ContextFactory.isMasterContextSet() ? ContextFactory.getMasterContext().stackTracker() : null;
    }

    protected CallRecInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
        stackTracker =  ContextFactory.isMasterContextSet() ? ContextFactory.getMasterContext().stackTracker() : null;
    }

    @Override
    public CallRecInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new CallRecInstruction(this, astContext);
    }

    public final LogicLabel getCallAddr() {
        return (LogicLabel) getArg(0);
    }

    public final LogicLabel getRetAddr() {
        return (LogicLabel) getArg(1);
    }

    public int getSharedSize(@Nullable Map<String, Integer> sharedStructures) {
        return externalStack()
                ? super.getSharedSize(sharedStructures) + (astContext.getGlobalProfile().isSymbolicLabels() ? 1 : 0)
                : 2;
    }

    @Override
    public void resolve(InstructionProcessor processor, Consumer<LogicInstruction> consumer) {
        LocalContextfulInstructionsCreator creator = creator(processor, consumer);

        assert stackTracker != null;
        LogicVariable stackPointer = stackTracker.getStackPointer();
        LogicVariable stackMemory = stackTracker.getStackMemory();
        boolean externalStack = stackTracker.externalStack();

        if (externalStack) {
            if (astContext.getGlobalProfile().isSymbolicLabels()) {
                LogicVariable returnAddress = processor.nextTemp();
                creator.createOp(ADD, returnAddress, LogicBuiltIn.COUNTER, LogicNumber.THREE);
                creator.createInstruction(WRITE, returnAddress, stackMemory, stackPointer);
            } else {
                creator.createInstruction(WRITE, getRetAddr(), stackMemory, stackPointer);
            }
            creator.createOp(ADD, stackPointer, stackPointer, LogicNumber.ONE);
            creator.createJumpUnconditional(getCallAddr()).copyComment(this);
        } else {
            MindcodeFunction function = getExistingFunction();
            creator.createInstruction(SET, function.getFnRetAddr(), getRetAddr());
            creator.createSet(LogicBuiltIn.COUNTER, function.getFnStackFrame());
        }
    }
}
