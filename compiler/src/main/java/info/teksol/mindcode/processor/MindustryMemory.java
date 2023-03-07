package info.teksol.mindcode.processor;

import java.util.Arrays;

import static info.teksol.mindcode.processor.ProcessorFlag.ERR_MEMORY_ACCESS;

public class MindustryMemory extends MindustryObject {
    private final double[] memory;

    private MindustryMemory(String name, String value, double[] array) {
        super(name, value);
        memory = array;
    }

    public MindustryMemory(String name, String value, int size) {
        super(name, value);
        memory = new double[size];
    }

    @Override
    public double read(int index) {
        return memory[checkIndex(index)];
    }

    @Override
    public void write(int index, double value) {
        memory[checkIndex(index)] = value;
    }

    private int checkIndex(int index) {
        if (index < 0 || index >= memory.length) {
            throw new ExecutionException(ERR_MEMORY_ACCESS, "Memory access out of bounds: index " + index + ", memory size " + memory.length);
        }
        return index;
    }

    public static MindustryMemory createMemoryCell(String name) {
        return new MindustryMemory(name, "cell", 64);
    }

    public static MindustryMemory createMemoryCell(String name, double[] array) {
        return new MindustryMemory(name, "cell", Arrays.copyOf(array, 64));
    }

    public static MindustryMemory createMemoryBank(String name) {
        return new MindustryMemory(name, "bank", 512);
    }

    public static MindustryMemory createMemoryBank(String name, double[] array) {
        return new MindustryMemory(name, "bank", Arrays.copyOf(array, 512));
    }
}
