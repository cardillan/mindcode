package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AstLiteral extends AstExpression {
    protected final @NotNull String literal;

    protected AstLiteral(@NotNull InputPosition inputPosition, @NotNull String literal) {
        super(inputPosition);
        this.literal = Objects.requireNonNull(literal);
    }

    public @NotNull String getLiteral() {
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
