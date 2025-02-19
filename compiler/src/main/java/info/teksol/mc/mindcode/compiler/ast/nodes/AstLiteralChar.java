package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralChar extends AstLiteralNumeric {

    public AstLiteralChar(SourcePosition sourcePosition, char literal, boolean suppressWarning) {
        super(sourcePosition, String.valueOf(literal), suppressWarning);
    }

    public AstLiteralChar(SourcePosition sourcePosition, char literal) {
        this(sourcePosition, literal, true);
    }

    @Override
    public double getDoubleValue() {
        return getLongValue();
    }

    @Override
    public long getLongValue() {
        return literal.charAt(0);
    }

    @Override
    public AstLiteralChar withSourcePosition(SourcePosition sourcePosition) {
        return new AstLiteralChar(sourcePosition, literal.charAt(0), suppressWarning);
    }
}
