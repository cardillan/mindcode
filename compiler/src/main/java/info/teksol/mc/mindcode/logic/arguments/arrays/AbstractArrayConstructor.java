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
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction.AccessType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.RuntimeChecks;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public abstract class AbstractArrayConstructor implements ArrayConstructor {
    protected final InstructionProcessor processor;
    protected final CompilerProfile profile;
    protected final AccessType accessType;
    protected final ArrayAccessInstruction instruction;
    protected final ArrayStore arrayStore;
    protected final boolean useTextTables;

    public AbstractArrayConstructor(ArrayAccessInstruction instruction) {
        this.processor = MindcodeCompiler.getContext().instructionProcessor();
        this.profile = instruction.getAstContext().getCompilerProfile();
        this.accessType = instruction.getAccessType();
        this.instruction = instruction;
        this.arrayStore = instruction.getArray().getArrayStore();
        this.useTextTables = instruction.getLocalProfile().useTextJumpTables();

        instruction.resetIndirectVariables();
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

    protected List<LogicVariable> variables(@Nullable LogicArgument... args) {
        return Stream.of(args).filter(LogicVariable.class::isInstance).map(LogicVariable.class::cast).toList();
    }

    protected LogicValue transferVariable() {
        return LogicVariable.INVALID;
    }

    @Override
    public String getJumpTableId() {
        return "";
    }

    protected void computeSharedJumpTableSize(@Nullable Map<String, Integer> sharedStructures) {
        if (sharedStructures != null) {
            String key = arrayStore.getName() + getJumpTableId();
            sharedStructures.computeIfAbsent(key, _ -> tableSize());
        }
    }

    protected BiConsumer<LocalContextfulInstructionsCreator, ValueStore> createArrayAccessCreator() {
        return switch (accessType) {
            case READ -> (creator, element) -> element.readValue(creator, (LogicVariable) transferVariable());
            case WRITE -> (creator, element) -> element.setValue(creator, transferVariable());
        };
    }

    protected void generateJumpTable(LocalContextfulInstructionsCreator creator, LogicLabel firstLabel, LogicLabel marker,
            Function<ValueStore, ValueStore> arrayElementProcessor, BiConsumer<LocalContextfulInstructionsCreator, ValueStore> arrayAccessCreator,
            Runnable createExit, boolean skipLastExit, List<LogicLabel> branchLabels) {
        LogicLabel nextLabel = firstLabel;

        List<ValueStore> elements = arrayStore.getElements();
        for (int i = 0; i < elements.size(); i++) {
            ValueStore arrayElement = elements.get(i);
            ValueStore element = arrayElementProcessor.apply(arrayElement);
            creator.createMultiLabel(nextLabel, marker).setJumpTarget(useTextTables);
            branchLabels.add(nextLabel);
            arrayAccessCreator.accept(creator, element);
            if (i < elements.size() - 1) {
                createExit.run();
                nextLabel = processor.nextLabel();      // We'll waste one label. Meh.
            } else if (!skipLastExit) {
                createExit.run();
            }
        }
    }

    protected void generateFoldedJumpTable(LocalContextfulInstructionsCreator creator, LogicLabel firstLabel, LogicLabel marker,
            Function<ValueStore, LogicValue> arrayElementProcessor, LogicValue index, LogicValue limit, LogicVariable target,
            Runnable createExit, boolean inlined, boolean useTextTables, List<LogicLabel> branchLabels) {
        LogicLabel nextLabel = firstLabel;

        List<ValueStore> elements = arrayStore.getElements();
        int count = (elements.size() + 1) / 2;

        branchLabels.addAll(Collections.nCopies(elements.size(), firstLabel));
        for (int i = 0; i < count; i++) {
            creator.createMultiLabel(nextLabel, marker).setJumpTarget(useTextTables);
            LogicValue element1 = arrayElementProcessor.apply(elements.get(i));
            if (i + count < elements.size()) {
                LogicValue element2 = arrayElementProcessor.apply(elements.get(i + count));
                creator.createSelect(target, Condition.LESS_THAN, index, limit, element1, element2);
                branchLabels.set(i, nextLabel);
                branchLabels.set(i + count, nextLabel);
            } else {
                creator.createSet(target, element1);
                branchLabels.set(i, nextLabel);
            }

            if (i < count - 1) {
                createExit.run();
                nextLabel = processor.nextLabel();      // We'll waste one label. Meh.
            } else if (!inlined) {
                createExit.run();
            }
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
            case MINIMAL -> createMinimalRuntimeCheck(astContext, consumer, index, max);
            case SIMPLE, DESCRIBED -> createSimpleOrDescribedRuntimeCheck(astContext, consumer, index, max, errorMessage);
        }
    }

    private void createAssertRuntimeChecks(AstContext astContext, Consumer<LogicInstruction> consumer,
            LogicValue index, int max, int multiple, String errorMessage) {
        consumer.accept(processor.createInstruction(astContext, Opcode.ASSERT_BOUNDS, LogicKeyword.create("multiple"),
                LogicNumber.create(multiple), LogicNumber.ZERO, Condition.LESS_THAN_EQ, index, Condition.LESS_THAN_EQ, LogicNumber.create(max),
                LogicString.create(errorMessage)));
    }

    private void createMinimalRuntimeCheck(AstContext astContext, Consumer<LogicInstruction> consumer,
            LogicValue index, int max) {
        AstContext ctx = astContext.createChild(instruction.getAstContext().existingNode(),
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
        AstContext ctx = astContext.createChild(instruction.getAstContext().existingNode(),
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

    protected void createCompactAccessInstruction(LocalContextfulInstructionsCreator creator, LogicValue storageProcessor,
            LogicVariable arrayElem) {
        switch (instruction) {
            case ReadArrInstruction rix -> creator.createRead(rix.getResult(), storageProcessor, arrayElem)
                    .setSideEffects(SideEffects.reads(arrayElements()))
                    .setIndirectVariables(arrayElements());

            case WriteArrInstruction wix -> creator.createWrite(wix.getValue(), storageProcessor, arrayElem)
                    .setSideEffects(SideEffects.resets(arrayElements()))
                    .setIndirectVariables(arrayElements());

            default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
        }
    }

    protected boolean folded() {
        return instruction.isArrayFolded();
    }

    protected final int flag(boolean flag) {
        return flag ? 1 : 0;
    }

    protected final int roundUpToEven(int value) {
        return (value + 1) / 2 * 2;
    }

    protected final int tableSize() {
        return folded()
                ? roundUpToEven(arrayStore.getSize()) + 2 * flag(profile.isSymbolicLabels())
                : 2 * arrayStore.getSize() + flag(profile.isSymbolicLabels());
    }

    protected final int inlinedTableSize() {
        return (folded() ? roundUpToEven(arrayStore.getSize()) : 2 * arrayStore.getSize()) - 1;
    }

    protected final double inlinedTableStepsSavings() {
        // The last jump in an inlined jump table is eliminated
        // Hit twice as often for even-sized folded arrays
        return (folded() && arrayStore.getSize() % 2 == 0 ? 2.0 : 1.0) / arrayStore.getSize();
    }
}
