package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralBinary extends AstLiteralNumeric {

    public AstLiteralBinary(SourcePosition sourcePosition, String literal, boolean suppressWarning) {
        super(sourcePosition, literal, suppressWarning);
        if (!literal.startsWith("0b") && !literal.startsWith("-0b") && !literal.startsWith("+0b")) {
            throw new IllegalArgumentException("Binary literal must start with '0b'");
        }
    }

    public AstLiteralBinary(SourcePosition sourcePosition, String literal) {
        this(sourcePosition, literal, true);
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
    public AstLiteralBinary withSourcePosition(SourcePosition sourcePosition) {
        return new AstLiteralBinary(sourcePosition, literal, suppressWarning);
    }
}
