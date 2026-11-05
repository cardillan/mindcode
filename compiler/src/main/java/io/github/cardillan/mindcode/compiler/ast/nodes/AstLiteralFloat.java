package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralFloat extends AstLiteralNumeric {

    public AstLiteralFloat(SourcePosition sourcePosition, String literal, boolean suppressWarning) {
        super(sourcePosition, literal, suppressWarning);
    }

    public AstLiteralFloat(SourcePosition sourcePosition, String literal) {
        this(sourcePosition, literal, true);
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
    public AstLiteralFloat withSourcePosition(SourcePosition sourcePosition) {
        return new AstLiteralFloat(sourcePosition, literal, suppressWarning);
    }
}
