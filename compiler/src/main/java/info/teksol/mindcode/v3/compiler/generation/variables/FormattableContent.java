package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.logic.LogicNull;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.generation.CodeAssembler;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
public class FormattableContent implements ValueStore {
    private final InputPosition inputPosition;
    private final List<ValueStore> parts;

    public FormattableContent(InputPosition inputPosition, List<ValueStore> parts) {
        this.inputPosition = inputPosition;
        this.parts = parts;
    }

    public InputPosition inputPosition() {
        return inputPosition;
    }

    public List<ValueStore> getParts() {
        return parts;
    }

    @Override
    public boolean isComplex() {
        // This class can never be used in a context where isComplex matters.
        throw new MindcodeInternalError("Unsupported for FormattableContent.");
    }

    @Override
    public boolean isLvalue() {
        return false;
    }

    @Override
    public LogicValue getValue(CodeAssembler assembler) {
        assembler.error(inputPosition, "A formattable string literal can only be used as a first argument to the print or remark function.");
        return LogicNull.NULL;
    }

    @Override
    public void setValue(CodeAssembler assembler, LogicValue value) {
        throw new MindcodeInternalError("Unsupported for FormattableContent.");
    }

    @Override
    public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        throw new MindcodeInternalError("Unsupported for FormattableContent.");
    }
}
