package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.postprocess.JumpTable;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.SideEffects;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public void generateJumpTable(Map<String, JumpTable> jumpTables) {
        String tableId = getJumpTableId();
        JumpTable jumpTable = jumpTables.get(tableId);
        if (jumpTable == null || useTextTables && !jumpTable.usesTextTable()) {
            jumpTables.put(tableId, buildJumpTable(tableId));
        }
    }

    protected BiConsumer<LocalContextfulInstructionsCreator, ValueStore> createArrayAccessCreator() {
        return (creator, element) -> creator.createSet(arrayElem, element.getMlogVariableName());
    }

    private JumpTable buildJumpTable(String tableId) {
        boolean folded = folded();
        List<LogicInstruction> instructions = new ArrayList<>();

        AstContext astContext = MindcodeCompiler.getContext().getRootAstContext()
                .createSubcontext(AstContextType.JUMPS, AstSubcontextType.BASIC, 1.0);
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, instructions::add);

        creator.createEnd();
        LogicLabel startLabel = processor.nextLabel();
        LogicLabel firstLabel = profile.isSymbolicLabels() ? processor.nextLabel() : startLabel;
        LogicLabel marker = processor.nextMarker();

        if (profile.isSymbolicLabels()) {
            creator.createLabel(startLabel).setMarker(startLabel);
            LogicVariable jumpValue = folded ? processor.nextTemp() : arrayInd;
            if (folded) {
                LogicNumber modulo = LogicNumber.create(roundUpToEven(arrayStore.getSize()));
                creator.createOp(Operation.MOD, jumpValue, arrayInd, modulo);
            }
            creator.createMultiJump(firstLabel, jumpValue, LogicNumber.ZERO, marker);
        }

        Runnable createExit = () -> creator.createReturn(arrayRet);
        List<LogicLabel> branchLabels = new ArrayList<>();
        if (folded) {
            LogicNumber limit = useTextTables
                    ? LogicNumber.create((arrayStore.getSize() + 1) / 2)
                    : LogicNumber.create(roundUpToEven(arrayStore.getSize()));
            generateFoldedJumpTable(creator, firstLabel, marker, ValueStore::getMlogVariableName,
                    arrayInd, limit, arrayElem, createExit, false, useTextTables, branchLabels);
        } else {
            generateJumpTable(creator, firstLabel, marker, e -> e, createArrayAccessCreator(), createExit,
                    false, branchLabels);
        }
        return new JumpTable(tableId, useTextTables, startLabel, marker, instructions, branchLabels);
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, JumpTable> jumpTables) {
        JumpTable jumpTable = jumpTables.get(getJumpTableId());
        if (useTextTables) {
            expandInstructionTextTable(consumer, jumpTable);
        } else if (profile.isSymbolicLabels()) {
            expandInstructionSymbolicLabels(consumer, jumpTable);
        } else {
            expandInstructionDirectAddress(consumer, jumpTable);
        }
    }

    private void expandInstructionSymbolicLabels(Consumer<LogicInstruction> consumer, JumpTable jumpTable) {
        AstContext astContext = instruction.getAstContext().createSubcontext(AstSubcontextType.ARRAY, 1.0);
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        if (!instruction.isCompactAccessTarget()) {
            LogicLabel address = jumpTable.label();
            creator.createOp(Operation.MUL, arrayInd, instruction.getIndex(), LogicNumber.TWO);
            generateBoundsCheck(astContext, consumer, arrayInd, 2);
            creator.createCallStackless(address, arrayRet, LogicVariable.INVALID).setSideEffects(createCallSideEffects());
        }

        LogicValue storageProcessor = arrayStore.isRemote() ? arrayStore.getProcessor() : LogicBuiltIn.THIS;
        createCompactAccessInstruction(creator, storageProcessor, arrayElem);
    }

    private void expandInstructionDirectAddress(Consumer<LogicInstruction> consumer, JumpTable jumpTable) {
        AstContext astContext = instruction.getAstContext();
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        if (!instruction.isCompactAccessTarget()) {
            LogicLabel marker2 = processor.nextMarker();
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
            creator.createMultiCall(jumpTable.label(), branch, jumpTable.marker())
                    .setSideEffects(createCallSideEffects()).setHoistId(marker2);
            creator.createLabel(returnLabel);
        }

        LogicValue storageProcessor = arrayStore.isRemote() ? arrayStore.getProcessor() : LogicBuiltIn.THIS;
        createCompactAccessInstruction(creator, storageProcessor, arrayElem);
    }

    private void expandInstructionTextTable(Consumer<LogicInstruction> consumer, JumpTable jumpTable) {
        AstContext astContext = instruction.getAstContext();
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        if (!instruction.isCompactAccessTarget()) {
            LogicLabel marker2 = processor.nextMarker();
            LogicLabel returnLabel = processor.nextLabel();
            creator.createSetAddress(arrayRet, returnLabel).setHoistId(marker2);
            generateBoundsCheck(astContext, consumer, instruction.getIndex(), 1);
            if (folded()) creator.createSet(arrayInd, instruction.getIndex());
            creator.createMultiCall(instruction.getIndex(), jumpTable.marker()).setSideEffects(createCallSideEffects())
                    .setHoistId(marker2).setJumpTable(jumpTable.branchLabels());
            creator.createLabel(returnLabel);
        }

        LogicValue storageProcessor = arrayStore.isRemote() ? arrayStore.getProcessor() : LogicBuiltIn.THIS;
        createCompactAccessInstruction(creator, storageProcessor, arrayElem);
    }
}
