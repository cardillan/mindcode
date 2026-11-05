package io.github.cardillan.mindcode.evaluator;

import io.github.cardillan.mindcode.emulator.MlogReadable;
import io.github.cardillan.mindcode.emulator.MlogWritable;
import org.jspecify.annotations.NullMarked;

/// Interface representing a Mindustry Logic operation.
@NullMarked
public interface LogicOperation {
    void execute(MlogWritable result, MlogReadable left, MlogReadable right);
}
