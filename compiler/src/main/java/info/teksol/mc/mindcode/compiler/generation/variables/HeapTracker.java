package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.CompilerMessageEmitter;
import info.teksol.mc.mindcode.compiler.Modifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.util.IntRange;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/// Tracks the heap usage/external variables' storage. Creates heap variables and assigns indexes to them.
@NullMarked
public class HeapTracker extends CompilerMessageEmitter {
    private final InstructionProcessor processor;
    private final NameCreator nameCreator;
    private final boolean heapAllocated;
    private final List<LogicVariable> heapStorage;
    private final @Nullable ElementsArrayStore memoryArray;
    private final int[] currentHeapIndex;
    private final int[] endHeapIndex;
    private final int blockSize;

    private HeapTracker(CodeGeneratorContext context, List<LogicVariable> heapStorage, int startHeapIndex, int endHeapIndex,
            @Nullable ElementsArrayStore memoryArray) {
        super(context.messageConsumer());
        this.processor = context.instructionProcessor();
        this.nameCreator = context.nameCreator();
        this.heapStorage = heapStorage;
        this.memoryArray = memoryArray;
        this.currentHeapIndex = new int[heapStorage.size()];
        this.endHeapIndex = new int[heapStorage.size()];
        this.heapAllocated = true;
        Arrays.fill(this.currentHeapIndex, startHeapIndex);
        Arrays.fill(this.endHeapIndex, endHeapIndex);
        this.blockSize = heapStorage.size() <= 1 ? Integer.MAX_VALUE : endHeapIndex;
    }

    private HeapTracker(VariablesContext context) {
        super(context.messageConsumer());
        this.processor = context.instructionProcessor();
        this.nameCreator = context.nameCreator();
        this.heapStorage = List.of();
        this.memoryArray = null;
        this.currentHeapIndex = new int[]{0};
        this.endHeapIndex = new int[]{Integer.MAX_VALUE};
        this.heapAllocated = false;
        this.blockSize = Integer.MAX_VALUE;
    }

    public static HeapTracker createDefaultTracker(VariablesContext context) {
        return new HeapTracker(context);
    }

    public static HeapTracker createTracker(CodeGeneratorContext context, List<LogicVariable> heapMemory, int startHeapIndex, int endHeapIndex,
            @Nullable ElementsArrayStore memoryArray) {
        return new HeapTracker(context, heapMemory, startHeapIndex, endHeapIndex, memoryArray);
    }

    private int findFreeStorageIndex(int size) {
        int bestIndex = -1;
        int bestSize = 0;

        for (int i = 0; i < heapStorage.size(); i++) {
            if (currentHeapIndex[i] + size <= endHeapIndex[i]) {
                return i;
            } else if (endHeapIndex[i] - currentHeapIndex[i] > bestSize) {
                bestIndex = i;
                bestSize = endHeapIndex[i] - currentHeapIndex[i];
            }
        }

        return bestIndex;
    }

    public ValueStore createVariable(AstIdentifier identifier, Modifiers modifiers) {
        if (!heapAllocated) {
            error(identifier, ERR.EXT_STORAGE_MISSING_HEAP);
        }

        LogicValue index;
        LogicVariable memory;
        int slot = findFreeStorageIndex(1);
        if (slot < 0 || currentHeapIndex[slot] >= endHeapIndex[slot]) {
            if (heapAllocated) error(identifier, ERR.EXT_STORAGE_EXCEEDED, identifier.getName());
            memory = LogicVariable.INVALID;
            index = LogicVariable.INVALID;
        } else {
            memory = heapStorage.get(slot);
            index = LogicNumber.create(currentHeapIndex[slot]++);
        }

        return modifiers.contains(Modifier.CACHED)
                ? new ExternalCachedVariable(identifier.sourcePosition(), memory, index,
                LogicVariable.global(identifier, nameCreator.global(identifier.getName())))
                : new ExternalVariable(identifier.sourcePosition(), memory, index, processor.nextTemp());
    }

    public ArrayStore createArray(AstIdentifier identifier, int size) {
        if (!heapAllocated) {
            error(identifier, ERR.EXT_STORAGE_MISSING_HEAP);
        }

        if (size > blockSize) {
            int blocks = (size + blockSize - 1) / blockSize;
            int unallocated = size;
            List<LogicVariable> memory = new ArrayList<>();
            for (int i = 0; i < heapStorage.size(); i++) {
                if (currentHeapIndex[i] == 0) {
                    memory.add(heapStorage.get(i));
                    currentHeapIndex[i] = Math.min(unallocated, blockSize);
                    unallocated -= blockSize;
                    if (--blocks <= 0) break;
                }
            }
            if (blocks > 0 && heapAllocated) {
                error(identifier, ERR.EXT_STORAGE_EXCEEDED_ARRAY, identifier.getName(), size, memory.size() * blockSize);
            }

            // Create an internal array of the memory blocks
            InternalArray memoryArray = InternalArray.createConst(nameCreator, null, identifier, memory);

            return new LargeArray(identifier.sourcePosition(),
                    nameCreator.arrayBase(null, "", identifier.getName(), 0), memory, memoryArray, blockSize, size);
        } else {
            int slot = findFreeStorageIndex(size);
            int available = slot < 0 ? 0 : endHeapIndex[slot] - currentHeapIndex[slot];

            if (available < size) {
                if (heapAllocated) error(identifier, ERR.EXT_STORAGE_EXCEEDED_ARRAY, identifier.getName(), size, available);
                return ExternalArray.createInvalid(identifier, size);
            } else {
                LogicVariable memory = heapStorage.get(slot);
                int startOffset = currentHeapIndex[slot];
                currentHeapIndex[slot] += size;
                return ExternalArray.create(processor, identifier.sourcePosition(), nameCreator.arrayBase(null, "",
                        identifier.getName(), 0), memory, IntRange.halfOpen(startOffset, startOffset + size));
            }
        }
    }
}
