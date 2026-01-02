package info.teksol.mc.evaluator;

import info.teksol.mc.emulator.v2.MlogReadable;
import info.teksol.mc.emulator.v2.MlogWritable;
import org.jspecify.annotations.NullMarked;

/// Interface representing a Mindustry Logic operation.
@NullMarked
public interface LogicOperation {
    void execute(MlogWritable result, MlogReadable left, MlogReadable right);
}
