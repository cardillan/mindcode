package info.teksol.mindcode.logic;

import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.v3.Color;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class LogicColor extends AbstractArgument implements LogicLiteral {
    private final double doubleValue;
    private final String literal;

    private LogicColor(double doubleValue, String literal) {
        super(ArgumentType.COLOR_LITERAL);
        this.doubleValue = doubleValue;
        this.literal = Objects.requireNonNull(literal);
    }

    @Override
    public String format(InstructionProcessor instructionProcessor) {
        return "0";
    }

    @Override
    public String toMlog() {
        return literal;
    }

    @Override
    public String toString() {
        return "LogicColor{" +
                "literal='" + literal + '\'' +
                '}';
    }

    public static LogicColor create(String literal) {
        return new LogicColor(Color.parseColor(literal), literal);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    public boolean isObject() {
        return false;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public double getDoubleValue() {
        return doubleValue;
    }

    @Override
    public long getLongValue() {
        return (long) doubleValue;
    }

    @Override
    public @Nullable Object getObject() {
        return null;
    }
}
