package info.teksol.evaluator;

/**
 * Interface representing a Mindustry Logic operation.
 */
public interface LogicOperation {
    void execute(LogicWritable result, LogicReadable a, LogicReadable b);
}
