package info.teksol.mindcode.ast;

import java.util.Objects;

public class NumericLiteral extends ConstantAstNode {
    private final String literal;

    public NumericLiteral(String literal) {
        this.literal = literal;
    }

    public NumericLiteral(int value) {
        this.literal = String.valueOf(value);
    }

    @Override
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

    @Override
    public double getAsDouble() {
        return literal.startsWith("0x") ? Long.decode(literal) : Double.parseDouble(literal);
    }

    public boolean isInteger() {
        double value = getAsDouble();
        // Criterium taken from Mindustry Logic
        return Math.abs(value - (int)value) < 0.00001;
    }

    public int getAsInteger() {
        return (int)getAsDouble();
    }
}
