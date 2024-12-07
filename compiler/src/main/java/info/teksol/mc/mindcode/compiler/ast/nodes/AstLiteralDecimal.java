package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralDecimal extends AstLiteralNumeric {

    public AstLiteralDecimal(SourcePosition sourcePosition, String literal, boolean suppressWarning) {
        super(sourcePosition, literal, suppressWarning);
    }

    public AstLiteralDecimal(SourcePosition sourcePosition, String literal) {
        this(sourcePosition, literal, true);
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
    public AstLiteralDecimal withSourcePosition(SourcePosition sourcePosition) {
        return new AstLiteralDecimal(sourcePosition, literal, suppressWarning);
    }
}
