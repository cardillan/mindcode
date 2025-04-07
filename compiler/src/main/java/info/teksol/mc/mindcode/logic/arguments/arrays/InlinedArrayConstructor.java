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
import java.util.stream.Stream;

@NullMarked
public class InlinedArrayConstructor extends AbstractArrayConstructor {

    public InlinedArrayConstructor(ArrayAccessInstruction instruction) {
        super(instruction);
    }

    @Override
    public SideEffects createSideEffects(AccessType accessType) {
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
    protected LogicValue transferVariable(AccessType accessType) {
        return switch (accessType) {
            case READ -> ((ReadArrInstruction)instruction).getResult();
            case WRITE -> ((WriteArrInstruction)instruction).getValue();
        };
    }

    @Override
    public void generateJumpTable(AccessType accessType, Map<String, List<LogicInstruction>> jumpTables) {
        // No shared jump tables
    }

    public int getInstructionSize(AccessType accessType, @Nullable Map<String, Integer> sharedStructures) {
        int checkSize = profile.getBoundaryChecks().getSize();
        return checkSize + 1 + 2 * arrayStore.getSize();
    }

    private List<LogicVariable> arrayElementsPlus(LogicValue value) {
        return arrayElementsConcat(Stream.of(value));
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables) {
        AccessType accessType = instruction.getAccessType();
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

        SideEffects sideEffects =  switch (instruction) {
            case ReadArrInstruction rix -> SideEffects.of(arrayElementsPlus(tmp),
                    variables(rix.getResult()), List.of());
            case WriteArrInstruction wix -> SideEffects.of(variables(wix.getValue(), tmp),
                    List.of(), arrayElements());
            default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
        };

        creator.createMultiJump(firstLabel,tmp, LogicNumber.ZERO, marker).setSideEffects(sideEffects);
        generateJumpTable(creator, firstLabel, marker, () -> creator.createJumpUnconditional(finalLabel));
        creator.createLabel(finalLabel);
        creator.popContext();
    }
}
