package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/// Represents a value that can be read by mlog instructions.
///
/// This interface extends the ValueStore interface, since it is often possible to represent the value of an AST node
/// as a LogicValue.
@NullMarked
public interface LogicValue extends LogicArgument, ValueStore {
    /// Indicates that an expressions using the value can be numerically evaluated at compile time.
    /// All literals can be evaluated. Built-in constants can only be evaluated if they aren't a variable
    /// (e.g. @coal can be evaluated, but @thisx cannot).
    ///
    /// @return true if the expression can be evaluated
    boolean canEvaluate();

    /// @return true if the value is a compile-time constant
    boolean isConstant();

    /**
     * Provides a text representation of the contained value as if printed by Mindustry Logic
     * Supported only for compile-time constants. Is not the same as an mlog representation!
     *
     * @param instructionProcessor instruction processor to use for version-dependent formats
     * @return a text representation of the contained value
     */
    String format(@Nullable InstructionProcessor instructionProcessor);

    // ValueStore methods

    @Override
    default LogicValue getValue(CodeAssembler assembler) {
        return this;
    }

    @Override
    default void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        throw new MindcodeInternalError("Cannot modify readable value.");
    }

    @Override
    default void setValue(CodeAssembler assembler, LogicValue value) {
        throw new MindcodeInternalError("Cannot modify readable value.");
    }

    @Override
    default boolean isComplex() {
        return false;
    }
}
