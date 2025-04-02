package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
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
    /// Provides a text representation of the contained value as if printed by Mindustry Logic
    /// Supported only for compile-time constants. Is not the same as an mlog representation!
    ///
    /// @param instructionProcessor instruction processor to use for version-dependent formatting
    /// @return a text representation of the contained value
    String format(@Nullable InstructionProcessor instructionProcessor);

    // ValueStore methods

    @Override
    default LogicValue getValue(ContextfulInstructionCreator creator) {
        return this;
    }

    @Override
    default void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
        creator.createSet(target, this);
    }

    @Override
    default void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
        throw new MindcodeInternalError("Cannot modify readable value.");
    }

    @Override
    default void setValue(ContextfulInstructionCreator creator, LogicValue value) {
        throw new MindcodeInternalError("Cannot modify readable value.");
    }

    @Override
    default boolean isComplex() {
        return false;
    }
}
