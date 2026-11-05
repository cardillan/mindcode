package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralBoolean extends AstLiteral {

    public AstLiteralBoolean(SourcePosition sourcePosition, String literal) {
        super(sourcePosition, literal, false);
        if (!"true".equals(literal) && !"false".equals(literal)) {
            throw new IllegalArgumentException("Boolean literal is not 'true' or 'false'");
        }
    }

    public AstLiteralBoolean(SourcePosition sourcePosition, boolean value) {
        this(sourcePosition, value ? "true" : "false");
    }

    public boolean getValue() {
        return "true".equals(getLiteral());
    }

    @Override
    public AstLiteralBoolean withSourcePosition(SourcePosition sourcePosition) {
        return new AstLiteralBoolean(sourcePosition, getValue());
    }

    @Override
    public double getDoubleValue() {
        return getLongValue();
    }

    @Override
    public long getLongValue() {
        return "true".equals(literal) ? 1 : 0;
    }
}
