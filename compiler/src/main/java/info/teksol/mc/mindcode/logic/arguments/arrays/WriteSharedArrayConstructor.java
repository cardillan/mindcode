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
public class WriteSharedArrayConstructor extends RegularSharedArrayConstructor {
    private final WriteArrInstruction instruction;

    public WriteSharedArrayConstructor(WriteArrInstruction instruction) {
        super(instruction, "wind", "wret", "w");
        this.instruction = instruction;
    }

    @Override
    protected boolean folded() {
        return false;
    }

    @Override
    public SideEffects createSideEffects() {
        LogicVariable sharedProc = shared ? proc : null;
        List<LogicVariable> reads = folded() || profile.isSymbolicLabels() ? variables(arrayInd, sharedProc)
                : variables(arrayElem, sharedProc);
        return SideEffects.of(reads, List.of(), arrayElements());
    }

    @Override
    public String getJumpTableId() {
        return arrayStore.getName() + "-w";
    }

    @Override
    protected BiFunction<LocalContextfulInstructionsCreator, ValueStore, LogicValue> elementValueExtractor() {
        throw new MindcodeInternalError("Unsupported for " + getClass().getSimpleName());
    }

    @Override
    protected void prepareTableCall(LocalContextfulInstructionsCreator creator) {
        if (shared) creator.createSet(proc, arrayStore.getProcessor());
        creator.createSet(arrayElem, instruction.getValue());
    }

    @Override
    protected void finishTableCall(LocalContextfulInstructionsCreator creator) {
        // Do nothing
    }
}
