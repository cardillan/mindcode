package info.teksol.mc.evaluator;

import org.jspecify.annotations.NullMarked;

/// Interface representing a Mindustry Logic operation.
@NullMarked
public interface LogicOperation {
    void execute(LogicWritable result, LogicReadable left, LogicReadable right);
}
