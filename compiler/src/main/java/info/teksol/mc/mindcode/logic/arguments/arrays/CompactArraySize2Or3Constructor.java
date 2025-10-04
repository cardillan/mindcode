package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class CompactArraySize2Or3Constructor extends RegularArraySize2Or3Constructor {
    private final int arraySize;
    private final boolean useSelects;
    private final LogicVariable arrayElem;

    public CompactArraySize2Or3Constructor(ArrayAccessInstruction instruction) {
        super(instruction);

        NameCreator nameCreator = MindcodeCompiler.getContext().nameCreator();
        String baseName = arrayStore.getName();
        arraySize = arrayStore.getSize();
        if (arraySize != 2 && arraySize != 3) throw new MindcodeInternalError("Expected array of size 2 or 3");
        useSelects = processor.isSupported(Opcode.SELECT)
                     && profile.getOptimizationLevel(Optimization.ARRAY_OPTIMIZATION) == OptimizationLevel.EXPERIMENTAL;
        arrayElem = LogicVariable.arrayAccess(baseName, "*elem", nameCreator.arrayAccess(baseName, "elem"));
    }

    @Override
    public LogicVariable getElementNameVariable() {
        return arrayElem;
    }

    @Override
    public SideEffects createSideEffects(AccessType accessType) {
        return switch (accessType) {
            case READ -> SideEffects.of(arrayElements(), List.of(arrayElem), List.of());
            case WRITE -> SideEffects.of(List.of(), List.of(arrayElem), arrayElements());
        };
    }

    @Override
    public int getInstructionSize(AccessType accessType, @Nullable Map<String, Integer> sharedStructures) {
        if (useSelects) {
            return profile.getBoundaryChecks().getSize() + arraySize + 1 - (accessType == AccessType.READ ? 1 : 0);
        } else {
            return profile.getBoundaryChecks().getSize() + (arraySize == 2 ? 5 : 8);
        }
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables) {
        generateBoundsCheck(instruction.getAstContext(), consumer, instruction.getIndex(), 1 );

        AstContextType contextType = useSelects ? AstContextType.BODY :  AstContextType.IF;
        AstContext astContext = this.instruction.getAstContext().createChild(instruction.getAstContext().existingNode(),
                contextType, AstSubcontextType.BASIC);
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        if (!instruction.isCompactAccessTarget()) {
            if (useSelects) {
                createNameSelect(creator, arrayElem);
            } else {
                expandAccess(creator, element -> creator.createSet(arrayElem, element.getMlogVariableName()));
            }
        }

        LogicValue storageProcessor = arrayStore.isRemote() ? arrayStore.getProcessor() : LogicBuiltIn.THIS;
        switch (instruction) {
            case ReadArrInstruction rix -> creator.createRead(rix.getResult(),storageProcessor, arrayElem);
            case WriteArrInstruction wix -> creator.createWrite(wix.getValue(),storageProcessor, arrayElem);
            default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
        }
    }
}
