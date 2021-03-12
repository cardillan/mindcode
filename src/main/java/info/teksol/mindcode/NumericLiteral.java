package info.teksol.mindcode;

import java.util.Objects;

public class NumericLiteral implements AstNode {
    private final String literal;

    public NumericLiteral(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumericLiteral that = (NumericLiteral) o;
        return Objects.equals(literal, that.literal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literal);
    }

    @Override
    public String toString() {
        return "NumericLiteral{" +
                "literal='" + literal + '\'' +
                '}';
    }
}
