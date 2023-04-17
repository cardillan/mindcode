package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.generator.GenerationException;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.processor.ExpressionEvaluator;

import java.util.Objects;

/**
 * Represents a constant numeric value which wasn't converted to a literal yet.
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
     * @return numeric literal representation of the value, or null if literal representation doesn't exist
     */
    public NumericLiteral toNumericLiteral(InstructionProcessor instructionProcessor) {
        return instructionProcessor.mlogFormat(value).map(NumericLiteral::new).orElse(null);
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
        // Criterium taken from Mindustry Logic
        return Math.abs(value - (int)value) < 0.00001;
    }

    public int getAsInteger() {
        return (int) getAsDouble();
    }
}
