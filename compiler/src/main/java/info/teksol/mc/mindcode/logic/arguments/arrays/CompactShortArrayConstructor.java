package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.compiler.postprocess.JumpTable;
import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.SideEffects;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class CompactShortArrayConstructor extends RegularShortArrayConstructor {
    private final int arraySize;
    private final boolean useSelects;
    private final LogicVariable arrayElem;

    public CompactShortArrayConstructor(ArrayAccessInstruction instruction) {
        super(instruction);

        NameCreator nameCreator = MindcodeCompiler.getContext().nameCreator();
        String baseName = arrayStore.getName();
        arraySize = arrayStore.getSize();
        useSelects = processor.isSupported(Opcode.SELECT);
        arrayElem = LogicVariable.arrayAccess(baseName, "*elem", nameCreator.arrayAccess(baseName, "elem"));

        int limit = useSelects ? 4 : 3;
        if (arraySize < 2 || arraySize > limit) throw new MindcodeInternalError("Expected array of size 2 to " + limit);

        instruction.setIndirectVariables(arrayElements());
    }

    @Override
    public int getInstructionSize(@Nullable Map<String, Integer> sharedStructures) {
        if (useSelects) {
            // It's always a "read" select list, plus one instruction for actual variable access
            return profile.getBoundaryChecks().getSize() + (instruction.isCompactAccessTarget() ? 1 : arraySize);
        } else {
            return profile.getBoundaryChecks().getSize() + (arraySize == 2 ? 5 : 8);
        }
    }

    @Override
    public double getExecutionSteps() {
        if (useSelects) {
            return profile.getBoundaryChecks().getExecutionSteps() + (instruction.isCompactAccessTarget() ? 1 : arraySize);
        } else {
            return profile.getBoundaryChecks().getSize() + (arraySize == 2 ? 3.5 : (4 + 5 + 4) / 3.0);
        }
    }

    @Override
    public LogicVariable getElementNameVariable() {
        return arrayElem;
    }

    @Override
    public SideEffects createSideEffects() {
        return switch (accessType) {
            case READ -> SideEffects.of(arrayElements(), List.of(arrayElem), List.of());
            case WRITE -> SideEffects.of(List.of(), List.of(arrayElem), arrayElements());
        };
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, JumpTable> jumpTables) {
        LocalContextfulInstructionsCreator creator = prepareExpansion(consumer);

        if (!instruction.isCompactAccessTarget()) {
            if (useSelects) {
                createNameSelect(creator, arrayElem);
            } else {
                expandAccess(creator, element -> creator.createSet(arrayElem, element.getMlogVariableName()));
            }
        }

        LogicValue storageProcessor = arrayStore.isRemote() ? arrayStore.getProcessor() : LogicBuiltIn.THIS;
        createCompactAccessInstruction(creator, storageProcessor, arrayElem);
    }
}
