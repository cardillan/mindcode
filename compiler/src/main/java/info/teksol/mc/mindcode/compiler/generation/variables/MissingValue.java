package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/// This class represents a missing value, which may be
///
/// - an unspecified function argument in place of an optional function parameter,
/// - a value placeholder in a formattable string literal.
///
/// The instance cannot be read (and throws errors when trying to). Instance representing unspecified
/// function argument can be written to and the writes are ignored. Instance representing a placeholder
/// can't be written to.
@NullMarked
public class MissingValue implements ValueStore {
    private final SourcePosition sourcePosition;

    public MissingValue(SourcePosition sourcePosition) {
        this.sourcePosition = sourcePosition;
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
    }

    @Override
    public boolean isComplex() {
        return false;
    }

    @Override
    public boolean isLvalue() {
        return true;
    }

    @Override
    public LogicValue getValue(CodeAssembler assembler) {
        throw new MindcodeInternalError("Trying to read an unspecified function argument");
    }

    @Override
    public void readValue(CodeAssembler assembler, LogicVariable target) {
        throw new MindcodeInternalError("Trying to read an unspecified function argument");
    }

    @Override
    public void setValue(CodeAssembler assembler, LogicValue value) {
        throw new MindcodeInternalError("Cannot modify this instance.");
    }

    @Override
    public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        throw new MindcodeInternalError("Cannot modify this instance.");
    }
}
