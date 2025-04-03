package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class ArraySize1Constructor extends AbstractArrayConstructor {
    public ArraySize1Constructor(ArrayAccessInstruction instruction) {
        super(instruction);
    }

    @Override
    public SideEffects createSideEffects(AccessType accessType) {
        return switch (accessType) {
            case READ -> SideEffects.reads(arrayElements().toList());
            case WRITE -> SideEffects.writes(arrayElements().toList());
        };
    }

    @Override
    public int getInstructionSize(AccessType accessType, @Nullable Map<String, Integer> sharedStructures) {
        return profile.getBoundaryChecks().getSize() + 1;
    }

    @Override
    public void generateJumpTable(AccessType accessType, Map<String, List<LogicInstruction>> jumpTables) {
        // No jump tables
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables) {
        AstContext astContext = instruction.getAstContext();
        generateBoundsCheck(astContext, consumer, instruction.getIndex(), 1 );

        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);
        ValueStore element = arrayStore.getElements().getFirst();
        switch (instruction) {
            case ReadArrInstruction rix -> element.readValue(creator, rix.getResult());
            case WriteArrInstruction wix -> element.setValue(creator, wix.getValue());
            default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
        }
    }
}
