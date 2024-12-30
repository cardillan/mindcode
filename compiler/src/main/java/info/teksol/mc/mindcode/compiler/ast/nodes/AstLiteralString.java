package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralString extends AstLiteral {

    public AstLiteralString(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
    }

    public String getValue() {
        return literal;
    }

    @Override
    public AstLiteralString withInputPosition(InputPosition inputPosition) {
        return new AstLiteralString(inputPosition, literal);
    }

    @Override
    public double getDoubleValue() {
        return 1.0;
    }

    @Override
    public long getLongValue() {
        return 1;
    }
}