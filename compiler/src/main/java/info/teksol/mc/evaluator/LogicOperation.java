package info.teksol.mc.evaluator;

/**
 * Interface representing a Mindustry Logic operation.
 */
public interface LogicOperation {
    void execute(LogicWritable result, LogicReadable left, LogicReadable right);
}
