package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

@NullMarked
public class RegularArrayConstructor extends AbstractArrayConstructor {
    private final LogicVariable readInd;
    private final LogicVariable readVal;
    private final LogicVariable readRet;
    private final LogicVariable writeInd;
    private final LogicVariable writeVal;
    private final LogicVariable writeRet;

    public RegularArrayConstructor(ArrayAccessInstruction instruction) {
        super(instruction);

        String baseName = arrayStore.getName();
        readInd = LogicVariable.arrayIndex(baseName, "*rind");
        readVal = LogicVariable.arrayReadAccess(baseName);
        readRet = LogicVariable.arrayReturn(baseName, "*rret");
        writeInd = LogicVariable.arrayIndex(baseName, "*wind");
        writeVal = LogicVariable.arrayWriteAccess(baseName);
        writeRet = LogicVariable.arrayReturn(baseName, "*wret");
    }

    @Override
    public SideEffects createSideEffects(AccessType accessType) {
        return switch (accessType) {
            case READ -> createReadSideEffects();
            case WRITE -> createWriteSideEffects();
        };
    }

    private SideEffects createReadSideEffects() {
        List<LogicVariable> reads = arrayElementsConcat(Stream.of(readInd).filter(_ -> profile.isSymbolicLabels()));
        return SideEffects.of(reads, List.of(), List.of(readVal));
    }

    private SideEffects createWriteSideEffects() {
        List<LogicVariable> reads = profile.isSymbolicLabels() ? List.of(writeVal, writeInd) : List.of(writeVal);
        return SideEffects.of(reads, List.of(), arrayElements());
    }

    @Override
    protected LogicVariable transferVariable(AccessType accessType) {
        return switch (accessType) {
            case READ -> readVal;
            case WRITE -> writeVal;
        };
    }

    public String getJumpTableId(AccessType accessType) {
        return switch (accessType) {
            case READ -> arrayStore.getName() + "-r";
            case WRITE -> arrayStore.getName() + "-w";
        };
    }

    @Override
    public void generateJumpTable(AccessType accessType, Map<String, List<LogicInstruction>> jumpTables) {
        jumpTables.computeIfAbsent(getJumpTableId(accessType),  _ -> buildJumpTable(accessType));
    }

    public int getInstructionSize(AccessType accessType, @Nullable Map<String, Integer> sharedStructures) {
        if (sharedStructures != null) {
            int size = arrayStore.getSize() * 2 + (profile.isSymbolicLabels() ? 1 : 0);
            String key = arrayStore.getName() + getJumpTableId(accessType);
            if (!sharedStructures.containsKey(key) || sharedStructures.get(key) < size) {
                sharedStructures.put(key, size);
            }
        }

        int checkSize = profile.getBoundaryChecks().getSize();
        return checkSize + 4;
    }

    private List<LogicInstruction> buildJumpTable(AccessType accessType) {
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
            creator.createMultiJump(firstLabel,writeInd,LogicNumber.ZERO, marker);
        }

        Runnable createExit = () -> creator.createReturn(accessType == AccessType.READ ? readRet : writeRet);
        generateJumpTable(creator, firstLabel, marker, createExit);
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
        AccessType accessType = instruction.getAccessType();
        AstContext astContext = instruction.getAstContext().createSubcontext(AstSubcontextType.ARRAY, 1.0);
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        LogicLabel address = ((LabeledInstruction) jumpTables.get(getJumpTableId(accessType)).get(1)).getLabel();
        LogicLabel returnLabel = processor.nextLabel();

        switch (instruction) {
            case ReadArrInstruction rix -> {
                creator.createOp(Operation.MUL, readInd, instruction.getIndex(), LogicNumber.TWO);
                generateBoundsCheck(astContext, consumer, readInd, 2);
                creator.createCallStackless(address, readRet, LogicVariable.INVALID).setSideEffects(rix.getSideEffects());
                creator.createSet(rix.getResult(), readVal);
            }

            case WriteArrInstruction wix -> {
                creator.createSet(writeVal, wix.getValue());
                creator.createOp(Operation.MUL, writeInd, instruction.getIndex(), LogicNumber.TWO);
                generateBoundsCheck(astContext, consumer, writeInd, 2);
                creator.createCallStackless(address, writeRet, LogicVariable.INVALID).setSideEffects(wix.getSideEffects());
            }

            default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
        }
    }

    private void expandInstructionDirectAddress(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables) {
        AccessType accessType = instruction.getAccessType();
        AstContext astContext = instruction.getAstContext();
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        LogicVariable temp = creator.nextTemp();
        LogicLabel marker = Objects.requireNonNull(jumpTables.get(getJumpTableId(accessType)).get(1).getMarker());
        LogicLabel marker2 = processor.nextMarker();
        LogicLabel target = ((LabeledInstruction) jumpTables.get(getJumpTableId(accessType)).get(1)).getLabel();
        LogicLabel returnLabel = processor.nextLabel();

        switch (instruction) {
            case ReadArrInstruction rix -> {
                creator.createSetAddress(readRet, returnLabel).setHoistId(marker2);
                creator.createOp(Operation.MUL, temp, instruction.getIndex(), LogicNumber.TWO);
                generateBoundsCheck(astContext, consumer, temp, 2);
                creator.createMultiCall(target, temp, marker).setSideEffects(rix.getSideEffects()).setHoistId(marker2);
                creator.createLabel(returnLabel);
                creator.createSet(rix.getResult(), readVal);
            }

            case WriteArrInstruction wix -> {
                creator.createSetAddress(writeRet, returnLabel).setHoistId(marker2);
                creator.createSet(writeVal, wix.getValue());
                creator.createOp(Operation.MUL, temp, instruction.getIndex(), LogicNumber.TWO);
                generateBoundsCheck(astContext, consumer, temp, 2);
                creator.createMultiCall(target, temp, marker).setSideEffects(wix.getSideEffects()).setHoistId(marker2);
                creator.createLabel(returnLabel);
            }

            default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
        }
    }
}
