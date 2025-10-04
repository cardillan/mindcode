package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.SideEffects;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@NullMarked
public class CompactInlinedArrayConstructor extends AbstractArrayConstructor {
    private final LogicVariable arrayElem;
    private final LogicValue storageProcessor;

    public CompactInlinedArrayConstructor(ArrayAccessInstruction instruction) {
        super(instruction);

        NameCreator nameCreator = MindcodeCompiler.getContext().nameCreator();
        String baseName = arrayStore.getName();
        arrayElem = LogicVariable.arrayAccess(baseName, "*elem", nameCreator.arrayAccess(baseName, "elem"));
        storageProcessor = arrayStore.isRemote() ? arrayStore.getProcessor() : LogicBuiltIn.THIS;
    }

    @Override
    public LogicVariable getElementNameVariable() {
        return arrayElem;
    }

    @Override
    public SideEffects createSideEffects(AccessType accessType) {
        return switch (accessType) {
            case READ -> createReadSideEffects();
            case WRITE -> createWriteSideEffects();
        };
    }

    private SideEffects createReadSideEffects() {
        return SideEffects.of(arrayElements(), List.of(arrayElem), List.of());
    }

    private SideEffects createWriteSideEffects() {
        return SideEffects.of(List.of(), List.of(arrayElem), arrayElements());
    }

    @Override
    protected LogicValue transferVariable(AccessType accessType) {
        return arrayElem;
    }

    @Override
    public void generateJumpTable(AccessType accessType, Map<String, List<LogicInstruction>> jumpTables) {
        // No shared jump tables
    }

    public int getInstructionSize(AccessType accessType, @Nullable Map<String, Integer> sharedStructures) {
        if (instruction.isCompactAccessTarget()) return 1;

        int checkSize = profile.getBoundaryChecks().getSize();
        return checkSize + 3 + (2 * arrayStore.getSize() - 1);
    }

    protected BiConsumer<LocalContextfulInstructionsCreator, ValueStore> createArrayAccessCreator() {
        return (creator, element) -> creator.createSet(arrayElem, element.getMlogVariableName());
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables) {
        if (!instruction.isCompactAccessTarget()) {
            AstContext astContext = instruction.getAstContext().createSubcontext(AstSubcontextType.ARRAY, 1.0);
            LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

            LogicLabel finalLabel = processor.nextLabel();
            LogicLabel firstLabel = processor.nextLabel();
            LogicLabel marker = processor.nextMarker();
            LogicVariable tmp = creator.nextTemp();
            creator.createOp(Operation.MUL, tmp, instruction.getIndex(), LogicNumber.TWO);
            generateBoundsCheck(astContext, consumer, tmp, 2);

            creator.pushContext(AstContextType.JUMPS, AstSubcontextType.BASIC);
            creator.setSubcontextType(AstSubcontextType.ARRAY, 1.0);

            creator.createMultiJump(firstLabel, tmp, LogicNumber.ZERO, marker)
                    .setSideEffects(SideEffects.writes(List.of(arrayElem)));
            generateJumpTable(creator, firstLabel, marker, e -> e, createArrayAccessCreator(),
                    () -> creator.createJumpUnconditional(finalLabel), true);
            creator.createLabel(finalLabel);
            creator.popContext();

            arrayElements().forEach(creator::addForcedVariable);
        }

        LocalContextfulInstructionsCreator creator2 = new LocalContextfulInstructionsCreator(processor, instruction.getAstContext(), consumer);
        createCompactAccessInstruction(creator2, storageProcessor, arrayElem);
    }
}
