package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.RemoteVariable;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
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
import java.util.stream.Collectors;

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

        // Maybe move to InternalArray?
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
        List<LogicVariable> elements = arrayStore.getElements().stream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .collect(Collectors.toCollection(ArrayList::new));

        if (profile.isSymbolicLabels()) {
            elements.add(readInd);
        }

        return SideEffects.of(List.copyOf(elements), List.of(), List.of(readVal));
    }

    private SideEffects createWriteSideEffects() {
        List<LogicVariable> elements = arrayStore.getElements().stream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .toList();

        List<LogicVariable> reads = profile.isSymbolicLabels()
                ? List.of(writeVal, writeInd)
                : List.of(writeVal);

        return SideEffects.of(reads, List.of(), elements);
    }

    private String getJumpTableId(AccessType accessType) {
        return switch (accessType) {
            case READ -> arrayStore.getName() + "-r";
            case WRITE -> arrayStore.getName() + "-w";
        };
    }

    @Override
    public void generateJumpTable(AccessType accessType, Map<String, List<LogicInstruction>> jumpTables) {
        switch (accessType) {
            case READ   -> jumpTables.computeIfAbsent(getJumpTableId(accessType),  _ -> buildReadJumpTable());
            case WRITE  -> jumpTables.computeIfAbsent(getJumpTableId(accessType),  _ -> buildWriteJumpTable());
        }
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

    private List<LogicInstruction> buildReadJumpTable() {
        AstContext astContext = MindcodeCompiler.getContext().getRootAstContext()
                .createSubcontext(AstContextType.ARRAY_READ, AstSubcontextType.BASIC, 1.0);
        List<LogicInstruction> result = new ArrayList<>();
        LogicLabel marker = processor.nextMarker();

        result.add(processor.createEnd(astContext));
        LogicLabel nextLabel = processor.nextLabel();

        if (profile.isSymbolicLabels()) {
            LogicLabel startLabel = processor.nextLabel();
            result.add(processor.createLabel(astContext, startLabel).setMarker(startLabel));
            result.add(processor.createMultiJump(astContext, nextLabel,readInd, LogicNumber.ZERO, marker));
        }

        for (ValueStore element : arrayStore.getElements()) {
            result.add(processor.createMultiLabel(astContext, nextLabel, marker));
            switch (element) {
                case LogicVariable value     -> result.add(processor.createSet(astContext, readVal, value));
                case RemoteVariable variable -> result.add(processor.createRead(astContext, readVal,
                        variable.getProcessor(), variable.getVariableName()));
                default -> throw new MindcodeInternalError("Unhandled array element type");
            }
            result.add(processor.createReturn(astContext, readRet));
            nextLabel = processor.nextLabel();      // We'll waste one label. Meh.
        }

        return result;
    }

    private List<LogicInstruction> buildWriteJumpTable() {
        AstContext astContext = MindcodeCompiler.getContext().getRootAstContext()
                .createSubcontext(AstContextType.ARRAY_WRITE, AstSubcontextType.BASIC, 1.0);
        List<LogicInstruction> result = new ArrayList<>();
        LogicLabel marker = processor.nextMarker();

        result.add(processor.createEnd(astContext));
        LogicLabel nextLabel = processor.nextLabel();

        if (profile.isSymbolicLabels()) {
            LogicLabel startLabel = processor.nextLabel();
            result.add(processor.createLabel(astContext, startLabel).setMarker(startLabel));
            result.add(processor.createMultiJump(astContext, nextLabel,writeInd,LogicNumber.ZERO, marker));
        }

        for (ValueStore element : arrayStore.getElements()) {
            result.add(processor.createMultiLabel(astContext, nextLabel, marker));
            switch (element) {
                case LogicVariable value     -> result.add(processor.createSet(astContext, value, writeVal));
                case RemoteVariable variable -> result.add(processor.createWrite(astContext, writeVal,
                        variable.getProcessor(), variable.getVariableName()));
                default -> throw new MindcodeInternalError("Unhandled array element type");
            }
            result.add(processor.createReturn(astContext, writeRet));
            nextLabel = processor.nextLabel();      // We'll waste one label. Meh.
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
        AccessType accessType = instruction.getAccessType();
        AstContext astContext = instruction.getAstContext().createSubcontext(AstSubcontextType.ARRAY, 1.0);
        LogicLabel marker = Objects.requireNonNull(jumpTables.get(getJumpTableId(accessType)).get(1).getMarker());
        LogicLabel address = ((LabeledInstruction) jumpTables.get(getJumpTableId(accessType)).get(1)).getLabel();
        LogicLabel returnLabel = processor.nextLabel();

        switch (instruction) {
            case ReadArrInstruction rix -> {
                consumer.accept(processor.createOp(astContext, Operation.MUL, readInd, instruction.getIndex(), LogicNumber.TWO));
                generateBoundsCheck(astContext, consumer, readInd, 2);
                consumer.accept(processor.createCallStackless(astContext, address, readRet, LogicVariable.INVALID).setSideEffects(rix.getSideEffects()));
                consumer.accept(processor.createSet(astContext, rix.getResult(), readVal));
            }

            case WriteArrInstruction wix -> {
                consumer.accept(processor.createSet(astContext, writeVal, wix.getValue()));
                consumer.accept(processor.createOp(astContext, Operation.MUL, writeInd, instruction.getIndex(), LogicNumber.TWO));
                generateBoundsCheck(astContext, consumer, writeInd, 2);
                consumer.accept(processor.createCallStackless(astContext, address, writeRet, LogicVariable.INVALID).setSideEffects(wix.getSideEffects()));
            }

            default -> consumer.accept(instruction);
        }
    }

    private void expandInstructionDirectAddress(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables) {
        AccessType accessType = instruction.getAccessType();
        AstContext astContext = instruction.getAstContext();
        LogicVariable temp = processor.nextTemp();
        LogicLabel marker = Objects.requireNonNull(jumpTables.get(getJumpTableId(accessType)).get(1).getMarker());
        LogicLabel target = ((LabeledInstruction) jumpTables.get(getJumpTableId(accessType)).get(1)).getLabel();
        LogicLabel returnLabel = processor.nextLabel();

        switch (instruction) {
            case ReadArrInstruction rix -> {
                consumer.accept(processor.createSetAddress(astContext, readRet, returnLabel));
                consumer.accept(processor.createOp(astContext, Operation.MUL, temp, instruction.getIndex(), LogicNumber.TWO));
                generateBoundsCheck(astContext, consumer, temp, 2);
                consumer.accept(processor.createMultiCall(astContext, target, temp, marker).setSideEffects(rix.getSideEffects()));
                consumer.accept(processor.createLabel(astContext, returnLabel));
                consumer.accept(processor.createSet(astContext, rix.getResult(), readVal));
            }

            case WriteArrInstruction wix -> {
                consumer.accept(processor.createSetAddress(astContext, writeRet, returnLabel));
                consumer.accept(processor.createSet(astContext, writeVal, wix.getValue()));
                consumer.accept(processor.createOp(astContext, Operation.MUL, temp, instruction.getIndex(), LogicNumber.TWO));
                generateBoundsCheck(astContext, consumer, temp, 2);
                consumer.accept(processor.createMultiCall(astContext, target, temp, marker).setSideEffects(wix.getSideEffects()));
                consumer.accept(processor.createLabel(astContext, returnLabel));
            }

            default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
        }
    }
}
