package info.teksol.mc.mindcode.compiler.evaluator;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLiteral;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLiteralColor;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLiteralFloat;
import info.teksol.mc.mindcode.logic.arguments.LogicColor;
import info.teksol.mc.mindcode.logic.arguments.LogicLiteral;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

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
        Optional<LogicLiteral> literal = MindcodeCompiler.getCompileTimeEvaluatorContext().instructionProcessor()
                .createLiteral(sourcePosition(), value, false);

        return switch(literal.orElse(null)) {
            case LogicNumber number -> new AstLiteralFloat(sourcePosition(), number.toMlog());
            case LogicColor color   -> new AstLiteralColor(sourcePosition(), color.toMlog());
            case null, default -> null;
        };
    }

    @Override
    public IntermediateValue withSourcePosition(SourcePosition sourcePosition) {
        return new IntermediateValue(sourcePosition, value);
    }
}
