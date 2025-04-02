package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.logic.arguments.LogicNull;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.intellij.lang.annotations.Subst;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/// Base class for compound value stores. These value stores do not map to a single value and need
/// specific handling in all contexts
@NullMarked
public abstract class CompoundValueStore implements ValueStore {
    @Subst(ERR.FORMATTABLE_FORBIDDEN)
    private final String errorMessage;

    protected final SourcePosition sourcePosition;

    protected CompoundValueStore(SourcePosition sourcePosition, String errorMessage) {
        this.sourcePosition = sourcePosition;
        this.errorMessage = errorMessage;
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
    }

    public boolean isMlogRepresentable() {
        return false;
    }

    @Override
    public boolean isComplex() {
        // This class can never be used in a context where isComplex matters.
        throw new MindcodeInternalError("Unsupported for " + getClass().getSimpleName());
    }

    @Override
    public boolean isLvalue() {
        return false;
    }

    @Override
    public LogicValue getValue(ContextfulInstructionCreator creator) {
        creator.error(sourcePosition, errorMessage);
        return LogicNull.NULL;
    }

    @Override
    public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
        creator.error(sourcePosition, errorMessage);
    }

    @Override
    public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
        throw new MindcodeInternalError("Unsupported for " + getClass().getSimpleName());
    }

    @Override
    public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
        throw new MindcodeInternalError("Unsupported for " + getClass().getSimpleName());
    }
}
