package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.generator.GenerationException;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.processor.DoubleVariable;
import info.teksol.mindcode.processor.ExpressionEvaluator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;
import java.util.Optional;

public class NumericLiteral extends ConstantAstNode {
    private final String literal;

    public NumericLiteral(String literal) {
        this.literal = literal;
    }

    public NumericLiteral(int value) {
        this.literal = String.valueOf(value);       // int is safe, no need to rewrite
    }

    @Override
    public String getLiteral(InstructionProcessor instructionProcessor) {
        return literal.startsWith("0x") || literal.startsWith("0b")
                ? literal
                : instructionProcessor.mlogRewrite(literal).orElseThrow(
                        () -> new GenerationException("Numeric literal " + literal + " cannot be represented in Mindustry Logic."));
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
}
