package info.teksol.evaluator;

/**
 * Interface representing a Mindustry Logic condition.
 */
public interface LogicCondition {
    boolean evaluate(LogicReadable a, LogicReadable b);
}
