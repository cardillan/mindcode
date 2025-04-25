package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralHexadecimal extends AstLiteralNumeric {

    public AstLiteralHexadecimal(SourcePosition sourcePosition, String literal, boolean suppressWarning) {
        super(sourcePosition, literal, suppressWarning);
        if (!literal.startsWith("0x") && !literal.startsWith("-0x")) {
            throw new IllegalArgumentException("Hexadecimal literal must start with '0x'");
        }
    }

    public AstLiteralHexadecimal(SourcePosition sourcePosition, String literal) {
        this(sourcePosition, literal, true);
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
    public AstLiteralHexadecimal withSourcePosition(SourcePosition sourcePosition) {
        return new AstLiteralHexadecimal(sourcePosition, literal, suppressWarning);
    }
}
