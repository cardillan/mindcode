package info.teksol.mc.mindcode.compiler.evaluator;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLiteral;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLiteralFloat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/// IntermediateValue holds an intermediate result of compile-time expression evaluation.
/// The intermediate result might not have an mlog representation, and as such cannot be stored
/// in `AstLiteralFloat`.
@NullMarked
public class IntermediateValue extends AstLiteralFloat {
    private final double value;

    public IntermediateValue(SourcePosition sourcePosition, double value) {
        super(sourcePosition, Double.toString(value));
        this.value = value;
    }

    public double getAsDouble() {
        return value;
    }

    public @Nullable AstLiteral toNumericLiteral() {
        return MindcodeCompiler.getCompileTimeEvaluatorContext().instructionProcessor().mlogFormat(sourcePosition(), value, false)
                .map(str -> new AstLiteralFloat(sourcePosition(), str))
                .orElse(null);
    }

    @Override
    public IntermediateValue withSourcePosition(SourcePosition sourcePosition) {
        return new IntermediateValue(sourcePosition, value);
    }
}
