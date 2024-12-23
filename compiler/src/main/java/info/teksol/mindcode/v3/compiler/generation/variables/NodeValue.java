package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.generation.Assembler;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/// Represents a read-write or read-only value (l-value or r-value). L-values are variables, including external ones,
/// and, in the future, array or structure elements. R-values are all l-values plus constants and parameters.
///
/// Some nodes might not be even r-values (e.g. a formattable string literal). These must be specifically
/// handled when needed (within the context on an inline function call, for example). Such implementations should
/// emit errors when {@code getValue} is called (this will prevent the instance from being assigned to an mlog
/// variable) and provide a different mechanism for accessing the contained information.
///
/// Instances of this class are used by the code generator to represent and pass around the individual nodes
/// of the AST tree (excluding nodes representing fragments of larger syntactic elements, which are only used
/// in specific contexts).
@NullMarked
public interface NodeValue {

    /// Indicates whether an `in` modifier was used on the syntactic element this NodeValue was created from.
    /// At this moment, only function arguments can have `in`/`out` modifiers.
    ///
    /// @return true if this node value represents an `in` function argument
    default boolean hasInModifier() {
        return true;
    }

    /// Indicates whether an `out` modifier was used on the syntactic element this NodeValue was created from.
    /// At this moment, only function arguments can have `in`/`out` modifiers.
    ///
    /// @return true if this node value represents an `out` function argument
    default boolean hasOutModifier() {
        return true;
    }

    /// L-values can be targets of assignments and increment/decrement operators.
    /// **Temporary variables aren't l-values.**
    ///
    /// The definition is separate from LogicArgument.isWritable, because temporary variables are generally
    /// writable, but they don't represent l-values. When they do, they need a special NodeValue implementation
    /// to handle reading and writing values anyway.
    ///
    /// @return true if this instance represents an l-value
    boolean isLvalue();

    /// Returns a value represented by this instance.
    ///
    /// Some node values might not be R-values (formattable strings or vararg identifiers, for example.)
    /// These values should emit errors when they're being read. They need special handling based on their
    /// type.
    ///
    /// @param assembler assembler instance used to produce code for obtaining the value, if needed
    /// @return value maintained by this instance
    LogicValue getValue(Assembler assembler);

    /// Sets the l-value to the given value, creating the necessary instructions for it.
    ///
    /// @param assembler assembler instance used to produce code for setting the value, if needed
    /// @param value value to set
    void setValue(Assembler assembler, LogicValue value);

    /// Writes the value into the l-value represented by this instance.
    ///
    /// The consumer is responsible for generating code that will set the given logic variable
    /// to the value to be written. The method may then generate additional instructions to
    /// facilitate the write operation.
    ///
    /// @param assembler assembler instance used to produce code for setting the value, if needed
    /// @param valueSetter consumer responsible for setting the passed-in variable to the value to be written
    void writeValue(Assembler assembler, Consumer<LogicVariable> valueSetter);

    // TODO Review
    default boolean hasModifier() {
        return hasInModifier() || hasOutModifier();
    }
    default boolean hasInModifierOnly() {
        return hasInModifier() && !hasOutModifier();
    }
}
