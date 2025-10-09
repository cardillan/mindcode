package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class RegularInlinedArrayConstructor extends AbstractArrayConstructor {

    public RegularInlinedArrayConstructor(ArrayAccessInstruction instruction) {
        super(instruction);
    }

    public int getInstructionSize(@Nullable Map<String, Integer> sharedStructures) {
        return profile.getBoundaryChecks().getSize() + 2 + (2 * arrayStore.getSize() - 1);
    }

    @Override
    public double getExecutionSteps() {
        // The last jump in the jump table is eliminated
        return profile.getBoundaryChecks().getExecutionSteps() + 4 - 1.0 / arrayStore.getSize();
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
    public void generateJumpTable(Map<String, List<LogicInstruction>> jumpTables) {
        // No shared jump tables
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables) {
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

        SideEffects sideEffects = switch (instruction) {
            case ReadArrInstruction rix -> SideEffects.of(arrayElementsPlus(tmp),
                    variables(rix.getResult()), List.of());
            case WriteArrInstruction wix -> SideEffects.of(variables(wix.getValue(), tmp),
                    List.of(), arrayElements());
            default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
        };

        creator.createMultiJump(firstLabel,tmp, LogicNumber.ZERO, marker).setSideEffects(sideEffects);
        generateJumpTable(creator, firstLabel, marker, e -> e, createArrayAccessCreator(),
                () -> creator.createJumpUnconditional(finalLabel), true);
        creator.createLabel(finalLabel);
        creator.popContext();
    }
}
