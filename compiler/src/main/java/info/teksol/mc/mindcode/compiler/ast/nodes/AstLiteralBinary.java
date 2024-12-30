package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralBinary extends AstLiteralNumeric {

    public AstLiteralBinary(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
        if (!literal.startsWith("0b")) {
            throw new IllegalArgumentException("Binary literal must start with '0b'");
        }
    }

    @Override
    public double getDoubleValue() {
        return getLongValue();
    }

    @Override
    public long getLongValue() {
        return Long.parseLong(literal, 2, literal.length(), 2);
    }

    @Override
    public AstLiteralBinary withInputPosition(InputPosition inputPosition) {
        return new AstLiteralBinary(inputPosition, literal);
    }
}
