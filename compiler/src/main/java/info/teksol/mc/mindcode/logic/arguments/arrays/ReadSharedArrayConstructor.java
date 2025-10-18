package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.ReadArrInstruction;
import info.teksol.mc.mindcode.logic.instructions.SideEffects;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.BiFunction;

@NullMarked
public class ReadSharedArrayConstructor extends RegularSharedArrayConstructor {
    private final ReadArrInstruction instruction;

    public ReadSharedArrayConstructor(ReadArrInstruction instruction) {
        super(instruction, "rind", "rret", "r");
        this.instruction = instruction;
    }

    @Override
    protected boolean folded() {
        return !arrayStore.isRemote() && instruction.isArrayFolded();
    }

    @Override
    public SideEffects createSideEffects() {
        LogicVariable sharedProc = shared ? proc : null;
        List<LogicVariable> reads = folded() || profile.isSymbolicLabels() ? arrayElementsPlus(arrayInd, sharedProc)
                : arrayElementsPlus(sharedProc);
        return SideEffects.of(reads, List.of(), List.of(arrayElem));
    }

    @Override
    public String getJumpTableId() {
        return arrayStore.getName() + (folded() ? "-fr" : "-r");
    }

    @Override
    protected BiFunction<LocalContextfulInstructionsCreator, ValueStore, LogicValue> elementValueExtractor() {
        return regularValueExtractor();
    }

    @Override
    protected void prepareTableCall(LocalContextfulInstructionsCreator creator) {
        if (shared) creator.createSet(proc, arrayStore.getProcessor());
    }

    @Override
    protected void finishTableCall(LocalContextfulInstructionsCreator creator) {
        creator.createSet(instruction.getResult(), arrayElem);
    }
}
