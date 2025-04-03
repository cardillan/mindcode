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
import java.util.Objects;
import java.util.function.Consumer;

@NullMarked
public class ArraySize3Constructor extends AbstractArrayConstructor {

    public ArraySize3Constructor(ArrayAccessInstruction instruction) {
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
        return profile.getBoundaryChecks().getSize() + 7;
    }

    @Override
    public void generateJumpTable(AccessType accessType, Map<String, List<LogicInstruction>> jumpTables) {
        // No jump tables
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables) {
        generateBoundsCheck(instruction.getAstContext(), consumer, instruction.getIndex(), 1);

        ValueStore element = arrayStore.getElements().getFirst();
        switch (instruction) {
            case ReadArrInstruction rix -> expandRead(consumer, rix);
            case WriteArrInstruction wix -> expandWrite(consumer, wix);
            default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
        }
    }

    private void expandRead(Consumer<LogicInstruction> consumer, ReadArrInstruction instruction) {
        AstContext astContext = this.instruction.getAstContext().createChild(profile,
                Objects.requireNonNull(this.instruction.getAstContext().node()),
                AstContextType.IF, AstSubcontextType.BASIC);

        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        LogicLabel label1 = processor.nextLabel();
        LogicLabel label2 = processor.nextLabel();
        LogicLabel labelX = processor.nextLabel();

        creator.setSubcontextType(AstSubcontextType.CONDITION, 1.0);
        creator.createJump(label1, Condition.NOT_EQUAL, instruction.getIndex(), LogicNumber.ZERO);
        creator.setSubcontextType(AstSubcontextType.BODY, 1.0 / 3.0);
        arrayStore.getElements().getFirst().readValue(creator, instruction.getResult());
        creator.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 1.0 / 3.0);
        creator.createJumpUnconditional(labelX);

        creator.createLabel(label1);
        creator.setSubcontextType(AstSubcontextType.BODY, 1.0 / 3.0);
        creator.pushContext(AstContextType.IF, AstSubcontextType.BASIC);

        creator.setSubcontextType(AstSubcontextType.CONDITION, 2.0 / 3.0);
        creator.createJump(label2, Condition.NOT_EQUAL, instruction.getIndex(), LogicNumber.ONE);
        creator.setSubcontextType(AstSubcontextType.BODY, 1.0 / 3.0);
        arrayStore.getElements().get(1).readValue(creator, instruction.getResult());
        creator.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 1.0 / 3.0);
        creator.createJumpUnconditional(labelX);

        creator.createLabel(label2);

        creator.setSubcontextType(AstSubcontextType.BODY, 1.0 / 3.0);
        arrayStore.getElements().get(2).readValue(creator, instruction.getResult());
        creator.popContext();
        creator.createLabel(labelX);
    }

    private void expandWrite(Consumer<LogicInstruction> consumer, WriteArrInstruction instruction) {
        AstContext astContext = this.instruction.getAstContext().createChild(profile,
                Objects.requireNonNull(this.instruction.getAstContext().node()),
                AstContextType.IF, AstSubcontextType.BASIC);

        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        LogicLabel label1 = processor.nextLabel();
        LogicLabel label2 = processor.nextLabel();
        LogicLabel labelX = processor.nextLabel();

        creator.setSubcontextType(AstSubcontextType.CONDITION, 1.0);
        creator.createJump(label1, Condition.NOT_EQUAL, instruction.getIndex(), LogicNumber.ZERO);
        creator.setSubcontextType(AstSubcontextType.BODY, 1.0 / 3.0);
        arrayStore.getElements().getFirst().setValue(creator, instruction.getValue());
        creator.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 1.0 / 3.0);
        creator.createJumpUnconditional(labelX);

        creator.createLabel(label1);
        creator.setSubcontextType(AstSubcontextType.BODY, 1.0 / 3.0);
        creator.pushContext(AstContextType.IF, AstSubcontextType.BASIC);

        creator.setSubcontextType(AstSubcontextType.CONDITION, 2.0 / 3.0);
        creator.createJump(label2, Condition.NOT_EQUAL, instruction.getIndex(), LogicNumber.ONE);
        creator.setSubcontextType(AstSubcontextType.BODY, 1.0 / 3.0);
        arrayStore.getElements().get(1).setValue(creator, instruction.getValue());
        creator.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 1.0 / 3.0);
        creator.createJumpUnconditional(labelX);

        creator.createLabel(label2);

        creator.setSubcontextType(AstSubcontextType.BODY, 1.0 / 3.0);
        arrayStore.getElements().get(2).setValue(creator, instruction.getValue());
        creator.popContext();
        creator.createLabel(labelX);
    }
}
