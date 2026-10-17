package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
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
        if (hasInjection()) {
            SideEffects sideEffects = SideEffects.reads(arrayElements());
            for (LogicInstruction ix : instruction.getArrayInjections()) {
                sideEffects = sideEffects.plusReadsAndWrites(
                        ix.getAllReads().filter(LogicVariable.class::isInstance).map(LogicVariable.class::cast).toList(),
                        ix.getAllWrites().filter(LogicVariable.class::isInstance).map(LogicVariable.class::cast).toList());
            }
            return sideEffects;
        } else {
            return SideEffects.of(arrayElements(), List.of(instruction.getResult()), List.of());
        }
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
