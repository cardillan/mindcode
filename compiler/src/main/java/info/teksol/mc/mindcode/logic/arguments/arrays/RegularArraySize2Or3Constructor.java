package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class RegularArraySize2Or3Constructor extends AbstractArrayConstructor {
    private final int arraySize;
    private final boolean useSelects;

    public RegularArraySize2Or3Constructor(ArrayAccessInstruction instruction) {
        super(instruction);
        arraySize = arrayStore.getSize();
        if (arraySize != 2 && arraySize != 3) throw new MindcodeInternalError("Expected array of size 2 or 3");
        useSelects = processor.isSupported(Opcode.SELECT)
                     && profile.getOptimizationLevel(Optimization.ARRAY_OPTIMIZATION) == OptimizationLevel.EXPERIMENTAL;
    }

    @Override
    public SideEffects createSideEffects(AccessType accessType) {
        return switch (accessType) {
            case READ -> SideEffects.reads(arrayElements());
            case WRITE -> SideEffects.resets(arrayElements());
        };
    }

    @Override
    public int getInstructionSize(AccessType accessType, @Nullable Map<String, Integer> sharedStructures) {
        if (useSelects) {
            return profile.getBoundaryChecks().getSize() + arraySize - (accessType == AccessType.READ ? 1 : 0)
                    + (instruction.getArray().getArrayStore().isRemote() ? 1 : 0);
        } else {
            return profile.getBoundaryChecks().getSize() + (arraySize == 2 ? 4 : 7);
        }
    }

    @Override
    public void generateJumpTable(AccessType accessType, Map<String, List<LogicInstruction>> jumpTables) {
        // No jump tables
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, List<LogicInstruction>> jumpTables) {
        generateBoundsCheck(instruction.getAstContext(), consumer, instruction.getIndex(), 1 );

        AstContextType contextType = useSelects ? AstContextType.BODY :  AstContextType.IF;
        AstContext astContext = this.instruction.getAstContext().createChild(instruction.getAstContext().existingNode(),
                contextType, AstSubcontextType.BASIC);
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        if (!useSelects) {
            switch (instruction) {
                case ReadArrInstruction rix -> expandAccess(creator, element -> element.readValue(creator, rix.getResult()));
                case WriteArrInstruction wix -> expandAccess(creator, element -> element.setValue(creator, wix.getValue()));
                default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
            }
        } else if (arrayStore.isRemote()) {
            LogicVariable tmp = creator.nextTemp();
            createNameSelect(creator, tmp);
            switch (instruction) {
                case ReadArrInstruction rix -> creator.createRead(rix.getResult(),arrayStore.getProcessor(), tmp);
                case WriteArrInstruction wix -> creator.createWrite(wix.getValue(),arrayStore.getProcessor(), tmp);
                default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
            }
        } else {
            switch (instruction) {
                case ReadArrInstruction rix -> createReadSelect(creator, rix);
                case WriteArrInstruction wix -> createWriteSelect(creator, wix);
                default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
            }
        }
    }

    protected void createNameSelect(LocalContextfulInstructionsCreator creator, LogicVariable arrayElem) {
        creator.setSubcontextType(AstSubcontextType.BODY, 1.0);

        creator.createSelect(arrayElem, Condition.EQUAL, instruction.getIndex(), LogicNumber.ZERO,
                arrayStore.getElements().get(0).getMlogVariableName(),
                arrayStore.getElements().get(1).getMlogVariableName());

        if (arraySize == 3) {
            creator.createSelect(arrayElem, Condition.EQUAL, instruction.getIndex(), LogicNumber.TWO,
                    arrayStore.getElements().get(2).getMlogVariableName(), arrayElem);
        }
    }

    private void createReadSelect(LocalContextfulInstructionsCreator creator, ReadArrInstruction rix) {
        creator.setSubcontextType(AstSubcontextType.BODY, 1.0);
        LogicVariable result = rix.getResult();

        creator.createSelect(result, Condition.EQUAL, instruction.getIndex(), LogicNumber.ZERO,
                arrayStore.getElements().get(0).getValue(creator),
                arrayStore.getElements().get(1).getValue(creator));

        if (arraySize == 3) {
            creator.createSelect(result, Condition.EQUAL, instruction.getIndex(), LogicNumber.TWO,
                    arrayStore.getElements().get(2).getValue(creator), result);
        }
    }

    private void createWriteSelect(LocalContextfulInstructionsCreator creator, WriteArrInstruction wix) {
        creator.setSubcontextType(AstSubcontextType.BODY, 1.0);
        LogicValue value = wix.getValue();

        for (int i = 0; i < arraySize; i++) {
            final int index = i;
            arrayStore.getElements().get(index).writeValue(creator,
                    element -> creator.createSelect(element, Condition.EQUAL, instruction.getIndex(), LogicNumber.create(index), value, element));
        }
    }

    protected void expandAccess(LocalContextfulInstructionsCreator creator, Consumer<ValueStore> operation) {
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
