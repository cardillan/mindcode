package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.generator.GenerationException;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicLiteral;

import java.util.Objects;

/**
 * Represents a constant numeric value (e.g. an intermediate result of constant expression evaluation)
 * which hasn't been converted to a literal yet.
 */
public class NumericValue extends ConstantAstNode {
    private final double value;

    public NumericValue(double value) {
        this.value = value;
    }

    public String getLiteral(InstructionProcessor instructionProcessor) {
        return instructionProcessor.mlogFormat(value).orElseThrow(
                () -> new GenerationException("Numeric literal " + value + " cannot be represented in Mindustry Logic."));
    }

    /**
     * If possible, converts the value to numeric literal.
     *
     * @return numeric literal representation of the value, or null if literal representation doesn't exist
     */
    public NumericLiteral toNumericLiteral(InstructionProcessor instructionProcessor) {
        return instructionProcessor.mlogFormat(value).map(NumericLiteral::new).orElse(null);
    }

    @Override
    public LogicLiteral toLogicLiteral(InstructionProcessor instructionProcessor) {
        throw new UnsupportedOperationException("Unexpected call to NumericValue.toLogicLiteral.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumericValue that = (NumericValue) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "NumericValue{value=" + value + '}';
    }

    @Override
    public double getAsDouble() {
        return value;
    }

    public boolean isInteger() {
        double value = getAsDouble();
        // Criteria taken from Mindustry Logic
        return Math.abs(value - (int)value) < 0.00001;
    }

    public int getAsInteger() {
        return (int) getAsDouble();
    }
}
