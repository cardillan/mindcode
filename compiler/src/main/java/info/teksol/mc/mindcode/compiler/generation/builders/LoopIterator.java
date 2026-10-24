package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.LargeArray;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

///  Represents an iterator variable in the loop
@NullMarked
final class LoopIterator {
    public final boolean out;
    public final ValueStore var;
    public final boolean descending;

    // Ranged loop over (large) array support
    public final int iteratorCount;
    public final int iteratorIndex;
    public @Nullable ArrayStore array;
    public @Nullable LogicVariable memoryVariable;
    public @Nullable LogicVariable indexVariable;
    public boolean updateMemory = true;
    public boolean updateIndex = true;
    public int bodyIteratorValue = Integer.MIN_VALUE;

    Consumer<CodeAssembler> indexDerivation = _ -> {};

    // For determining whether memory variables of different iterators can be merged
    final List<LogicVariable> memoryValues = new ArrayList<>();

    int blockSize;
    int index = 0;

    public LoopIterator(boolean out, ValueStore var, boolean descending, int iteratorCount, int iteratorIndex) {
        this.out = out;
        this.var = var;
        this.descending = descending;
        this.iteratorCount = iteratorCount;
        this.iteratorIndex = iteratorIndex;
    }

    public void setArray(ArrayStore array) {
        this.array = array;
        if (array instanceof LargeArray a) {
            blockSize = a.getBlockSize();
        } else {
            blockSize = array.size();
        }
    }

    public ArrayStore array() {
        assert array != null;
        return array;
    }

    public LargeArray largeArray() {
        assert array instanceof LargeArray;
        return (LargeArray) array;
    }

    public LogicVariable memoryVariable() {
        assert memoryVariable != null;
        return memoryVariable;
    }

    public LogicVariable indexVariable() {
        assert indexVariable != null;
        return indexVariable;
    }

    public LogicVariable memoryValue() {
        LargeArray array = largeArray();
        return array.getMemoryBlock(index - array.getStartOffset());
    }

    public int indexValue() {
        return switch (array) {
            case LargeArray a -> a.getMemoryIndex(index - a.getStartOffset());
            case ArrayStore _ -> index;
            case null -> throw new IllegalStateException();
        };
    }

    public boolean isCompatible(LoopIterator other) {
        return descending == other.descending && blockSize == other.blockSize && array().getStartOffset() == other.array().getStartOffset()
                && iteratorCount == other.iteratorCount && iteratorIndex == other.iteratorIndex;
    }

    public void setIteration(int iteration) {
        ArrayStore array = array();
        int ascIteration = descending ? array.size() / iteratorCount - iteration - 1 : iteration;
        index = array.getStartOffset() + ascIteration * iteratorCount + iteratorIndex;
    }

    public int nextIteration() {
        // Computing the minimal offset before switching to the next block:
        // Descending: looking for the minimal offset such that (index - offset * iteratorCount) / blockSize < index / blockSize.
        // Ascending:  looking for the minimal offset such that (index + offset * iteratorCount) / blockSize > index / blockSize.
        int offset = descending
                ? -((index % blockSize) / iteratorCount + 1)
                : (blockSize - index % blockSize + iteratorCount - 1) / iteratorCount;

        // Add the offset
        int nextIndex = index + iteratorCount * offset;

        // Convert to a raw index and then to iteration
        return descending
                ? (array().getStartOffset() + array().size() - nextIndex - 1) / iteratorCount
                : (nextIndex - array().getStartOffset()) / iteratorCount;
    }

    public void setValue(CodeAssembler assembler, LogicValue value) {
        var.setValue(assembler, value);
    }

    public LogicValue getValue(CodeAssembler assembler) {
        return var.getValue(assembler);
    }

    /// Tries to derive the index variable from this iterator's variable. If a derivation is found, the proper
    /// derivation is stored in indexDerivation.
    ///
    /// @return true when successful
    public boolean derive(LoopIterator from) {
        // If compatible, they have the same value and nothing is needed
        if (isCompatible(from)) return true;

        if (descending == from.descending) {
            // Both are ascending or descending. Are they the same?
            if (iteratorCount == from.iteratorCount) {
                // Same iterator count. Possible offset?
                int originalOffset = from.array().getStartOffset() + from.iteratorIndex;
                int derivedOffset = array().getStartOffset() + iteratorIndex;

                indexDerivation = assembler -> assembler.createOp(Operation.ADD,
                        indexVariable(), from.indexVariable(), LogicNumber.create(derivedOffset - originalOffset));
                return true;
            } else if (array().getStartOffset() + iteratorIndex == 0 && from.array().getStartOffset() + from.iteratorIndex == 0) {
                // Different iterator count, but same base: multiplication!
                if (iteratorCount % from.iteratorCount == 0) {
                    indexDerivation = assembler -> assembler.createOp(Operation.MUL,
                            indexVariable(), from.indexVariable(), LogicNumber.create(iteratorCount / from.iteratorCount));
                    return true;
                }
            }
        } else if (iteratorCount == from.iteratorCount) {
            // One of them is descending
            int originalMin = from.array().getStartOffset();
            int originalMax = originalMin + from.array().size() - iteratorCount;
            int derivedMin = array().getStartOffset();
            int derivedMax = derivedMin + array().size() - iteratorCount;
            int offset = descending ? originalMax + derivedMin : derivedMax + originalMin;

            indexDerivation = assembler -> assembler.createOp(Operation.SUB,
                    indexVariable(), LogicNumber.create(offset), from.indexVariable());
            return true;
        }

        return false;
    }
}
