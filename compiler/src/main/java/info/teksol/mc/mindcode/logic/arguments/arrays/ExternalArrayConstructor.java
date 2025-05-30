package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ExternalArray;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class ExternalArrayConstructor extends AbstractArrayConstructor {
    private final LogicVariable memory;

    public ExternalArrayConstructor(ArrayAccessInstruction instruction) {
        super(instruction);
        memory = ((ExternalArray)instruction.getArray().getArrayStore()).getMemory();
    }

    @Override
    public SideEffects createSideEffects(AccessType accessType) {
        return SideEffects.NONE;
    }

    @Override
    public int getInstructionSize(AccessType accessType, @Nullable Map<String, Integer> sharedStructures) {
        return profile.getBoundaryChecks().getSize() + 1;
    }

    @Override
    public void generateJumpTable(AccessType accessType, Map<String, List<LogicInstruction>> jumpTables) {
        // Do nothing
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables) {
        AccessType accessType = instruction.getAccessType();
        AstContext astContext = instruction.getAstContext();
        generateBoundsCheck(astContext, consumer, instruction.getIndex(), 1 );
        switch (instruction) {
            case ReadArrInstruction rix -> consumer.accept(processor.createRead(astContext, rix.getResult(), memory, rix.getIndex()));
            case WriteArrInstruction wix -> consumer.accept(processor.createWrite(astContext, wix.getValue(), memory, wix.getIndex()));
            default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
        }
    }
}
