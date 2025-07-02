package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.Condition;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class ArraySize2Or3Constructor extends AbstractArrayConstructor {
    private final int arraySize;

    public ArraySize2Or3Constructor(ArrayAccessInstruction instruction) {
        super(instruction);
        arraySize = arrayStore.getSize();
        if (arraySize != 2 && arraySize != 3) throw new MindcodeInternalError("Expected array of size 2 or 3");
    }

    @Override
    public SideEffects createSideEffects(AccessType accessType) {
        return switch (accessType) {
            case READ -> SideEffects.reads(arrayElements());
            case WRITE -> SideEffects.writes(arrayElements());
        };
    }

    @Override
    public int getInstructionSize(AccessType accessType, @Nullable Map<String, Integer> sharedStructures) {
        return profile.getBoundaryChecks().getSize() + (arraySize == 2 ? 4 : 7);
    }

    @Override
    public void generateJumpTable(AccessType accessType, Map<String, List<LogicInstruction>> jumpTables) {
        // No jump tables
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables) {
        generateBoundsCheck(instruction.getAstContext(), consumer, instruction.getIndex(), 1 );

        AstContext astContext = this.instruction.getAstContext().createChild(instruction.getAstContext().existingNode(),
                AstContextType.IF, AstSubcontextType.BASIC);
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        switch (instruction) {
            case ReadArrInstruction rix -> expandAccess(creator, element -> element.readValue(creator, rix.getResult()));
            case WriteArrInstruction wix -> expandAccess( creator, element -> element.setValue(creator, wix.getValue()));
            default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
        }
    }

    private void expandAccess(LocalContextfulInstructionsCreator creator, Consumer<ValueStore> operation) {
        createIfElse(creator, operation, 0, arraySize - 1);
    }

    private void createIfElse(LocalContextfulInstructionsCreator creator, Consumer<ValueStore> operation, int startIndex, int endIndex) {
        creator.setSubcontextType(AstSubcontextType.CONDITION, 1.0);
        LogicLabel elseLabel = processor.nextLabel();
        LogicLabel endLabel = processor.nextLabel();

        creator.createJump(elseLabel, Condition.NOT_EQUAL, instruction.getIndex(), LogicNumber.create(startIndex));

        creator.setSubcontextType(AstSubcontextType.BODY, 0.5);
        operation.accept(arrayStore.getElements().get(startIndex));
        creator.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
        creator.createJumpUnconditional(endLabel);
        creator.createLabel(elseLabel);

        creator.setSubcontextType(AstSubcontextType.BODY, 0.5);
        if (endIndex == startIndex + 1) {
            operation.accept(arrayStore.getElements().get(endIndex));
        } else {
            creator.pushContext(AstContextType.IF, AstSubcontextType.BASIC);
            createIfElse(creator, operation, startIndex + 1, endIndex);
            creator.popContext();
        }
        creator.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
        creator.createLabel(endLabel);
    }
}
