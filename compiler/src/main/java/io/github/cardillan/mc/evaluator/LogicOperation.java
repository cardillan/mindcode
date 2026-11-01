package io.github.cardillan.mc.evaluator;

import io.github.cardillan.mc.emulator.MlogReadable;
import io.github.cardillan.mc.emulator.MlogWritable;
import org.jspecify.annotations.NullMarked;

/// Interface representing a Mindustry Logic operation.
@NullMarked
public interface LogicOperation {
    void execute(MlogWritable result, MlogReadable left, MlogReadable right);
}
