package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicLiteral;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

/**
 * Represents a constant numeric value (e.g. an intermediate result of constant expression evaluation)
 * which hasn't been converted to a literal yet.
 */
public class NumericValue extends ConstantAstNode {
    private final double value;

    public NumericValue(Token startToken, InputFile inputFile, double value) {
        super(startToken, inputFile);
        this.value = value;
    }

    /**
     * If possible, converts the value to numeric literal.
     *
     * @return numeric literal representation of the value, or null if literal representation doesn't exist
     */
    public NumericLiteral toNumericLiteral(InstructionProcessor instructionProcessor) {
        return instructionProcessor.mlogFormat(value).map(str -> new NumericLiteral(startToken(), sourceFile(), str)).orElse(null);
    }

    @Override
    public LogicLiteral toLogicLiteral(InstructionProcessor instructionProcessor) {
        throw new UnsupportedOperationException("Unexpected call to NumericValue.toLogicLiteral.");
    }

    @Override
    public NumericValue withToken(Token startToken) {
        return new NumericValue(startToken, sourceFile(), value);
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
}
