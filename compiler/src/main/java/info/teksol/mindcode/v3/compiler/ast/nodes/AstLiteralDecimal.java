package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
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
