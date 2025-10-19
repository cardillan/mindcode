package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.SideEffects;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@NullMarked
public class CompactSharedArrayConstructor extends SharedArrayConstructor {
    private final LogicValue storageProcessor;

    public CompactSharedArrayConstructor(ArrayConstructorContext context, ArrayAccessInstruction instruction) {
        super(context, instruction, "ind", "ret", "elem");
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

    @Override
    public int getInstructionSize(@Nullable Map<String, Integer> sharedStructures) {
        if (skipCompactLookup()) return 1;

        computeSharedJumpTableSize(sharedStructures);
        int checkSize = profile.getBoundaryChecks().getSize();
        return useTextTables
                ? checkSize + 3 + flag(folded())
                : checkSize + 4 + flag(folded() && !profile.isSymbolicLabels());
    }

    @Override
    public double getExecutionSteps() {
        if (skipCompactLookup()) return 1;

        int checkSize = profile.getBoundaryChecks().getSize();
        return checkSize + 6 - flag(useTextTables) + flag(folded()) + flag(profile.isSymbolicLabels());
    }

    @Override
    public LogicVariable getElementNameVariable() {
        return arrayElem;
    }

    @Override
    public SideEffects createSideEffects() {
        return switch (accessType) {
            case READ -> createReadSideEffects();
            case WRITE -> createWriteSideEffects();
        };
    }

    private SideEffects createReadSideEffects() {
        List<LogicVariable> reads = folded() || profile.isSymbolicLabels() ? arrayElementsPlus(arrayInd) : arrayElements();
        return SideEffects.of(reads, List.of(arrayElem), List.of());
    }

    private SideEffects createWriteSideEffects() {
        List<LogicVariable> reads = folded() || profile.isSymbolicLabels() ? List.of(arrayInd) : List.of();
        return SideEffects.of(reads, List.of(arrayElem), arrayElements());
    }

    @Override
    protected SideEffects createCallSideEffects() {
        List<LogicVariable> reads = folded() || profile.isSymbolicLabels() ? List.of(arrayInd) : List.of();
        return SideEffects.of(reads, List.of(arrayElem), List.of());
    }

    @Override
    public String getJumpTableId() {
        if (skipCompactLookup()) return "";

        return arrayStore.getName() + (folded() ? "-fe" : "-e");
    }

    @Override
    protected BiConsumer<LocalContextfulInstructionsCreator, ValueStore> branchCreator() {
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
    protected void prepareTableCall(LocalContextfulInstructionsCreator creator) {
        // Do nothing
    }

    @Override
    protected void finishTableCall(LocalContextfulInstructionsCreator creator) {
        createCompactAccessInstruction(creator, storageProcessor, arrayElem);
    }
}
