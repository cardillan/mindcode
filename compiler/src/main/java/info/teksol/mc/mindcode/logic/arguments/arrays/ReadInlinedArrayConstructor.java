package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.ReadArrInstruction;
import info.teksol.mc.mindcode.logic.instructions.SideEffects;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.BiFunction;

@NullMarked
public class ReadInlinedArrayConstructor extends RegularInlinedArrayConstructor {
    private final ReadArrInstruction instruction;

    public ReadInlinedArrayConstructor(ArrayConstructorContext context, ReadArrInstruction instruction) {
        super(context, instruction, instruction.getResult());
        this.instruction = instruction;
    }

    @Override
    public boolean folded() {
        return !arrayStore.isRemote() && instruction.isArrayFolded();
    }

    @Override
    public boolean canFold() {
        return !arrayStore.isRemote() && !instruction.isArrayFolded();
    }

    @Override
    public SideEffects createSideEffects() {
        return SideEffects.of(arrayElements(), List.of(instruction.getResult()), List.of());
    }

    @Override
    protected LogicValue transferVariable() {
        return instruction.getResult();
    }

    @Override
    protected BiFunction<LocalContextfulInstructionsCreator, ValueStore, LogicValue> elementValueExtractor() {
        return regularValueExtractor();
    }
}
