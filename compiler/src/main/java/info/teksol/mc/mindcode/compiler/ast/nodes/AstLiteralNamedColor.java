package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralNamedColor extends AstExpression {
    protected final String literal;

    public AstLiteralNamedColor(SourcePosition sourcePosition, String literal) {
        super(sourcePosition);
        this.literal = literal;
        if (!literal.startsWith("%[") || !literal.endsWith("]")) {
            throw new IllegalArgumentException("Invalid named color literal " + literal);
        }
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstLiteralNamedColor that = (AstLiteralNamedColor) o;
        return literal.equals(that.literal);
    }

    @Override
    public int hashCode() {
        return literal.hashCode();
    }
}
