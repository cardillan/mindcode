package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.TMP_VARIABLE;

@NullMarked
public class LargeArray extends AbstractArrayStore {
    private final List<LogicVariable> memory;
    private final ArrayStore memoryArray;

    public LargeArray(SourcePosition sourcePosition, String name, List<LogicVariable> memory, ArrayStore memoryArray, int size) {
        super(sourcePosition, name, ArrayType.LARGE, 0, size, null, false);
        this.memory = memory;
        this.memoryArray = memoryArray;
    }

    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public boolean optimizeElementAccess() {
        // Optimizing element access helps: memory block selection can be skipped
        return true;
    }

    @Override
    public ArrayStore subarray(SourcePosition sourcePosition, int start, int end) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ArrayStore offset(int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, int index) {
        return new ArrayElement(sourcePosition, logicArray, LogicNumber.create(startOffset + index), creator.nextTemp(), true);
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, SourcePosition sourcePosition, ValueStore index,
            boolean safeAccess) {
        if (startOffset == 0) {
            LogicValue fixedIndex = creator.defensiveCopy(index, TMP_VARIABLE);
            return new ArrayElement(sourcePosition, logicArray, fixedIndex, creator.nextTemp(), safeAccess);
        } else {
            LogicVariable actualIndex = creator.nextTemp();
            return new ArrayElement(sourcePosition, logicArray, actualIndex, creator.nextTemp(), safeAccess);
        }
    }

    @Override
    public LargeArray withSourcePosition(SourcePosition sourcePosition) {
        return new LargeArray(sourcePosition, name, memory, memoryArray, size);
    }
}
