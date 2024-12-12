package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public abstract class AstLiteral extends AstExpression {
    protected final String literal;

    protected AstLiteral(InputPosition inputPosition, String literal) {
        super(inputPosition);
        this.literal = Objects.requireNonNull(literal);
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstLiteral that = (AstLiteral) o;
        return literal.equals(that.literal);
    }

    @Override
    public int hashCode() {
        return literal.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "literal='" + literal + '\'' +
                '}';
    }
}
