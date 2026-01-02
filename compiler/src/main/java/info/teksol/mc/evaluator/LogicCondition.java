package info.teksol.mc.evaluator;

import info.teksol.mc.emulator.MlogReadable;
import org.jspecify.annotations.NullMarked;

/// Interface representing a Mindustry Logic condition.
@NullMarked
public interface LogicCondition {
    boolean evaluate(MlogReadable a, MlogReadable b);
}
