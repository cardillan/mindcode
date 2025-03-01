package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/// Contains information about instruction's side effects and applies it when necessary. A side effect causes
/// variables other than function arguments to be read or written when the instruction is executed.
@NullMarked
public interface SideEffects {

    /// Applies the side effect by passing read variables into the `reads` consumer and written variables
    /// into the `writes` consumer. The actual values being read or written aren't known.
    ///
    /// @param reads  consumer accepting variables that are read as a side effect
    /// @param writes consumer accepting variables that are written as a side effect
    /// @param resets consumer accepting variables that are reset (possibly modified) as a side effect
    void apply(Consumer<LogicVariable> reads, Consumer<LogicVariable> writes, Consumer<LogicVariable> resets);

    /// Creates a side effect which does nothing
    static SideEffects none() {
        return (reads, writes, resets) -> {};
    }

    /// Creates a side effect which reads all variables from the list
    static SideEffects reads(Iterable<LogicVariable> variables) {
        return (reads, writes, resets) -> variables.forEach(reads);
    }

    /// Creates a side effect which writes all variables from the list
    static SideEffects writes(Iterable<LogicVariable> variables) {
        return (reads, writes, resets) -> variables.forEach(writes);
    }

    /// Creates a side effect with reads, writes and resets
    static SideEffects of(Iterable<LogicVariable> reads, Iterable<LogicVariable> writes, Iterable<LogicVariable> resets) {
        return (read, write, reset) -> {
            reads.forEach(read);
            writes.forEach(write);
            resets.forEach(reset);
        };
    }
}
