package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicLiteral;
import info.teksol.mindcode.logic.LogicNumber;

import java.util.Objects;

public class NumericLiteral extends ConstantAstNode {
    private final String literal;

    public NumericLiteral(InputPosition inputPosition, String literal) {
        super(inputPosition);
        this.literal = literal;
    }

    public NumericLiteral(InputPosition inputPosition, int value) {
        super(inputPosition);
        this.literal = String.valueOf(value);
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public LogicLiteral toLogicLiteral(InstructionProcessor instructionProcessor) throws NoValidMlogRepresentationException {
        try {
            if (literal.startsWith("0x")) {
                return LogicNumber.get(literal, Long.decode(literal));
            } else if (literal.startsWith("0b")) {
                return LogicNumber.get(literal, Long.parseLong(literal, 2, literal.length(), 2));
            } else {
                return instructionProcessor.mlogRewrite(literal)
                        .map(str -> LogicNumber.get(str, Double.parseDouble(str)))
                        .orElseThrow(() -> new NoValidMlogRepresentationException(literal));
            }
        } catch (NumberFormatException ex) {
            throw new NoValidMlogRepresentationException(literal);
        }
    }

    @Override
    public NumericLiteral withInputPosition(InputPosition inputPosition) {
        return new NumericLiteral(inputPosition, literal);
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

    public boolean notInteger() {
        double value = getAsDouble();
        // Criteria taken from Mindustry Logic
        return Math.abs(value - (int) value) >= 0.00001;
    }

    public int getAsInteger() {
        return (int)getAsDouble();
    }

    public static class NoValidMlogRepresentationException extends Exception {
        private final String literal;
        public NoValidMlogRepresentationException(String literal) {
            super();
            this.literal = literal;
        }

        public String getLiteral() {
            return literal;
        }

        @Override
        public String getMessage() {
            return "Numeric literal '" + literal  + "' does not have a valid mlog representation.";
        }
    }
}
