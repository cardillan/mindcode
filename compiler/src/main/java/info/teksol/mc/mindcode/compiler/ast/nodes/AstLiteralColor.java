package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.evaluator.Color;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralColor extends AstLiteralNumeric {

    public AstLiteralColor(SourcePosition sourcePosition, String literal) {
        super(sourcePosition, literal, false);
    }

    @Override
    public double getDoubleValue() {
        return Color.parseColor(literal);
    }

    @Override
    public long getLongValue() {
        return (long) getDoubleValue();
    }

    @Override
    public AstLiteralColor withSourcePosition(SourcePosition sourcePosition) {
        return new AstLiteralColor(sourcePosition, literal);
    }
}
