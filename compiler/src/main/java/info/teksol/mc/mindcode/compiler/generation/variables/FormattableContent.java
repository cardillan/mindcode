package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.InputPosition;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstExpression;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.arguments.LogicNull;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
public class FormattableContent implements ValueStore {
    private final InputPosition inputPosition;
    private final List<AstExpression> parts;

    public FormattableContent(InputPosition inputPosition, List<AstExpression> parts) {
        this.inputPosition = inputPosition;
        this.parts = parts;
    }

    public InputPosition inputPosition() {
        return inputPosition;
    }

    public List<AstExpression> getParts() {
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