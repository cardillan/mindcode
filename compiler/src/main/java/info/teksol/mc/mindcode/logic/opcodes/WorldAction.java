package info.teksol.mc.mindcode.logic.opcodes;

import org.jspecify.annotations.NullMarked;

/// This enum defines in which ways instructions interact with the Mindustry world.
@NullMarked
public enum WorldAction {
    /// No interaction with the world
    NONE,

    /// Reads data from the world
    READS,

    /// Modifies this processor in a way not possible from other processors
    THIS,

    /// Modifies the world
    WRITES,

    /// Both reads and writes the world
    BOTH,
}
