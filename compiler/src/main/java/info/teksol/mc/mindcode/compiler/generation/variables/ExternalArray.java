package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.TMP_VARIABLE;

@NullMarked
public class ExternalArray extends ElementsArrayStore {
    private final LogicVariable memory;
    private final LogicNumber startOffsetNumber;

    public ExternalArray(SourcePosition sourcePosition, String name, LogicVariable memory, int startOffset,
            @Nullable ElementsArrayStore masterArray, List<ValueStore> elements) {
        super(sourcePosition, name, ArrayType.EXTERNAL, startOffset, elements.size(), masterArray, elements, false);
        this.memory = memory;
        this.startOffsetNumber = LogicNumber.create(startOffset);
    }

    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public boolean optimizeElementAccess() {
        return false;
    }

    public LogicVariable getMemory() {
        return memory;
    }

    @Override
    public ArrayStore subarray(SourcePosition sourcePosition, int start, int end) {
        return new ExternalArray(sourcePosition, name, memory, startOffset + start, getMasterArray(), elements.subList(start, end));
    }

    @Override
    public ArrayStore offset(int offset) {
        return new ExternalArray(sourcePosition, name, memory, startOffset + offset, getMasterArray(), elements);
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, int index) {
        return elements.get(index);
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, SourcePosition sourcePosition, ValueStore index,
            boolean safeAccess) {
        if (startOffset == 0) {
            LogicValue fixedIndex = creator.defensiveCopy(index, TMP_VARIABLE);
            return new ArrayElement(sourcePosition, logicArray, fixedIndex, creator.nextTemp(), safeAccess);
        } else {
            LogicVariable actualIndex = creator.nextTemp();
            creator.createOp(Operation.ADD, actualIndex, index.getValue(creator), startOffsetNumber);
            return new ArrayElement(sourcePosition, logicArray, actualIndex, creator.nextTemp(), safeAccess);
        }
    }

    @Override
    public ExternalArray withSourcePosition(SourcePosition sourcePosition) {
        return new ExternalArray(sourcePosition, name, memory, startOffset, (ElementsArrayStore) masterArray, elements);
    }
}
