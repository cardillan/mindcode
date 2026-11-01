package io.github.cardillan.mc.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mc.common.SourcePosition;
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
