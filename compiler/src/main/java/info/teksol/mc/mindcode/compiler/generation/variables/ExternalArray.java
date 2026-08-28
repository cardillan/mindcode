package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.TMP_VARIABLE;

@NullMarked
public class ExternalArray extends AbstractArrayStore {
    private final LogicVariable memory;
    private final int baseIndex;
    private final LogicNumber baseIndexNumber;
    private final LogicArray logicArray;

    public ExternalArray(SourcePosition sourcePosition, String name, LogicVariable memory, int baseIndex, List<ValueStore> elements) {
        super(sourcePosition, name, elements.size(), elements);
        this.memory = memory;
        this.baseIndex = baseIndex;
        this.baseIndexNumber = LogicNumber.create(baseIndex);
        logicArray = LogicArray.create(this);
    }

    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public boolean isDeclaredRemote() {
        return false;
    }

    @Override
    public ArrayType getArrayType() {
        return ArrayType.EXTERNAL;
    }

    @Override
    public int getStartOffset() {
        return baseIndex;
    }

    @Override
    public @Nullable MindcodeFunction getFunction() {
        return null;
    }

    @Override
    public LogicValue getArrayOffset() {
        return LogicNumber.ZERO;
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
        return new ExternalArray(sourcePosition, name, memory, baseIndex + start, elements.subList(start, end));
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, int index) {
        return elements.get(index);
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, SourcePosition sourcePosition, ValueStore index) {
        if (baseIndex == 0) {
            LogicValue fixedIndex = creator.defensiveCopy(index, TMP_VARIABLE);
            return new ExternalArrayElement(sourcePosition, fixedIndex, creator.nextTemp());
        } else {
            LogicVariable actualIndex = creator.nextTemp();
            creator.createOp(Operation.ADD, actualIndex, index.getValue(creator), baseIndexNumber);
            return new ExternalArrayElement(sourcePosition, actualIndex, creator.nextTemp());
        }
    }

    @Override
    public ExternalArray withSourcePosition(SourcePosition sourcePosition) {
        return new ExternalArray(sourcePosition, name, memory, baseIndex, elements);
    }

    private class ExternalArrayElement implements ValueStore {
        private final SourcePosition sourcePosition;
        private final LogicValue index;
        private final LogicVariable transferVariable;

        public ExternalArrayElement(SourcePosition sourcePosition, LogicValue index, LogicVariable transferVariable) {
            this.sourcePosition = sourcePosition;
            this.index = index;
            this.transferVariable = transferVariable;
        }

        @Override
        public boolean isComplex() {
            return true;
        }

        @Override
        public boolean isLvalue() {
            return true;
        }

        @Override
        public LogicValue getValue(ContextfulInstructionCreator creator) {
            creator.createReadArr(transferVariable, logicArray, index);
            return transferVariable;
        }

        @Override
        public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
            creator.createReadArr(target, logicArray, index);
        }

        @Override
        public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
            creator.createWriteArr(value, logicArray, index);
        }

        @Override
        public SourcePosition sourcePosition() {
            return sourcePosition;
        }

        @Override
        public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
            valueSetter.accept(transferVariable);
            creator.createWriteArr(transferVariable, logicArray, index);
        }

        @Override
        public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
            return transferVariable;
        }

        @Override
        public void storeValue(ContextfulInstructionCreator creator) {
            creator.createWriteArr(transferVariable, logicArray, index);
        }
    }
}
