package info.teksol.mc.emulator.blocks;

import info.teksol.mc.emulator.processor.ExecutionException;
import info.teksol.mc.mindcode.logic.mimex.BlockType;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;

import static info.teksol.mc.emulator.processor.ExecutionFlag.ERR_MEMORY_ACCESS;

@NullMarked
public class Memory extends MindustryBlock {
    private final double[] memory;

    private Memory(String name, BlockType type, double[] array) {
        super(name, type);
        memory = array;
    }

    public Memory(String name, BlockType type, int size) {
        super(name, type);
        memory = new double[size];
    }

    public double read(int index) {
        return memory[checkIndex(index)];
    }

    public void write(int index, double value) {
        memory[checkIndex(index)] = value;
    }

    private int checkIndex(int index) {
        if (index < 0 || index >= memory.length) {
            throw new ExecutionException(ERR_MEMORY_ACCESS, "Memory access out of bounds: index %d, memory size %d.", index, memory.length);
        }
        return index;
    }

    public static Memory createMemoryCell() {
        return new Memory("cell", BlockType.existing("@memory-cell"), 64);
    }

    public static Memory createMemoryCell(double[] array) {
        return new Memory("cell", BlockType.existing("@memory-cell"), Arrays.copyOf(array, 64));
    }

    public static Memory createMemoryBank() {
        return new Memory("bank", BlockType.existing("@memory-bank"), 512);
    }

    public static Memory createMemoryBank(double[] array) {
        return new Memory("bank", BlockType.existing("@memory-bank"), Arrays.copyOf(array, 512));
    }
}
