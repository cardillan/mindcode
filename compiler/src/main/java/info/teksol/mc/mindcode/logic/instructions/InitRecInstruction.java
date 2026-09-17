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
        // If the call was inlined, only process the arrays
        int size = getExistingFunction().getArrays().size();

        if (!isInlined().getBooleanValue() && stackTracker != null) {
            // Note: stack overflow for simple stacks is handled by a separate assertbounds instruction.
            if (stackTracker.largeStack()) {
                size += 4 + switch (getLocalProfile().getErrorReporting()) {
                    case NONE -> 0;
                    case ASSERT, MINIMAL -> 1;
                    case SIMPLE -> 2;
                    case DESCRIBED -> 3;
                };
            } else if (!stackTracker.externalStack()) {
                size++;
            }
        }

        return size;
    }

    @Override
    public void resolve(InstructionProcessor processor, Consumer<LogicInstruction> consumer) {
        LocalContextfulInstructionsCreator creator = creator(processor, consumer);

        assert stackTracker != null;
        LogicVariable stackPointer = stackTracker.getStackPointer();
        LogicVariable stackMemory = stackTracker.getStackMemory();

        MindcodeFunction function = getExistingFunction();
        if (!isInlined().getBooleanValue()) {
            if (stackTracker.largeStack()) {
                LogicNumber limit = LogicNumber.create(stackTracker.getAllocationEnd() - function.getCallSize());
                LogicLabel skipSwitch = processor.nextLabel();
                creator.createJump(skipSwitch, Condition.LESS_THAN_EQ, stackPointer, limit);
                creator.createWrite(stackPointer, stackMemory, LogicNumber.TWO);
                creator.createRead(stackMemory, stackMemory, LogicNumber.ZERO);
                creator.createSet(stackPointer, LogicNumber.THREE);
                switch (getLocalProfile().getErrorReporting()) {
                    case NONE -> {}
                    case ASSERT -> {
                        creator.createInstruction(Opcode.ASSERT_TYPE, LogicKeyword.create(AssertionDataType.memory), stackMemory,
                                LogicString.createRaw(String.format("%s: stack overflow error", astContext.sourcePosition().formatForMlog())));
                    }
                    case MINIMAL -> {
                        LogicLabel error = processor.nextLabel();
                        creator.createLabel(error);
                        creator.createJump(error, Condition.EQUAL, stackMemory, LogicNumber.ZERO);
                    }
                    case SIMPLE -> {
                        creator.createJump(skipSwitch, Condition.NOT_EQUAL, stackMemory, LogicNumber.ZERO);
                        creator.createStop();
                    }
                    case DESCRIBED -> {
                        creator.createJump(skipSwitch, Condition.NOT_EQUAL, stackMemory, LogicNumber.ZERO);
                        creator.createPrint(LogicString.createRaw(String.format("%s: stack overflow error", astContext.sourcePosition().formatForMlog())));
                        creator.createStop();
                    }
                }
                creator.createLabel(skipSwitch);
            } else if (!stackTracker.externalStack()) {
                creator.createOp(Operation.ADD, function.getFnStackFrame(), function.getFnStackFrame(), LogicNumber.create(function.getStackFrameSize()));
            }
        }

        function.getArrays().forEach(array -> {
            if (array.getArrayOffset() instanceof LogicVariable offset) {
                creator.createOp(ADD, offset, offset, LogicNumber.create(array.getSize()));
            }
        });
    }
}
