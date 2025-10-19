package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
public abstract class RegularInlinedArrayConstructor extends InlinedArrayConstructor {

    public RegularInlinedArrayConstructor(ArrayConstructorContext context, ArrayAccessInstruction instruction, LogicVariable arrayElem) {
        super(context, instruction, arrayElem);
    }

    public int getInstructionSize(@Nullable Map<String, Integer> sharedStructures) {
        int checkSize = profile.getBoundaryChecks().getSize();
        return folded()
                ? checkSize + inlinedTableSize() + 1 + 2 * flag(!useTextTables)
                : checkSize + inlinedTableSize() + 1 + flag(!useTextTables);
    }

    @Override
    public double getExecutionSteps() {
        int checkSteps = profile.getBoundaryChecks().getExecutionSteps();
        return checkSteps + (useTextTables ? 3 : 4 + flag(folded())) - inlinedTableStepsSavings();
    }

    @Override
    protected BiConsumer<LocalContextfulInstructionsCreator, ValueStore> branchCreator() {
        return regularBranchCreator();
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
