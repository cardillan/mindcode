package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralDecimal extends AstLiteralNumeric {

    public AstLiteralDecimal(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
    }

    @Override
    public double getDoubleValue() {
        return getLongValue();
    }

    @Override
    public long getLongValue() {
        return Long.parseLong(literal);
    }

    @Override
    public AstLiteralDecimal withInputPosition(InputPosition inputPosition) {
        return new AstLiteralDecimal(inputPosition, literal);
    }
}