package io.github.cardillan.mc.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralString extends AstLiteral {

    public AstLiteralString(SourcePosition sourcePosition, String literal) {
        super(sourcePosition, literal, false);
        if (!literal.isEmpty() && literal.charAt(0) == '"') {
            throw new IllegalArgumentException("String literal value must not be enclosed in quotes.");
        }
    }

    public String getValue() {
        return literal;
    }

    @Override
    public AstLiteralString withSourcePosition(SourcePosition sourcePosition) {
        return new AstLiteralString(sourcePosition, literal);
    }

    public AstLiteralString append(AstLiteralString other) {
        return new AstLiteralString(sourcePosition(), getValue() + other.getValue());
    }

    @Override
    public double getDoubleValue() {
        return 1.0;
    }

    @Override
    public long getLongValue() {
        return 1;
    }
}
