package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
public abstract class RegularInlinedArrayConstructor extends InlinedArrayConstructor {

    public RegularInlinedArrayConstructor(ArrayConstructorContext context, ArrayAccessInstruction instruction, LogicVariable arrayElem) {
        super(context, instruction, arrayElem);
    }

    public int getInstructionSize(@Nullable Map<String, Integer> sharedStructures) {
        int checkSize = boundsCheckSize();
        return folded()
                ? checkSize + offsetInstructions() + inlinedTableSize() + 1 + 2 * flag(!useTextTables)
                : checkSize + offsetInstructions() + inlinedTableSize() + 1 + flag(!useTextTables);
    }

    protected boolean hasInjection() {
        return !folded() && instruction.getArrayInjection().isPresent() && accessType == ArrayAccessInstruction.AccessType.READ
                && arrayStore.getElements().stream().allMatch(e -> e.unwrap() instanceof LogicValue);
    }

    @Override
    public double getExecutionSteps() {
        int checkSteps = boundsCheckExecutionSteps();
        int injectionSavings = hasInjection() ? 1 : 0;
        return checkSteps + offsetInstructions() + (useTextTables ? 3 : 4 + flag(folded())) - inlinedTableStepsSavings() - injectionSavings;
    }

    @Override
    protected BiConsumer<LocalContextfulInstructionsCreator, ValueStore> branchCreator() {
        if (hasInjection()) {
            LogicVariable result = Objects.requireNonNull(instruction.getResult());
            LogicInstruction original = instruction.getArrayInjection().orElseThrow();
            original.setArrayInjected(true);
            return (creator, element) -> {
                LogicInstruction ix = processor.replaceAllArgs(original, result, (LogicValue) element.unwrap()).setArrayInjected(false);
                creator.accept(ix);
            };
        } else {
            return regularBranchCreator();
        }
    }

    @Override
    protected Function<ValueStore, ValueStore> elementValueStoreExtractor() {
        return e -> e;
    }

    @Override
    protected void finishTableCall(LocalContextfulInstructionsCreator creator) {
        // Do nothing
    }
}
