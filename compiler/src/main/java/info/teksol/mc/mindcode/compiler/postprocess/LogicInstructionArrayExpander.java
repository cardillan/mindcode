package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.RuntimeChecks;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/// Expands virtual instructions (READARR, WRITEARR) into real code.
@NullMarked
public class LogicInstructionArrayExpander {
    private final CompilerProfile profile;
    private final InstructionProcessor processor;
    private boolean expanded = false;

    private final Map<String, List<LogicInstruction>> jumpTables = new HashMap<>();

    public LogicInstructionArrayExpander(CompilerProfile profile, InstructionProcessor processor) {
        this.processor = processor;
        this.profile = profile;
    }

    public List<LogicInstruction> expandArrayInstructions(List<LogicInstruction> program) {
        // Already expanded, do not repeat
        if (expanded) return program;

        analyze(program);

        if (jumpTables.isEmpty()) {
            return program;
        }

        List<LogicInstruction> expanded = program.stream()
                .mapMulti(this::expandInstruction)
                .collect(Collectors.toCollection(ArrayList::new));

        int skip = expanded.getLast().getOpcode() == Opcode.END ? 1 : 0;
        jumpTables.values().stream()
                .flatMap(Collection::stream)
                .skip(skip)
                .forEach(expanded::add);

        return expanded;
    }

    public boolean analyze(List<LogicInstruction> program) {
        if (expanded) return false;
        expanded = true;

        // Subarray support (separate processing for read/write):
        // 1. Gather all array access instructions
        // 2. Group by array name, find min/max index per array
        // 3. Build the jump table for the resulting range, remember to adjust offset

        for (LogicInstruction instruction : program) {
            switch (instruction) {
                case ReadArrInstruction ix  -> jumpTables.computeIfAbsent(ix.getArray().getReadJumpTableId(),  _ -> buildReadJumpTable(ix.getArray()));
                case WriteArrInstruction ix -> jumpTables.computeIfAbsent(ix.getArray().getWriteJumpTableId(), _ -> buildWriteJumpTable(ix.getArray()));
                default -> {}
            }
        }

        return !jumpTables.isEmpty();
    }

    public List<LogicInstruction> getJumpTables(boolean generateEndSeparator) {
        return jumpTables.values().stream()
                .flatMap(Collection::stream)
                .skip(generateEndSeparator ? 1 : 0)
                .collect(Collectors.toList());
    }

    public List<LogicInstruction> expand(ArrayAccessInstruction ix) {
        List<LogicInstruction> result = new ArrayList<>();
        expandInstruction(ix, result::add);
        return result;
    }

    private void expandInstruction(LogicInstruction instruction, Consumer<LogicInstruction> consumer) {
        if (!(instruction instanceof ArrayAccessInstruction ix)) {
            consumer.accept(instruction);
            return;
        }

        AstContext astContext = ix.getAstContext();
        LogicArray array = ix.getArray();
        LogicVariable temp = processor.nextTemp();
        LogicLabel marker = Objects.requireNonNull(jumpTables.get(ix.getJumpTableId()).get(1).getMarker());
        LogicLabel target = ((LabeledInstruction) jumpTables.get(ix.getJumpTableId()).get(1)).getLabel();
        LogicLabel returnLabel = processor.nextLabel();

        switch (instruction) {
            case ReadArrInstruction rix -> {
                consumer.accept(processor.createSetAddress(astContext, array.readRet, returnLabel));
                consumer.accept(processor.createOp(astContext, Operation.MUL, temp, ix.getIndex(), LogicNumber.TWO));
                generateBoundsCheck(consumer, rix, temp);
                consumer.accept(processor.createMultiCall(astContext, target, temp, marker).withSideEffects(rix.sideEffects()));
                consumer.accept(processor.createLabel(astContext, returnLabel));
                consumer.accept(processor.createSet(astContext, rix.getResult(), array.readVal));
            }

            case WriteArrInstruction wix -> {
                consumer.accept(processor.createSetAddress(astContext, array.writeRet, returnLabel));
                consumer.accept(processor.createSet(astContext, array.writeVal, wix.getValue()));
                consumer.accept(processor.createOp(astContext, Operation.MUL, temp, ix.getIndex(), LogicNumber.TWO));
                generateBoundsCheck(consumer, wix, temp);
                consumer.accept(processor.createMultiCall(astContext, target, temp, marker).withSideEffects(wix.sideEffects()));
                consumer.accept(processor.createLabel(astContext, returnLabel));
            }

            default -> consumer.accept(instruction);
        }
    }

    private void generateBoundsCheck(Consumer<LogicInstruction> consumer, ArrayAccessInstruction ix, LogicVariable index) {
        RuntimeChecks boundaryChecks = ix.getAstContext().getProfile().getBoundaryChecks();
        if (boundaryChecks == RuntimeChecks.NONE) return;

        AstContext astContext = ix.getAstContext();
        int maxIndex = ix.getArray().getSize() - 1;
        LogicNumber max = LogicNumber.create(maxIndex * 2L);
        String errorMessage = String.format("%s: index out of bounds (%d to %d)", ix.sourcePosition().formatForMlog(), 0, maxIndex);

        if (boundaryChecks == RuntimeChecks.ASSERT) {
            consumer.accept(processor.createInstruction(astContext, Opcode.ASSERT_BOUNDS, LogicKeyword.create("multiple"),
                    LogicNumber.TWO, LogicNumber.ZERO, Condition.LESS_THAN_EQ, index, Condition.LESS_THAN_EQ, max, LogicString.create(errorMessage)));
            return;
        }

        AstContext ctx = astContext.createChild(profile, Objects.requireNonNull(astContext.node()),
                AstContextType.IF, AstSubcontextType.CONDITION);

        switch (boundaryChecks) {
            case MINIMAL -> {
                LogicLabel logicLabel1 = processor.nextLabel();
                consumer.accept(processor.createLabel(ctx, logicLabel1));
                consumer.accept(processor.createJump(ctx, logicLabel1, Condition.LESS_THAN, index, LogicNumber.ZERO));
                LogicLabel logicLabel2 = processor.nextLabel();
                consumer.accept(processor.createLabel(ctx, logicLabel2));
                consumer.accept(processor.createJump(ctx, logicLabel2, Condition.GREATER_THAN, index, max));
            }

            case SIMPLE, DESCRIBED -> {
                LogicLabel logicLabelStop = processor.nextLabel();
                LogicLabel logicLabelRun = processor.nextLabel();
                consumer.accept(processor.createJump(ctx, logicLabelStop, Condition.LESS_THAN, index, LogicNumber.ZERO));
                consumer.accept(processor.createJump(ctx, logicLabelRun, Condition.LESS_THAN_EQ, index, max));
                consumer.accept(processor.createLabel(ctx, logicLabelStop));
                if (boundaryChecks == RuntimeChecks.DESCRIBED) {
                    consumer.accept(processor.createPrint(ctx, LogicString.create(errorMessage)));
                }
                consumer.accept(processor.createStop(ctx));
                consumer.accept(processor.createLabel(ctx, logicLabelRun));
            }
        }
    }

    private List<LogicInstruction> buildReadJumpTable(LogicArray array) {
        AstContext astContext = MindcodeCompiler.getContext().getRootAstContext()
                .createSubcontext(AstContextType.ARRAY_READ, AstSubcontextType.BASIC, 1.0);
        List<LogicInstruction> result = new ArrayList<>();
        LogicLabel marker = processor.nextMarker();

        result.add(processor.createEnd(astContext));
        for (LogicVariable element : array.getElements()) {
            result.add(processor.createMultiLabel(astContext, processor.nextLabel(), marker));
            result.add(processor.createSet(astContext, array.readVal, element));
            result.add(processor.createReturn(astContext, array.readRet));
        }

        return result;
    }

    private List<LogicInstruction> buildWriteJumpTable(LogicArray array) {
        AstContext astContext = MindcodeCompiler.getContext().getRootAstContext()
                .createSubcontext(AstContextType.ARRAY_WRITE, AstSubcontextType.BASIC, 1.0);
        List<LogicInstruction> result = new ArrayList<>();
        LogicLabel marker = processor.nextMarker();

        result.add(processor.createEnd(astContext));
        for (LogicVariable element : array.getElements()) {
            result.add(processor.createMultiLabel(astContext, processor.nextLabel(), marker));
            result.add(processor.createSet(astContext, element, array.writeVal));
            result.add(processor.createReturn(astContext, array.writeRet));
        }

        return result;
    }
}
