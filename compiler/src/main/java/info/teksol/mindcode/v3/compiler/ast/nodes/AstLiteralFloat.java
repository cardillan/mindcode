package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralFloat extends AstLiteralNumeric {

    public AstLiteralFloat(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
    }

    @Override
    public double getDoubleValue() {
        return Double.parseDouble(literal);
    }

    @Override
    public long getLongValue() {
        return (long) getDoubleValue();
    }

    @Override
    public AstLiteralFloat withInputPosition(InputPosition inputPosition) {
        return new AstLiteralFloat(inputPosition, literal);
    }
}
