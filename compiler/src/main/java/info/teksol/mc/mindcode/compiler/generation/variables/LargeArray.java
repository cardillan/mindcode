package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.profile.LocalCompilerProfile;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.TMP_VARIABLE;

@NullMarked
public class LargeArray extends AbstractArrayStore {
    private final List<LogicVariable> memoryBlocks;
    private final ArrayStore memoryArray;
    private final int blockSize;
    private final LogicNumber blockSizeNumber;

    public LargeArray(SourcePosition sourcePosition, String name, List<LogicVariable> memoryBlocks, ArrayStore memoryArray, int blockSize, int size) {
        super(sourcePosition, name, ArrayType.LARGE, 0, size, null, false);
        this.memoryBlocks = memoryBlocks;
        this.memoryArray = memoryArray;
        this.blockSize = blockSize;
        this.blockSizeNumber = LogicNumber.create(blockSize);
    }

    private LargeArray(SourcePosition sourcePosition, String name, ArrayType arrayType, int startOffset, int size, ArrayStore masterArray,
            boolean subarray, List<LogicVariable> memoryBlocks, ArrayStore memoryArray, int blockSize, LogicNumber blockSizeNumber) {
        super(sourcePosition, name, arrayType, startOffset, size, masterArray, subarray);
        this.memoryBlocks = memoryBlocks;
        this.memoryArray = memoryArray;
        this.blockSize = blockSize;
        this.blockSizeNumber = blockSizeNumber;
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
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        LogicVariable memory = memoryBlocks.get(index / blockSize);
        return new ExternalVariable(sourcePosition, memory, LogicNumber.create(index % blockSize), creator.nextTemp());
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, SourcePosition sourcePosition, ValueStore index, boolean safeAccess) {
        if (startOffset == 0) {
            LogicValue fixedIndex = creator.defensiveCopy(index, TMP_VARIABLE);
            return new LargeArrayElement(sourcePosition, fixedIndex, creator.nextTemp(), safeAccess);
        } else {
            LogicVariable actualIndex = creator.nextTemp();
            return new LargeArrayElement(sourcePosition, actualIndex, creator.nextTemp(), safeAccess);
        }
    }

    @Override
    public LargeArray withSourcePosition(SourcePosition sourcePosition) {
        return new LargeArray(sourcePosition, name, arrayType, startOffset, size, masterArray, false,
                memoryBlocks, memoryArray, blockSize, blockSizeNumber);
    }

    public class LargeArrayElement implements ValueStore {
        private final SourcePosition sourcePosition;
        private final LogicValue index;
        private final LogicVariable transferVariable;
        private final boolean safeAccess;

        public LargeArrayElement(SourcePosition sourcePosition, LogicValue index, LogicVariable transferVariable, boolean safeAccess) {
            this.sourcePosition = sourcePosition;
            this.index = index;
            this.transferVariable = transferVariable;
            this.safeAccess = safeAccess;
        }

        @Override
        public SourcePosition sourcePosition() {
            return sourcePosition;
        }

        @Override
        public boolean isComplex() {
            return true;
        }

        @Override
        public boolean isLvalue() {
            return true;
        }

        private void access(ContextfulInstructionCreator creator, BiConsumer<LogicVariable, LogicVariable> accessor) {
            LocalCompilerProfile profile = creator.getAstContext().getLocalProfile();
            if (!safeAccess && profile.isBoundaryChecks()) {
                int maxIndex = size - 1;
                String errorMessage = String.format("%s: index out of bounds (%d to %d)", creator.getAstContext().sourcePosition().formatForMlog(), 0, maxIndex);

                LogicInstruction instruction = creator.createAssertBounds( LogicKeyword.create("multiple"), LogicNumber.create(1),
                        LogicNumber.ZERO, Condition.LESS_THAN_EQ, index, Condition.LESS_THAN_EQ,
                        LogicNumber.create(maxIndex), LogicString.createRaw(errorMessage));
                instruction.setNonNegativeInt(index);
            }
            LogicVariable elementIndex = creator.nextTemp();
            LogicVariable blockIndex = creator.nextTemp();
            LogicVariable memory = creator.nextTemp();
            creator.createOp(Operation.MOD, elementIndex, index, blockSizeNumber);
            creator.createOp(Operation.IDIV, blockIndex, index, blockSizeNumber);
            creator.createReadArr(memory, memoryArray.getLogicArray(), blockIndex).setSafeAccess(true);
            accessor.accept(memory, elementIndex);
        }

        @Override
        public LogicValue getValue(ContextfulInstructionCreator creator) {
            access(creator, (memory, elementIndex) -> creator.createRead(transferVariable, memory, elementIndex).setSafeAccess(safeAccess));
            return transferVariable;
        }

        @Override
        public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
            access(creator, (memory, elementIndex) -> creator.createRead(target, memory, elementIndex).setSafeAccess(safeAccess));
        }

        @Override
        public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
            access(creator, (memory, elementIndex) -> creator.createWrite(value, memory, elementIndex).setSafeAccess(safeAccess));
        }

        @Override
        public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
            valueSetter.accept(transferVariable);
            access(creator, (memory, elementIndex) -> creator.createWrite(transferVariable, memory, elementIndex).setSafeAccess(safeAccess));
        }

        @Override
        public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
            return transferVariable;
        }

        @Override
        public void storeValue(ContextfulInstructionCreator creator) {
            access(creator, (memory, elementIndex) -> creator.createWrite(transferVariable, memory, elementIndex).setSafeAccess(safeAccess));
        }
    }
}
