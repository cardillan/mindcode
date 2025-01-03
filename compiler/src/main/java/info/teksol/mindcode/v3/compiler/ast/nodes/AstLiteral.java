package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public abstract class AstLiteral extends AstBaseMindcodeNode {
    protected final String literal;

    public AstLiteral(InputPosition inputPosition, String literal) {
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
