package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.postprocess.JumpTable;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction.AccessType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@NullMarked
public class RegularShortArrayConstructor extends TablelessArrayConstructor {
    private final int arraySize;
    private final boolean useSelects;

    public RegularShortArrayConstructor(ArrayConstructorContext context, ArrayAccessInstruction instruction) {
        super(context, instruction);
        arraySize = arrayStore.getFullSize();
        useSelects = processor.isSupported(Opcode.SELECT);
        int limit = useSelects ? 4 : 3;
        if (arraySize < 2 || arraySize > limit) throw new MindcodeInternalError("Expected array of size 2 to " + limit);
    }

    @Override
    public int getInstructionSize(@Nullable Map<String, Integer> sharedStructures) {
        if (useSelects) {
            // If the array is not remote, we save one instruction on read
            return boundsCheckSize() + offsetInstructions() + arraySize - flag(!arrayStore.isRemote() && accessType == AccessType.READ);
        } else {
            return boundsCheckSize() + offsetInstructions() + (arraySize == 2 ? 4 : 7);
        }
    }

    @Override
    public double getExecutionSteps() {
        if (useSelects) {
            // If the array is not remote, we save one instruction on read
            return boundsCheckExecutionSteps() + offsetInstructions() + arraySize - flag(!arrayStore.isRemote() && accessType == AccessType.READ);
        } else {
            return boundsCheckExecutionSteps() + offsetInstructions() + (arraySize == 2 ? 2.5 : (3 + 4 + 3) / 3.0);
        }
    }

    @Override
    public SideEffects createSideEffects() {
        return switch (accessType) {
            case READ -> SideEffects.reads(arrayElements());
            case WRITE -> SideEffects.resets(arrayElements());
        };
    }

    protected LocalContextfulInstructionsCreator prepareExpansion(Consumer<LogicInstruction> consumer) {
        generateBoundsCheck(instruction.getAstContext(), consumer, instruction.getIndex(), 1);

        AstContextType contextType = useSelects ? AstContextType.CODE :  AstContextType.IF;
        AstContext astContext = this.instruction.getAstContext().createChild(instruction.getAstContext().existingNode(),
                contextType, AstSubcontextType.BASIC);
        return new LocalContextfulInstructionsCreator(processor, astContext, consumer);
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, JumpTable> jumpTables) {
        LocalContextfulInstructionsCreator creator = prepareExpansion(consumer);
        LogicValue index = computeIndex(creator);

        if (!useSelects) {
            switch (instruction) {
                case ReadArrInstruction rix -> expandAccess(creator, index, element -> element.readValue(creator, rix.getResult()));
                case WriteArrInstruction wix -> expandAccess(creator, index, element -> element.setValue(creator, wix.getValue()));
                default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
            }
        } else if (arrayStore.isRemote()) {
            LogicVariable tmp = creator.nextTemp();
            createNameSelect(creator, index, tmp);
            switch (instruction) {
                case ReadArrInstruction rix -> creator.createRead(rix.getResult(),arrayStore.getProcessor(), tmp);
                case WriteArrInstruction wix -> creator.createWrite(wix.getValue(),arrayStore.getProcessor(), tmp);
                default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
            }
        } else {
            switch (instruction) {
                case ReadArrInstruction rix -> createReadSelect(creator, index, rix);
                case WriteArrInstruction wix -> createWriteSelect(creator, index, wix);
                default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
            }
        }
    }

    private void createSelect(LocalContextfulInstructionsCreator creator, LogicValue index, LogicVariable result,
            Function<ValueStore, LogicValue> valueExtractor) {
        creator.setSubcontextType(AstSubcontextType.BODY, 1.0);

        if (arraySize == 4) {
            LogicVariable tmp0 = creator.nextTemp();
            creator.createSelect(tmp0, Condition.LESS_THAN, index, LogicNumber.ONE,
                            valueExtractor.apply(arrayStore.getElements().get(0)),
                            valueExtractor.apply(arrayStore.getElements().get(1)))
                    .setNonNegativeInt(instruction.getIndex());


            LogicVariable tmp1 = creator.nextTemp();
            creator.createSelect(tmp1, Condition.LESS_THAN, index, LogicNumber.THREE,
                            valueExtractor.apply(arrayStore.getElements().get(2)),
                            valueExtractor.apply(arrayStore.getElements().get(3)))
                    .setNonNegativeInt(instruction.getIndex());

            creator.createSelect(result, Condition.LESS_THAN, index, LogicNumber.TWO, tmp0, tmp1)
                    .setNonNegativeInt(instruction.getIndex());
        } else {
            creator.createSelect(result, Condition.LESS_THAN, index, LogicNumber.ONE,
                            valueExtractor.apply(arrayStore.getElements().get(0)),
                            valueExtractor.apply(arrayStore.getElements().get(1)))
                    .setNonNegativeInt(instruction.getIndex());

            if (arraySize == 3) {
                creator.createSelect(result, Condition.LESS_THAN, index, LogicNumber.TWO,
                                result, valueExtractor.apply(arrayStore.getElements().get(2)))
                        .setNonNegativeInt(instruction.getIndex());
            }
        }
    }

    protected void createNameSelect(LocalContextfulInstructionsCreator creator, LogicValue index, LogicVariable arrayElem) {
        creator.setSubcontextType(AstSubcontextType.BODY, 1.0);
        createSelect(creator, index, arrayElem, ValueStore::getMlogVariableName);
    }

    private void createReadSelect(LocalContextfulInstructionsCreator creator, LogicValue index, ReadArrInstruction rix) {
        creator.setSubcontextType(AstSubcontextType.BODY, 1.0);
        LogicVariable result = rix.getResult();
        createSelect(creator, index, result, element -> element.getValue(creator));
    }

    private void createWriteSelect(LocalContextfulInstructionsCreator creator, LogicValue index, WriteArrInstruction wix) {
        creator.setSubcontextType(AstSubcontextType.BODY, 1.0);
        LogicValue value = wix.getValue();

        for (int i = 0; i < arraySize; i++) {
            final int ind = i;
            arrayStore.getElements().get(ind).writeValue(creator,
                    element -> creator.createSelect(element, Condition.EQUAL,
                            instruction.getIndex(), LogicNumber.create(ind), value, element));
        }
    }

    protected void expandAccess(LocalContextfulInstructionsCreator creator, LogicValue index, Consumer<ValueStore> valueExtractor) {
        createIfElse(creator, index, valueExtractor, 0, arraySize - 1);
    }

    private void createIfElse(LocalContextfulInstructionsCreator creator, LogicValue index, Consumer<ValueStore> valueExtractor, int startIndex, int endIndex) {
        creator.setSubcontextType(AstSubcontextType.CONDITION, 1.0);
        LogicLabel elseLabel = processor.nextLabel();
        LogicLabel endLabel = processor.nextLabel();

        creator.createJump(elseLabel, Condition.GREATER_THAN_EQ, index, LogicNumber.create(startIndex + 1))
                .setNonNegativeInt(instruction.getIndex());

        creator.setSubcontextType(AstSubcontextType.BODY, 0.5);
        valueExtractor.accept(arrayStore.getElements().get(startIndex));
        creator.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
        creator.createJumpUnconditional(endLabel);
        creator.createLabel(elseLabel);

        creator.setSubcontextType(AstSubcontextType.BODY, 0.5);
        if (endIndex == startIndex + 1) {
            valueExtractor.accept(arrayStore.getElements().get(endIndex));
        } else {
            creator.pushContext(AstContextType.IF, AstSubcontextType.BASIC);
            createIfElse(creator, index, valueExtractor, startIndex + 1, endIndex);
            creator.popContext();
        }
        creator.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
        creator.createLabel(endLabel);
    }
}
