package info.teksol.mc.evaluator;

import org.jspecify.annotations.NullMarked;

/// Interface representing a Mindustry Logic condition.
@NullMarked
public interface LogicCondition {
    boolean evaluate(LogicReadable a, LogicReadable b);
}
