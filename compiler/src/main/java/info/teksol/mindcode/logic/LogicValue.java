package info.teksol.mindcode.logic;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.v3.compiler.generation.CodeBuilder;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;

import java.util.function.Consumer;

/// Represents a value that can be read by mlog instructions.
///
/// This interface extends the NodeValue interface, since it is often possible to represent the value nf an AST node
/// as a LogicValue.
public interface LogicValue extends LogicArgument, NodeValue {
    /**
     * Indicates that an expressions using the value can be numerically evaluated at compile time.
     * All literals can be evaluated. Built-in constants can only be evaluated if they aren't a variable
     * (e.g. @coal can be evaluated, but @thisx cannot).
     *
     * @return true if the expression can be evaluated.
     */
    boolean canEvaluate();

    /** @return true if the value is a compile-time constant */
    boolean isConstant();

    /**
     * Provides a text representation of the contained value as if printed by Mindustry Logic
     * Supported only for compile-time constants. Is not the same as an mlog representation!
     *
     * @param instructionProcessor instruction processor to use for version-dependent formats
     * @return a text representation of the contained value
     */
    String format(InstructionProcessor instructionProcessor);

    // NodeValue methods

    @Override
    default boolean isWritable() {
        return false;
    }

    @Override
    default LogicValue getValue(CodeBuilder codeBuilder) {
        return this;
    }

    @Override
    default void writeValue(CodeBuilder codeBuilder, Consumer<LogicVariable> valueSetter) {
        throw new MindcodeInternalError("Cannot modify readable value.");
    }

    @Override
    default void setValue(CodeBuilder codeBuilder, LogicValue value) {
        throw new MindcodeInternalError("Cannot modify readable value.");
    }
}
