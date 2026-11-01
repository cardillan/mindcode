package io.github.cardillan.mc.evaluator;

import io.github.cardillan.mc.emulator.MlogReadable;
import org.jspecify.annotations.NullMarked;

/// Interface representing a Mindustry Logic condition.
@NullMarked
public interface LogicCondition {
    boolean evaluate(MlogReadable a, MlogReadable b);
}
