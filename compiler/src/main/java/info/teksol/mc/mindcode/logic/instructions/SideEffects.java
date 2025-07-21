package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/// Contains information about instruction's side effects and applies it when necessary. A side effect causes
/// variables other than function arguments to be read or written when the instruction is executed.
@NullMarked
public record SideEffects(List<LogicVariable> reads, List<LogicVariable> writes, List<LogicVariable> resets) {
    public static final SideEffects NONE = new SideEffects(List.of(), List.of(), List.of());

    /// Applies the side effect by passing read variables into the `reads` consumer, written variables
    /// into the `writes` consumer and reset variables into the `resets` consumer. The actual values being read
    /// or written aren't known.
    ///
    /// @param reads  consumer accepting variables that are read as a side effect
    /// @param writes consumer accepting variables that are written as a side effect
    /// @param resets consumer accepting variables that are reset (possibly modified) as a side effect
    public void apply(Consumer<LogicVariable> reads, Consumer<LogicVariable> writes, Consumer<LogicVariable> resets) {
        this.reads.forEach(reads);
        this.writes.forEach(writes);
        this.resets.forEach(resets);
    }

    public SideEffects replaceVariables(Map<LogicVariable, LogicValue> valueReplacements) {
        List<LogicVariable> reads = replaceVariables(this.reads, valueReplacements);
        List<LogicVariable> writes = replaceVariables(this.writes, valueReplacements);
        List<LogicVariable> resets = replaceVariables(this.resets, valueReplacements);
        return reads.isEmpty() && writes.isEmpty() && resets.isEmpty() ? NONE
                : reads == this.reads && writes == this.writes && resets == this.resets ? this
                : new SideEffects(reads, writes, resets);
    }

    private List<LogicVariable> replaceVariables(List<LogicVariable> variables, Map<LogicVariable, LogicValue> valueReplacements) {
        if (variables.stream().anyMatch(valueReplacements::containsKey)) {
            return variables.stream()
                    .map(v -> valueReplacements.getOrDefault(v, v))
                    .filter(LogicVariable.class::isInstance)
                    .map(LogicVariable.class::cast)
                    .toList();
        } else {
            return variables;
        }
    }

    /// Creates a side effect which reads all variables from the list
    public static SideEffects reads(List<LogicVariable> variables) {
        return new SideEffects(variables, List.of(), List.of());
    }

    /// Creates a side effect which writes all variables from the list
    public static SideEffects writes(List<LogicVariable> variables) {
        return new SideEffects(List.of(), variables, List.of());
    }

    /// Creates a side effect which resets all variables from the list
    public static SideEffects resets(List<LogicVariable> variables) {
        return new SideEffects(List.of(), List.of(), variables);
    }

    /// Creates a side effect with reads, writes and resets
    public static SideEffects of(List<LogicVariable> reads, List<LogicVariable> writes, List<LogicVariable> resets) {
        return new SideEffects(reads,writes,resets);
    }
}
