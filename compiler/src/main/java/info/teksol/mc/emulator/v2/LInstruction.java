package info.teksol.mc.emulator.v2;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface LInstruction {

    /// @return true to continue the execution, false to stop
    boolean run();
}
