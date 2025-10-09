package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.compiler.generation.variables.RemoteVariable;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction.AccessType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@NullMarked
public class RegularArrayConstructor extends AbstractArrayConstructor {
    private final LogicVariable proc;
    private final LogicVariable readInd;
    private final LogicVariable readVal;
    private final LogicVariable readRet;
    private final LogicVariable writeInd;
    private final LogicVariable writeVal;
    private final LogicVariable writeRet;

    public RegularArrayConstructor(ArrayAccessInstruction instruction) {
        super(instruction);
        NameCreator nameCreator = MindcodeCompiler.getContext().nameCreator();

        String baseName = arrayStore.getName();
        proc = LogicVariable.arrayAccess(baseName, "*proc", nameCreator.arrayAccess(baseName, "proc"));
        readInd = LogicVariable.arrayAccess(baseName, "*rind", nameCreator.arrayAccess(baseName, "rind"));
        readVal = LogicVariable.arrayAccess(baseName, "*r", nameCreator.arrayAccess(baseName, "r"));
        readRet = LogicVariable.arrayReturn(baseName, "*rret", nameCreator.arrayAccess(baseName, "rret"));
        writeInd = LogicVariable.arrayAccess(baseName, "*wind", nameCreator.arrayAccess(baseName, "wind"));
        writeVal = LogicVariable.arrayAccess(baseName, "*w", nameCreator.arrayAccess(baseName, "w"));
        writeRet = LogicVariable.arrayReturn(baseName, "*wret", nameCreator.arrayAccess(baseName, "wret"));
    }

    public int getInstructionSize(@Nullable Map<String, Integer> sharedStructures) {
        computeSharedJumpTableSize(sharedStructures);
        return profile.getBoundaryChecks().getSize() + 4 + b(arrayStore.getArrayType() == ArrayStore.ArrayType.REMOTE_SHARED);
    }

    @Override
    public double getExecutionSteps() {
        // There is a possibility that further optimization will eliminate the transfer variable,
        // saving at least one instruction and execution step. We can't discount the instruction size safely,
        // but we can at least discount the execution step; this will also cause the regular array to be preferred
        // over a compact array, if there's enough instruction space left.
        return profile.getBoundaryChecks().getExecutionSteps() + 6
               + b(arrayStore.getArrayType() == ArrayStore.ArrayType.REMOTE_SHARED)
               + b(profile.isSymbolicLabels()) - 0.2;
    }

    @Override
    public SideEffects createSideEffects() {
        return switch (accessType) {
            case READ -> createReadSideEffects();
            case WRITE -> createWriteSideEffects();
        };
    }

    private SideEffects createReadSideEffects() {
        List<LogicVariable> reads = arrayElementsPlus(
                profile.isSymbolicLabels() ? readInd : null,
                arrayStore.getArrayType() == ArrayStore.ArrayType.REMOTE_SHARED ? proc : null
        );
        return SideEffects.of(reads, List.of(), List.of(readVal));
    }

    private SideEffects createWriteSideEffects() {
        List<LogicVariable> reads = variables(writeVal,
                profile.isSymbolicLabels() ? writeInd : null,
                arrayStore.getArrayType() == ArrayStore.ArrayType.REMOTE_SHARED ? proc : null
        );
        return SideEffects.of(reads, List.of(), arrayElements());
    }

    @Override
    protected LogicVariable transferVariable() {
        return switch (accessType) {
            case READ -> readVal;
            case WRITE -> writeVal;
        };
    }

    public String getJumpTableId() {
        return switch (accessType) {
            case READ -> arrayStore.getName() + "-r";
            case WRITE -> arrayStore.getName() + "-w";
        };
    }

    @Override
    public void generateJumpTable(Map<String, List<LogicInstruction>> jumpTables) {
        jumpTables.computeIfAbsent(getJumpTableId(),  _ -> buildJumpTable());
    }

    private List<LogicInstruction> buildJumpTable() {
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
            creator.createMultiJump(firstLabel, accessType == AccessType.READ ? readInd : writeInd,LogicNumber.ZERO, marker);
        }

        Function<ValueStore, ValueStore> arrayElementProcessor = arrayStore.getArrayType() == ArrayStore.ArrayType.REMOTE_SHARED
                ? e -> ((RemoteVariable)e).withProcessor(proc)
                : e -> e;
        Runnable createExit = () -> creator.createReturn(accessType == AccessType.READ ? readRet : writeRet);
        generateJumpTable(creator, firstLabel, marker, arrayElementProcessor, createArrayAccessCreator(), createExit, false);
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

        boolean shared = arrayStore.getArrayType() == ArrayStore.ArrayType.REMOTE_SHARED;
        LogicVariable remoteProcessor = shared ? arrayStore.getProcessor() : null;
        LogicLabel address = ((LabeledInstruction) jumpTables.get(getJumpTableId()).get(1)).getLabel();

        switch (instruction) {
            case ReadArrInstruction rix -> {
                if (shared) creator.createSet(proc, remoteProcessor);
                creator.createOp(Operation.MUL, readInd, instruction.getIndex(), LogicNumber.TWO);
                generateBoundsCheck(astContext, consumer, readInd, 2);
                creator.createCallStackless(address, readRet, LogicVariable.INVALID).setSideEffects(rix.getSideEffects());
                creator.createSet(rix.getResult(), readVal);
            }

            case WriteArrInstruction wix -> {
                if (shared) creator.createSet(proc, remoteProcessor);
                creator.createSet(writeVal, wix.getValue());
                creator.createOp(Operation.MUL, writeInd, instruction.getIndex(), LogicNumber.TWO);
                generateBoundsCheck(astContext, consumer, writeInd, 2);
                creator.createCallStackless(address, writeRet, LogicVariable.INVALID).setSideEffects(wix.getSideEffects());
            }

            default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
        }
    }

    private void expandInstructionDirectAddress(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables) {
        AstContext astContext = instruction.getAstContext();
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        boolean shared = arrayStore.getArrayType() == ArrayStore.ArrayType.REMOTE_SHARED;
        LogicVariable remoteProcessor = shared ? arrayStore.getProcessor() : null;
        LogicVariable temp = creator.nextTemp();
        LogicLabel marker = Objects.requireNonNull(jumpTables.get(getJumpTableId()).get(1).getMarker());
        LogicLabel marker2 = processor.nextMarker();
        LogicLabel target = ((LabeledInstruction) jumpTables.get(getJumpTableId()).get(1)).getLabel();
        LogicLabel returnLabel = processor.nextLabel();

        switch (instruction) {
            case ReadArrInstruction rix -> {
                if (shared) creator.createSet(proc, remoteProcessor);
                creator.createSetAddress(readRet, returnLabel).setHoistId(marker2);
                creator.createOp(Operation.MUL, temp, instruction.getIndex(), LogicNumber.TWO);
                generateBoundsCheck(astContext, consumer, temp, 2);
                creator.createMultiCall(target, temp, marker).setSideEffects(rix.getSideEffects()).setHoistId(marker2);
                creator.createLabel(returnLabel);
                creator.createSet(rix.getResult(), readVal);
            }

            case WriteArrInstruction wix -> {
                if (shared) creator.createSet(proc, remoteProcessor);
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
