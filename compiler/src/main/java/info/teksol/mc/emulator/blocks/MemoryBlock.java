package info.teksol.mc.emulator.blocks;

import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;

@NullMarked
public class MemoryBlock extends MindustryBuilding {
    private final double[] memory;

    private MemoryBlock(String name, BlockType type, BlockPosition position, double[] array) {
        super(name, type, position);
        memory = array;
    }

    public MemoryBlock(String name, BlockType type, BlockPosition position, int size) {
        super(name, type, position);
        memory = new double[size];
    }

    public int size() {
        return memory.length;
    }

    public double read(int index) {
        return memory[checkIndex(index)];
    }

    public void write(int index, double value) {
        memory[checkIndex(index)] = value;
    }

    private int checkIndex(int index) {
        if (index < 0 || index >= memory.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("Memory access out of bounds: index %d, memory size %d.", index, memory.length));
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
