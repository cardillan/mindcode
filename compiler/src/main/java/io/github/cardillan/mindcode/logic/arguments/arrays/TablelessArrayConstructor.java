package io.github.cardillan.mindcode.logic.arguments.arrays;

import io.github.cardillan.mindcode.compiler.MindcodeInternalError;
import io.github.cardillan.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mindcode.logic.arguments.LogicValue;
import io.github.cardillan.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mindcode.logic.instructions.ArrayAccessInstruction;
import io.github.cardillan.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiFunction;
import java.util.function.Function;

@NullMarked
public abstract class TablelessArrayConstructor extends AbstractArrayConstructor {

    public TablelessArrayConstructor(ArrayConstructorContext context, ArrayAccessInstruction instruction) {
        super(context, instruction);
    }

    @Override
    public boolean folded() {
        return false;
    }

    @Override
    public boolean canFold() {
        return false;
    }

    @Override
    protected LogicVariable transferVariable() {
        throw new MindcodeInternalError("Unsupported for " + getClass().getSimpleName());
    }

    @Override
    protected BranchCreator branchCreator() {
        throw new MindcodeInternalError("Unsupported for " + getClass().getSimpleName());
    }

    @Override
    protected Function<ValueStore, ValueStore> elementValueStoreExtractor() {
        throw new MindcodeInternalError("Unsupported for " + getClass().getSimpleName());
    }

    @Override
    protected BiFunction<LocalContextfulInstructionsCreator, ValueStore, LogicValue> elementValueExtractor() {
        throw new MindcodeInternalError("Unsupported for " + getClass().getSimpleName());
    }
}
