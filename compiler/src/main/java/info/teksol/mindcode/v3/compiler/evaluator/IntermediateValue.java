package info.teksol.mindcode.v3.compiler.evaluator;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.v3.MindcodeCompiler;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstLiteral;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstLiteralFloat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/// IntermediateValue holds an intermediate result of compile-time expression evaluation.
/// The intermediate result might not have an mlog representation, and as such cannot be stored
/// in `AstLiteralFloat`.
///
/// Instances of this class are restricted to this package. They're converted to proper
/// `AstLiteralFloat` nodes before being passed outside expression evaluator.
///
/// This class accesses the `ExpressionEvaluatorContext`.
@NullMarked
class IntermediateValue extends AstLiteralFloat {
    private final double value;

    public IntermediateValue(InputPosition inputPosition, double value) {
        super(inputPosition, Double.toString(value));
        this.value = value;
    }

    public double getAsDouble() {
        return value;
    }

    public @Nullable AstLiteral toNumericLiteral() {
        return MindcodeCompiler.getCompileTimeEvaluatorContext().instructionProcessor().mlogFormat(value)
                .map(str -> new AstLiteralFloat(inputPosition(), str))
                .orElse(null);
    }

    @Override
    public IntermediateValue withInputPosition(InputPosition inputPosition) {
        return new IntermediateValue(inputPosition, value);
    }
}
