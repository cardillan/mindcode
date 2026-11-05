package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mindcode.common.SourcePosition;
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
