package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.SideEffects;
import info.teksol.mc.mindcode.logic.instructions.WriteArrInstruction;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.BiFunction;

@NullMarked
public class WriteInlinedArrayConstructor extends RegularInlinedArrayConstructor {
    private final WriteArrInstruction instruction;

    public WriteInlinedArrayConstructor(ArrayConstructorContext context, WriteArrInstruction instruction) {
        super(context, instruction, LogicVariable.INVALID);
        this.instruction = instruction;
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
    public SideEffects createSideEffects() {
        return SideEffects.of(variables(instruction.getValue()), List.of(), arrayElements());
    }

    @Override
    protected LogicValue transferVariable() {
        return instruction.getValue();
    }

    @Override
    protected BiFunction<LocalContextfulInstructionsCreator, ValueStore, LogicValue> elementValueExtractor() {
        throw new MindcodeInternalError("Unsupported for " + getClass().getSimpleName());
    }
}
