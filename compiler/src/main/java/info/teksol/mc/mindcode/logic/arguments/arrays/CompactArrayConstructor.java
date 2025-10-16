package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@NullMarked
public class CompactArrayConstructor extends AbstractArrayConstructor {
    private final LogicVariable arrayInd;
    private final LogicVariable arrayRet;
    private final LogicVariable arrayElem;

    public CompactArrayConstructor(ArrayAccessInstruction instruction) {
        super(instruction);

        NameCreator nameCreator = MindcodeCompiler.getContext().nameCreator();
        String baseName = arrayStore.getName();
        arrayInd = LogicVariable.arrayAccess(baseName, "*ind", nameCreator.arrayAccess(baseName, "ind"));
        arrayRet = LogicVariable.arrayReturn(baseName, "*ret", nameCreator.arrayAccess(baseName, "ret"));
        arrayElem = LogicVariable.arrayAccess(baseName, "*elem", nameCreator.arrayAccess(baseName, "elem"));

        instruction.setIndirectVariables(arrayElements());
    }

    public int getInstructionSize(@Nullable Map<String, Integer> sharedStructures) {
        computeSharedJumpTableSize(sharedStructures);
        return instruction.isCompactAccessTarget()
                ? 1
                : profile.getBoundaryChecks().getSize() + 4 + flag(folded() && !profile.isSymbolicLabels());
    }

    @Override
    public double getExecutionSteps() {
        return instruction.isCompactAccessTarget()
                ? 1
                : profile.getBoundaryChecks().getSize() + 6 + flag(folded()) + flag(profile.isSymbolicLabels());
    }

    @Override
    public LogicVariable getElementNameVariable() {
        return arrayElem;
    }

    @Override
    public SideEffects createSideEffects() {
        return switch (accessType) {
            case READ -> createReadSideEffects();
            case WRITE -> createWriteSideEffects();
        };
    }

    private SideEffects createReadSideEffects() {
        List<LogicVariable> reads = folded() || profile.isSymbolicLabels() ? arrayElementsPlus(arrayInd) : arrayElements();
        return SideEffects.of(reads, List.of(arrayElem), List.of());
    }

    private SideEffects createWriteSideEffects() {
        List<LogicVariable> reads = folded() || profile.isSymbolicLabels() ? List.of(arrayInd) : List.of();
        return SideEffects.of(reads, List.of(arrayElem), arrayElements());
    }

    private SideEffects createCallSideEffects() {
        List<LogicVariable> reads = folded() || profile.isSymbolicLabels() ? List.of(arrayInd) : List.of();
        return SideEffects.of(reads, List.of(arrayElem), List.of());
    }

    @Override
    protected LogicVariable transferVariable() {
        return arrayElem;
    }

    public String getJumpTableId() {
        return arrayStore.getName() + (folded() ? "-fe" : "-e");
    }

    @Override
    public void generateJumpTable(Map<String, List<LogicInstruction>> jumpTables) {
        jumpTables.computeIfAbsent(getJumpTableId(), this::buildJumpTable);
    }

    protected BiConsumer<LocalContextfulInstructionsCreator, ValueStore> createArrayAccessCreator() {
        return (creator, element) -> creator.createSet(arrayElem, element.getMlogVariableName());
    }

    private List<LogicInstruction> buildJumpTable(String jumpTableId) {
        boolean folded = folded();
        List<LogicInstruction> result = new ArrayList<>();

        AstContext astContext = MindcodeCompiler.getContext().getRootAstContext()
                .createSubcontext(AstContextType.JUMPS, AstSubcontextType.BASIC, 1.0);
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, result::add);

        creator.createEnd();
        LogicLabel firstLabel = processor.nextLabel();
        LogicLabel marker = processor.nextMarker();

        if (profile.isSymbolicLabels()) {
            LogicLabel startLabel = processor.nextLabel();
            creator.createLabel(startLabel).setMarker(startLabel);
            LogicVariable jumpValue = folded ? processor.nextTemp() : arrayInd;
            if (folded) {
                LogicNumber modulo = LogicNumber.create(roundUpToEven(arrayStore.getSize()));
                creator.createOp(Operation.MOD, jumpValue, arrayInd, modulo);
            }
            creator.createMultiJump(firstLabel, jumpValue,LogicNumber.ZERO, marker);
        }

        Runnable createExit = () -> creator.createReturn(arrayRet);
        if (folded) {
            LogicNumber limit = LogicNumber.create(roundUpToEven(arrayStore.getSize()));
            generateFoldedJumpTable(creator, firstLabel, marker, ValueStore::getMlogVariableName,
                    arrayInd, limit, arrayElem, createExit, false);
        } else {
            generateJumpTable(creator, firstLabel, marker, e -> e, createArrayAccessCreator(), createExit, false);
        }
        return result;
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables) {
        if (profile.isSymbolicLabels()) {
            expandInstructionSymbolicLabels(consumer, jumpTables);
        } else {
            expandInstructionDirectAddress(consumer, jumpTables);
        }
    }

    private void expandInstructionSymbolicLabels(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables) {
        AstContext astContext = instruction.getAstContext().createSubcontext(AstSubcontextType.ARRAY, 1.0);
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        LogicValue storageProcessor = arrayStore.isRemote() ? arrayStore.getProcessor() : LogicBuiltIn.THIS;

        if (!instruction.isCompactAccessTarget()) {
            LogicLabel address = ((LabeledInstruction) jumpTables.get(getJumpTableId()).get(1)).getLabel();
            creator.createOp(Operation.MUL, arrayInd, instruction.getIndex(), LogicNumber.TWO);
            generateBoundsCheck(astContext, consumer, arrayInd, 2);
            creator.createCallStackless(address, arrayRet, LogicVariable.INVALID).setSideEffects(createCallSideEffects());
        }

        createCompactAccessInstruction(creator, storageProcessor, arrayElem);
    }

    private void expandInstructionDirectAddress(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables) {
        AstContext astContext = instruction.getAstContext();
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        LogicValue storageProcessor = arrayStore.isRemote() ? arrayStore.getProcessor() : LogicBuiltIn.THIS;

        if (!instruction.isCompactAccessTarget()) {
            LogicLabel marker = Objects.requireNonNull(jumpTables.get(getJumpTableId()).get(1).getMarker());
            LogicLabel marker2 = processor.nextMarker();
            LogicLabel target = ((LabeledInstruction) jumpTables.get(getJumpTableId()).get(1)).getLabel();
            LogicLabel returnLabel = processor.nextLabel();

            creator.createSetAddress(arrayRet, returnLabel).setHoistId(marker2);
            LogicVariable index = folded() ? arrayInd : processor.nextTemp(); //Symbolic labels not handled here
            creator.createOp(Operation.MUL, index, instruction.getIndex(), LogicNumber.TWO);
            generateBoundsCheck(astContext, consumer, index, 2);
            LogicVariable branch = folded() ? creator.nextTemp() : index;
            if (folded()) {
                LogicNumber modulo = LogicNumber.create(roundUpToEven(arrayStore.getSize()));
                creator.createOp(Operation.MOD, branch, index, modulo);
            }
            creator.createMultiCall(target, branch, marker).setSideEffects(createCallSideEffects()).setHoistId(marker2);
            creator.createLabel(returnLabel);
        }

        createCompactAccessInstruction(creator, storageProcessor, arrayElem);
    }
}
