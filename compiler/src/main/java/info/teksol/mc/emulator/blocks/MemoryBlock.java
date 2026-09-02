package info.teksol.mc.emulator.blocks;

import info.teksol.mc.emulator.LVar;
import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

@NullMarked
public class MemoryBlock extends MindustryBuilding {
    private static final Object NUMERIC_VALUE = new Object();
    private final double[] numVals;
    private final @Nullable Object[] objVals;
    private final LVar accessVar = LVar.create("");

    private MemoryBlock(String name, BlockType type, BlockPosition position, double[] array) {
        super(name, type, position);
        numVals = array;
        objVals = new Object[array.length];
        Arrays.fill(objVals, NUMERIC_VALUE);
    }

    public MemoryBlock(String name, BlockType type, BlockPosition position, int size) {
        super(name, type, position);
        numVals = new double[size];
        objVals = new Object[size];
        Arrays.fill(objVals, NUMERIC_VALUE);
    }

    public int size() {
        return numVals.length;
    }

    public LVar read(int index) {
        checkIndex(index);
        if (objVals[index] == NUMERIC_VALUE) {
            accessVar.setnum(numVals[index]);
        } else {
            accessVar.setobj(objVals[index]);
        }
        return accessVar;
    }

    public void write(int index, LVar value) {
        checkIndex(index);
        if (value.isobj) {
            objVals[index] = value.objval;
        } else {
            objVals[index] = NUMERIC_VALUE;
            numVals[index] = value.numval;
        }
    }

    private int checkIndex(int index) {
        if (index < 0 || index >= numVals.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Memory access out of bounds: index %d, memory size %d.", index, numVals.length));
        }
        return index;
    }

    public static MemoryBlock createMemoryCell(MindustryMetadata metadata, BlockPosition position) {
        return new MemoryBlock("cell", metadata.getExistingBlock("@memory-cell"), position, 64);
    }

    public static MemoryBlock createMemoryCell(MindustryMetadata metadata, BlockPosition position, double[] array) {
        return new MemoryBlock("cell", metadata.getExistingBlock("@memory-cell"), position, Arrays.copyOf(array, 64));
    }

    public static MemoryBlock createMemoryBank(MindustryMetadata metadata, BlockPosition position) {
        return new MemoryBlock("bank", metadata.getExistingBlock("@memory-bank"), position, 512);
    }

    public static MemoryBlock createMemoryBank(MindustryMetadata metadata, BlockPosition position, double[] array) {
        return new MemoryBlock("bank", metadata.getExistingBlock("@memory-bank"), position, Arrays.copyOf(array, 512));
    }
}
