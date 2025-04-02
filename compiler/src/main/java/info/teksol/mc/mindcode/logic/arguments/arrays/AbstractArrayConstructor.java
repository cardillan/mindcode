package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.RuntimeChecks;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.function.Consumer;

@NullMarked
public abstract class AbstractArrayConstructor implements ArrayConstructor {
    protected final InstructionProcessor processor;
    protected final CompilerProfile profile;
    protected final ArrayAccessInstruction instruction;
    protected final ArrayStore arrayStore;

    public AbstractArrayConstructor(ArrayAccessInstruction instruction) {
        this.processor = MindcodeCompiler.getContext().instructionProcessor();
        this.profile = instruction.getAstContext().getProfile();
        this.instruction = instruction;
        this.arrayStore = instruction.getArray().getArrayStore();
    }

    protected void generateBoundsCheck(Consumer<LogicInstruction> consumer, LogicVariable index) {
        RuntimeChecks boundaryChecks = profile.getBoundaryChecks();
        if (boundaryChecks == RuntimeChecks.NONE) return;

        AstContext astContext = instruction.getAstContext();
        int maxIndex = arrayStore.getSize() - 1;
        LogicNumber max = LogicNumber.create(maxIndex * 2L);
        String errorMessage = String.format("%s: index out of bounds (%d to %d)", instruction.sourcePosition().formatForMlog(), 0, maxIndex);

        switch (boundaryChecks) {
            case ASSERT -> createAssertRuntimeChecks(consumer, index, max, errorMessage);
            case MINIMAL -> createMinimalRuntimeCheck(consumer, index, max, errorMessage);
            case SIMPLE, DESCRIBED -> createSimpleOrDescribedRuntimeCheck(consumer, index, max, errorMessage);
        }
    }

    private void createAssertRuntimeChecks(Consumer<LogicInstruction> consumer, LogicVariable index, LogicNumber max, String errorMessage) {
        consumer.accept(processor.createInstruction(instruction.getAstContext(), Opcode.ASSERT_BOUNDS, LogicKeyword.create("multiple"),
                LogicNumber.TWO, LogicNumber.ZERO, Condition.LESS_THAN_EQ, index, Condition.LESS_THAN_EQ, max, LogicString.create(errorMessage)));
    }

    private void createMinimalRuntimeCheck(Consumer<LogicInstruction> consumer, LogicVariable index, LogicNumber max, String errorMessage) {
        AstContext ctx = instruction.getAstContext().createChild(profile, Objects.requireNonNull(instruction.getAstContext().node()),
                AstContextType.IF, AstSubcontextType.CONDITION);

        LogicLabel logicLabel1 = processor.nextLabel();
        consumer.accept(processor.createLabel(ctx, logicLabel1));
        consumer.accept(processor.createJump(ctx, logicLabel1, Condition.LESS_THAN, index, LogicNumber.ZERO));
        LogicLabel logicLabel2 = processor.nextLabel();
        consumer.accept(processor.createLabel(ctx, logicLabel2));
        consumer.accept(processor.createJump(ctx, logicLabel2, Condition.GREATER_THAN, index, max));
    }

    private void createSimpleOrDescribedRuntimeCheck(Consumer<LogicInstruction> consumer, LogicVariable index, LogicNumber max, String errorMessage) {
        AstContext ctx = instruction.getAstContext().createChild(profile, Objects.requireNonNull(instruction.getAstContext().node()),
                AstContextType.IF, AstSubcontextType.CONDITION);

        LogicLabel logicLabelStop = processor.nextLabel();
        LogicLabel logicLabelRun = processor.nextLabel();
        consumer.accept(processor.createJump(ctx, logicLabelStop, Condition.LESS_THAN, index, LogicNumber.ZERO));
        consumer.accept(processor.createJump(ctx, logicLabelRun, Condition.LESS_THAN_EQ, index, max));
        consumer.accept(processor.createLabel(ctx, logicLabelStop));
        if (profile.getBoundaryChecks() == RuntimeChecks.DESCRIBED) {
            consumer.accept(processor.createPrint(ctx, LogicString.create(errorMessage)));
        }
        consumer.accept(processor.createStop(ctx));
        consumer.accept(processor.createLabel(ctx, logicLabelRun));
    }
}
