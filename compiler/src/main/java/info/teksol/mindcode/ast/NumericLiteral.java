package info.teksol.mindcode.ast;

import java.util.Objects;
import java.util.Optional;

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
        return mlogRewrite(literal).orElse(literal);
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
        return literal.startsWith("0x") ? Long.decode(literal) :
                literal.startsWith("0b") ? Long.parseLong(literal, 2, literal.length(), 2) :
                Double.parseDouble(literal);
    }

    public boolean isInteger() {
        double value = getAsDouble();
        // Criterium taken from Mindustry Logic
        return Math.abs(value - (int)value) < 0.00001;
    }

    public int getAsInteger() {
        return (int)getAsDouble();
    }

    /**
     * Inspects the literal and, if necessary, rewrites it to conform to mlog limitations
     * (mlog recognizes decimal separator (a dot) and an exponent (e or E) in numeric literals, but not both).
     * If the literal cannot be processed (e.g. an infinity or a NaN), returns an empty optional.
     *
     * @param literal the literal to process
     * @return Optional containing mlog compatible literal, or nothing if mlog compatible equivalent doesn't exist
     */
    public static Optional<String> mlogRewrite(String literal) {
        try {
            if (literal.contains("NaN") || literal.contains("Infinity")) {
                return null;
            } else if (literal.endsWith(".0") && literal.length() > 2) {
                return Optional.of(literal.substring(0, literal.length() - 2));
            }

            int dot = literal.indexOf('.');
            int exp = Math.max(literal.indexOf('e'), literal.indexOf('E'));
            if (dot >= 0 && exp >= 0) {
                if (dot >= exp) {
                    return null;
                }

                int exponent = Integer.parseInt(literal.substring(exp + 1)) - (exp - dot - 1);
                String mantisa =  literal.substring(0, dot) + literal.substring(dot + 1, exp);

                int lastValidDigit = mantisa.length() - 1;
                while (mantisa.charAt(lastValidDigit) == '0') {
                    if (--lastValidDigit < 0) {
                        return Optional.of("0");
                    }
                    exponent++;
                }

                String mlog = exponent == 0
                        ? mantisa.substring(0, lastValidDigit + 1)
                        : mantisa.substring(0, lastValidDigit + 1) + literal.charAt(exp) + exponent;
                return Optional.of(mlog);
            } else {
                return Optional.of(literal);
            }
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
