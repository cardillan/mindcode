package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiConsumer;
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
    protected BiConsumer<LocalContextfulInstructionsCreator, ValueStore> branchCreator() {
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
