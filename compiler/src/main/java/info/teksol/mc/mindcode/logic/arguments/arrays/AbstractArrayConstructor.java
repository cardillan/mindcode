package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.RuntimeChecks;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    protected List<LogicVariable> arrayElements() {
        return arrayStore.getElements().stream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .toList();
    }

    protected List<LogicVariable> arrayElementsPlus(@Nullable LogicVariable... variables) {
        ArrayList<LogicVariable> result = arrayStore.getElements().stream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .collect(Collectors.toCollection(ArrayList::new));

        for (LogicVariable variable : variables) {
            if (variable != null) result.add(variable);
        }

        return result;
    }

    protected List<LogicVariable> arrayElementsConcat(Stream<? extends LogicArgument> variables) {
        return Stream.concat(variables,
                        arrayStore.getElements().stream())
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .toList();
    }

    protected List<LogicVariable> variables(LogicArgument... args) {
        return Stream.of(args).filter(LogicVariable.class::isInstance).map(LogicVariable.class::cast).toList();
    }

    protected LogicValue transferVariable(AccessType accessType) {
        return LogicVariable.INVALID;
    }

    public String getJumpTableId(AccessType accessType) {
        return "";
    }

    protected void generateJumpTable(LocalContextfulInstructionsCreator creator, LogicLabel firstLabel, LogicLabel marker,
            Function<ValueStore, ValueStore> arrayElementProcessor, Runnable createExit) {
        LogicLabel nextLabel = firstLabel;

        for (ValueStore arrayElement : arrayStore.getElements()) {
            ValueStore element = arrayElementProcessor.apply(arrayElement);
            creator.createMultiLabel(nextLabel, marker);
            switch (instruction) {
                case ReadArrInstruction rix -> element.readValue(creator, (LogicVariable) transferVariable(AccessType.READ));
                case WriteArrInstruction wix -> element.setValue(creator, transferVariable(AccessType.WRITE));
                default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
            }
            createExit.run();
            nextLabel = processor.nextLabel();      // We'll waste one label. Meh.
        }
    }

    protected void generateBoundsCheck(AstContext astContext, Consumer<LogicInstruction> consumer, LogicValue index, int multiple) {
        RuntimeChecks boundaryChecks = profile.getBoundaryChecks();
        if (boundaryChecks == RuntimeChecks.NONE) return;

        int maxIndex = arrayStore.getSize() - 1;
        int max = maxIndex * multiple;
        String errorMessage = String.format("%s: index out of bounds (%d to %d)", instruction.sourcePosition().formatForMlog(), 0, maxIndex);

        switch (boundaryChecks) {
            case ASSERT -> createAssertRuntimeChecks(astContext, consumer, index, max, multiple, errorMessage);
            case MINIMAL -> createMinimalRuntimeCheck(astContext, consumer, index, max, errorMessage);
            case SIMPLE, DESCRIBED ->
                    createSimpleOrDescribedRuntimeCheck(astContext, consumer, index, max, errorMessage);
        }
    }

    private void createAssertRuntimeChecks(AstContext astContext, Consumer<LogicInstruction> consumer,
            LogicValue index, int max, int multiple, String errorMessage) {
        consumer.accept(processor.createInstruction(astContext, Opcode.ASSERT_BOUNDS, LogicKeyword.create("multiple"),
                LogicNumber.create(multiple), LogicNumber.ZERO, Condition.LESS_THAN_EQ, index, Condition.LESS_THAN_EQ, LogicNumber.create(max),
                LogicString.create(errorMessage)));
    }

    private void createMinimalRuntimeCheck(AstContext astContext, Consumer<LogicInstruction> consumer,
            LogicValue index, int max, String errorMessage) {
        AstContext ctx = astContext.createChild(profile, Objects.requireNonNull(instruction.getAstContext().node()),
                AstContextType.IF, AstSubcontextType.CONDITION);

        LogicLabel logicLabel1 = processor.nextLabel();
        consumer.accept(processor.createLabel(ctx, logicLabel1));
        consumer.accept(processor.createJump(ctx, logicLabel1, Condition.LESS_THAN, index, LogicNumber.ZERO));
        LogicLabel logicLabel2 = processor.nextLabel();
        consumer.accept(processor.createLabel(ctx, logicLabel2));
        consumer.accept(processor.createJump(ctx, logicLabel2, Condition.GREATER_THAN, index, LogicNumber.create(max)));
    }

    private void createSimpleOrDescribedRuntimeCheck(AstContext astContext, Consumer<LogicInstruction> consumer,
            LogicValue index, int max, String errorMessage) {
        AstContext ctx = astContext.createChild(profile, Objects.requireNonNull(instruction.getAstContext().node()),
                AstContextType.IF, AstSubcontextType.CONDITION);

        LogicLabel logicLabelStop = processor.nextLabel();
        LogicLabel logicLabelRun = processor.nextLabel();
        consumer.accept(processor.createJump(ctx, logicLabelStop, Condition.LESS_THAN, index, LogicNumber.ZERO));
        consumer.accept(processor.createJump(ctx, logicLabelRun, Condition.LESS_THAN_EQ, index, LogicNumber.create(max)));
        consumer.accept(processor.createLabel(ctx, logicLabelStop));
        if (profile.getBoundaryChecks() == RuntimeChecks.DESCRIBED) {
            consumer.accept(processor.createPrint(ctx, LogicString.create(errorMessage)));
        }
        consumer.accept(processor.createStop(ctx));
        consumer.accept(processor.createLabel(ctx, logicLabelRun));
    }
}
