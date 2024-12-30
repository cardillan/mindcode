package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
import info.teksol.mc.evaluator.Color;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralColor extends AstLiteralNumeric {

    public AstLiteralColor(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
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
    public AstLiteralColor withInputPosition(InputPosition inputPosition) {
        return new AstLiteralColor(inputPosition, literal);
    }
}
