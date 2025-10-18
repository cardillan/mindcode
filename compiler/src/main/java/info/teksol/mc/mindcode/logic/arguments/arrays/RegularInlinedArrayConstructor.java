package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.postprocess.JumpTable;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class RegularInlinedArrayConstructor extends AbstractArrayConstructor {

    public RegularInlinedArrayConstructor(ArrayAccessInstruction instruction) {
        super(instruction);
    }

    @Override
    protected boolean folded() {
        return accessType == ArrayAccessInstruction.AccessType.READ && !arrayStore.isRemote() && instruction.isArrayFolded();
    }

    public int getInstructionSize(@Nullable Map<String, Integer> sharedStructures) {
        int checkSize = profile.getBoundaryChecks().getSize();
        return checkSize + 1 + inlinedTableSize() + flag(folded());
    }

    @Override
    public double getExecutionSteps() {
        int checkSteps = profile.getBoundaryChecks().getExecutionSteps();
        return checkSteps + 4 + flag(folded()) - inlinedTableStepsSavings();
    }

    @Override
    public SideEffects createSideEffects() {
        return switch (accessType) {
            case READ -> createReadSideEffects();
            case WRITE -> createWriteSideEffects();
        };
    }

    private SideEffects createReadSideEffects() {
        return SideEffects.of(arrayElements(), List.of(), List.of());
    }

    private SideEffects createWriteSideEffects() {
        return SideEffects.of(List.of(), List.of(), arrayElements());
    }

    @Override
    protected LogicValue transferVariable() {
        return switch (accessType) {
            case READ -> ((ReadArrInstruction)instruction).getResult();
            case WRITE -> ((WriteArrInstruction)instruction).getValue();
        };
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, JumpTable> jumpTables) {
        boolean folded = folded() && instruction instanceof ReadArrInstruction;
        AstContext astContext = instruction.getAstContext().createSubcontext(AstSubcontextType.ARRAY, 1.0);

        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        LogicLabel finalLabel = processor.nextLabel();
        LogicLabel firstLabel = processor.nextLabel();
        LogicLabel marker = processor.nextMarker();
        LogicVariable tmp;
        if (useTextTables) {
            generateBoundsCheck(astContext, consumer, instruction.getIndex(), 1);
            tmp = LogicVariable.INVALID;  // Won't be used
        } else if (folded) {
            LogicVariable tmp1 = creator.nextTemp();
            creator.createOp(Operation.MUL, tmp1, instruction.getIndex(), LogicNumber.TWO);
            generateBoundsCheck(astContext, consumer, tmp1, 2);
            LogicVariable tmp2 = creator.nextTemp();
            LogicNumber modulo = LogicNumber.create(roundUpToEven(arrayStore.getSize()));
            creator.createOp(Operation.MOD, tmp2, tmp1, modulo);
            tmp = tmp2;
        } else {
            LogicVariable tmp1 = creator.nextTemp();
            creator.createOp(Operation.MUL, tmp1, instruction.getIndex(), LogicNumber.TWO);
            generateBoundsCheck(astContext, consumer, tmp1, 2);
            tmp = tmp1;
        }

        creator.pushContext(AstContextType.JUMPS, AstSubcontextType.BASIC);
        creator.setSubcontextType(AstSubcontextType.ARRAY, 1.0);

        SideEffects sideEffects = switch (instruction) {
            case ReadArrInstruction rix -> SideEffects.of(arrayElements(), variables(rix.getResult()), List.of());
            case WriteArrInstruction wix -> SideEffects.of(variables(wix.getValue()), List.of(), arrayElements());
            default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
        };

        List<LogicLabel> branchLabels = new ArrayList<>();
        if (useTextTables) {
            creator.createMultiJump(instruction.getIndex(), marker).setSideEffects(sideEffects).setJumpTable(branchLabels);
        } else {
            creator.createMultiJump(firstLabel, tmp, LogicNumber.ZERO, marker).setSideEffects(sideEffects);
        }

        if (folded) {
            ReadArrInstruction rix = (ReadArrInstruction) instruction;
            LogicNumber limit = LogicNumber.create((arrayStore.getSize() + 1) / 2);
            generateFoldedJumpTable(creator, firstLabel, marker, e -> e.getValue(creator),
                    instruction.getIndex(), limit, rix.getResult(), () -> creator.createJumpUnconditional(finalLabel),
                    true, useTextTables, branchLabels);
        } else {
            generateJumpTable(creator, firstLabel, marker, e -> e, createArrayAccessCreator(),
                    () -> creator.createJumpUnconditional(finalLabel), true, branchLabels);
        }

        creator.createLabel(finalLabel);
        creator.popContext();
    }
}
