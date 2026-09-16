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
import static info.teksol.mc.mindcode.logic.arguments.Operation.SUB;

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

    @Override
    public void resolve(InstructionProcessor processor, Consumer<LogicInstruction> consumer) {
        LocalContextfulInstructionsCreator creator = creator(processor, consumer);

        assert stackTracker != null;
        LogicVariable stackPointer = stackTracker.getStackPointer();
        LogicVariable stackMemory = stackTracker.getStackMemory();
        boolean externalStack = stackTracker.externalStack();

        MindcodeFunction function = getExistingFunction();
        function.getArrays().forEach(array -> {
            if (array.getArrayOffset() instanceof LogicVariable offset) {
                creator.createOp(SUB, offset, offset, LogicNumber.create(array.getSize()));
            }
        });
        if (externalStack) {
            if (stackTracker.largeStack()) {
                LogicLabel skipSwitch = processor.nextLabel();
                creator.createJump(skipSwitch, Condition.GREATER_THAN, stackPointer, LogicNumber.THREE);
                creator.createRead(stackMemory, stackMemory, LogicNumber.ONE);
                creator.createRead(stackPointer, stackMemory, LogicNumber.TWO);
                creator.createLabel(skipSwitch);
            }
            creator.createOp(SUB, stackPointer, stackPointer, LogicNumber.ONE);
            creator.createRead(LogicBuiltIn.COUNTER, stackMemory, stackPointer).copyComment(this);
        } else {
            creator.createOp(SUB, function.getFnStackFrame(), function.getFnStackFrame(), LogicNumber.create(function.getStackFrameSize()));
            creator.createOp(ADD, LogicBuiltIn.COUNTER, function.getFnStackFrame(), LogicNumber.create(function.getStackFrameSize() - function.getReturnOffset()));
        }
    }
}
