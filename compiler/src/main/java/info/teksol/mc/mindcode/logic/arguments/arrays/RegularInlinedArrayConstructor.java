package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.generation.variables.ArrayType;
import info.teksol.mc.mindcode.compiler.generation.variables.ElementsArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.WriteArrInstruction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@NullMarked
public abstract class RegularInlinedArrayConstructor extends InlinedArrayConstructor {

    public RegularInlinedArrayConstructor(ArrayConstructorContext context, ArrayAccessInstruction instruction, LogicVariable arrayElem) {
        super(context, instruction, arrayElem);
    }

    protected boolean hasInjection() {
        return !folded()
                && (arrayStore.getArrayType() == ArrayType.INTERNAL || arrayStore.getArrayType() == ArrayType.CONSTANT)
                && accessType == ArrayAccessInstruction.AccessType.READ
                && !instruction.getArrayInjections().isEmpty();
    }

    public int getInstructionSize(@Nullable Map<String, Integer> sharedStructures) {
        int checkSize = boundsCheckSize();
        int injectionSavings = hasInjection() ? instruction.getArrayInjections().stream().mapToInt(ix -> ix.getSharedSize(sharedStructures)).sum() : 0;
        return folded()
                ? checkSize + offsetInstructions() + inlinedTableSize() + 1 + 2 * flag(!useTextTables) - injectionSavings
                : checkSize + offsetInstructions() + inlinedTableSize() + 1 + flag(!useTextTables) - injectionSavings;
    }

    @Override
    public double getExecutionSteps() {
        int checkSteps = boundsCheckExecutionSteps();
        double injectionSavings = hasInjection() ? instruction.getArrayInjections().stream().mapToDouble(
                ix -> ix instanceof ArrayAccessInstruction a ? a.getExecutionSteps() : 1).sum() : 0;
        return checkSteps + offsetInstructions() + (useTextTables ? 3 : 4 + flag(folded())) - inlinedTableStepsSavings() - injectionSavings;
    }

    @Override
    protected BranchCreator branchCreator() {
        if (hasInjection()) {
            LogicVariable readVar = Objects.requireNonNull(instruction.getResult());
            Optional<LogicInstruction> update = instruction.getArrayInjections().stream()
                    .filter(ix -> !(ix instanceof ArrayAccessInstruction)).findFirst();
            Optional<WriteArrInstruction> write = instruction.getArrayInjections().stream()
                    .filter(WriteArrInstruction.class::isInstance).map(WriteArrInstruction.class::cast).findFirst();

            if (update.isEmpty() || write.isPresent() && !write.get().getValue().equals(readVar)) {
                assert write.isPresent();
                ElementsArrayStore writeArray = (ElementsArrayStore) write.get().getArray().getArrayStore();
                LogicVariable writeVar = (LogicVariable) write.get().getValue();
                if (update.isPresent()) {
                    return (creator, element, index) -> {
                        LogicValue readElem = (LogicValue) element.unwrap();
                        LogicValue writeElem = (LogicValue) writeArray.getElements().get(index).unwrap();
                        LogicInstruction ix = processor.replaceArgs(update.get(), Map.of(readVar, readElem, writeVar, writeElem)).setArrayInjectedInto(null);
                        creator.accept(ix);
                    };
                } else {
                    return (creator, element, index) -> {
                        LogicVariable readElem = (LogicVariable) element.unwrap();
                        LogicVariable writeElem = (LogicVariable) writeArray.getElements().get(index).unwrap();
                        creator.createSet(writeElem, readElem);
                    };
                }
            } else {
                assert update.isPresent();
                return (creator, element, index) -> {
                    LogicInstruction ix = processor.replaceAllArgs(update.get(), readVar, (LogicValue) element.unwrap()).setArrayInjectedInto(null);
                    creator.accept(ix);
                };
            }
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
