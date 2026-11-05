package io.github.cardillan.mindcode.evaluator;

import io.github.cardillan.mindcode.emulator.MlogReadable;
import org.jspecify.annotations.NullMarked;

/// Interface representing a Mindustry Logic condition.
@NullMarked
public interface LogicCondition {
    boolean evaluate(MlogReadable a, MlogReadable b);
}
