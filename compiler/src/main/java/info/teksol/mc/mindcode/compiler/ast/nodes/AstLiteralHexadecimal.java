package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralHexadecimal extends AstLiteralNumeric {

    public AstLiteralHexadecimal(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
        if (!literal.startsWith("0x")) {
            throw new IllegalArgumentException("Binary literal must start with '0x'");
        }
    }

    @Override
    public double getDoubleValue() {
        return getLongValue();
    }

    @Override
    public long getLongValue() {
        return Long.decode(literal);
    }

    @Override
    public AstLiteralHexadecimal withInputPosition(InputPosition inputPosition) {
        return new AstLiteralHexadecimal(inputPosition, literal);
    }
}
