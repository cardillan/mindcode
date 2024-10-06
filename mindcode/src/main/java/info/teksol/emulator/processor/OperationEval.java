package info.teksol.emulator.processor;

/**
 * Mindustry processor operation.
 */
public interface OperationEval {
    void execute(LogicWritable result, LogicReadable a, LogicReadable b);
}
