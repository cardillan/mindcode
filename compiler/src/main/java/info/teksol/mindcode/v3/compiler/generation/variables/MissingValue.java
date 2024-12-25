package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.generation.CodeAssembler;
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
public class MissingValue implements NodeValue {
    public static final MissingValue UNSPECIFIED_FUNCTION_ARGUMENT = new MissingValue(true);
    public static final MissingValue FORMATTABLE_PLACEHOLDER = new MissingValue(false);

    private final boolean ignoreWrites;

    private MissingValue(boolean ignoreWrites) {
        this.ignoreWrites = ignoreWrites;
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
    public void setValue(CodeAssembler assembler, LogicValue value) {
        if (!ignoreWrites) {
            throw new MindcodeInternalError("Cannot modify this instance.");
        }
    }

    @Override
    public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        if (!ignoreWrites) {
            throw new MindcodeInternalError("Cannot modify this instance.");
        }
    }
}
