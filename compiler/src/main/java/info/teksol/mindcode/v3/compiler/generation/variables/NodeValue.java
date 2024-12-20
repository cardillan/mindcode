package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.generation.CodeBuilder;

import java.util.function.Consumer;

/// Represents a read-write or read-only value (l-value or r-value). L-values are variables, including external ones,
/// and, in the future, array or structure elements. R-values are all l-values plus constants and parameters.
///
/// Instances of this class are used by the code generator to represent and pass around the individual nodes
/// of the AST tree (excluding nodes representing fragments of larger syntactic elements, which are only used
/// in specific contexts).
public interface NodeValue {

    /// @return true if this instance is writable (can accept new values)
    default boolean isWritable() {
        return false;
    }

    /// @return value maintained by this instance
    LogicValue getValue(CodeBuilder codeBuilder);

    /// Sets the l-value to the given value, creating the necessary instructions for it.
    ///
    /// @param value value to set
    void setValue(CodeBuilder codeBuilder, LogicValue value);

    /// Writes the value into the l-value represented by this instance.
    ///
    /// The consumer is responsible for generating code that will set the given logic variable
    /// to the value to be written. The method may then generate additional instructions to
    /// facilitate the write operation.
    ///
    /// @param valueSetter consumer responsible for setting the passed-in variable to the value to be written
    void writeValue(CodeBuilder codeBuilder, Consumer<LogicVariable> valueSetter);
}
