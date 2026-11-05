package io.github.cardillan.mindcode.logic.arguments.arrays;

import io.github.cardillan.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mindcode.logic.arguments.LogicBuiltIn;
import io.github.cardillan.mindcode.logic.arguments.LogicValue;
import io.github.cardillan.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mindcode.logic.instructions.ArrayAccessInstruction;
import io.github.cardillan.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import io.github.cardillan.mindcode.logic.instructions.SideEffects;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@NullMarked
public class CompactInlinedArrayConstructor extends InlinedArrayConstructor {
    private final LogicValue storageProcessor;

    public CompactInlinedArrayConstructor(ArrayConstructorContext context, ArrayAccessInstruction instruction) {
        super(context, instruction, "elem");
        storageProcessor = arrayStore.isRemote() ? arrayStore.getProcessor() : LogicBuiltIn.THIS;
        instruction.setIndirectVariables(arrayElements());
    }

    @Override
    public boolean folded() {
        return instruction.isArrayFolded();
    }

    @Override
    public boolean canFold() {
        return !instruction.isArrayFolded();
    }

    public int getInstructionSize(@Nullable Map<String, Integer> sharedStructures) {
        if (skipCompactLookup()) return 1;

        int checkSize = boundsCheckSize();
        return folded()
                ? offsetInstructions() + checkSize + inlinedTableSize() + 4 - 2 * flag(useTextTables)
                : offsetInstructions() + checkSize + inlinedTableSize() + 3 - flag(useTextTables);
    }

    @Override
    public double getExecutionSteps() {
        if (skipCompactLookup()) return 1;

        int checkSteps = boundsCheckExecutionSteps();
        return checkSteps + offsetInstructions() + (useTextTables ? 4 : 5 + flag(folded())) - inlinedTableStepsSavings();
    }

    @Override
    public LogicVariable getElementNameVariable() {
        return arrayElem;
    }

    @Override
    public SideEffects createSideEffects() {
        return createNameBasedSideEffects();
    }

    @Override
    protected LogicValue transferVariable() {
        return arrayElem;
    }

    protected BranchCreator branchCreator() {
        return compactBranchCreator();
    }

    @Override
    protected Function<ValueStore, ValueStore> elementValueStoreExtractor() {
        return e -> e;
    }

    @Override
    protected BiFunction<LocalContextfulInstructionsCreator, ValueStore, LogicValue> elementValueExtractor() {
        return compactValueExtractor();
    }

    @Override
    protected void finishTableCall(LocalContextfulInstructionsCreator creator) {
        createCompactAccessInstruction(creator, storageProcessor, arrayElem);
    }
}
