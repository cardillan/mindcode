package info.teksol.mindcode.processor;

import static info.teksol.mindcode.processor.ProcessorFlag.ERR_UNSUPPORTED_BLOCK_OPERATION;

public class MindustryObject {
    private final String name;
    private final String value;

    public MindustryObject(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public double read(int index) {
        throw new ExecutionException(ERR_UNSUPPORTED_BLOCK_OPERATION, "Unsupported operation read on " + name + " (class " + getClass().getSimpleName() + ")");
    }

    public void write(int index, double value) {
        throw new ExecutionException(ERR_UNSUPPORTED_BLOCK_OPERATION, "Unsupported operation write on " + name + " (class " + getClass().getSimpleName() + ")");
    }
}
