package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class LogicNumber extends AbstractArgument implements LogicLiteral {
    public static final LogicNumber ZERO = create(0);
    public static final LogicNumber ONE = create(1);
    public static final LogicNumber TWO = create(2);
    public static final LogicNumber THREE = create(3);

    private final SourcePosition sourcePosition;
    private final String literal;
    private final double value;

    private LogicNumber(SourcePosition sourcePosition, String literal, double value) {
        super(ArgumentType.NUMERIC_LITERAL, ValueMutability.CONSTANT);
        this.sourcePosition = sourcePosition;
        this.literal = literal;
        this.value = value;
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public boolean isNumericLiteral() {
        return true;
    }

    @Override
    public String toMlog() {
        return literal;
    }

    @Override
    public double getDoubleValue() {
        return value;
    }

    @Override
    public long getLongValue() {
        return (long) value;
    }

    public int getIntValue() {
        return (int) value;
    }

    public boolean isInteger() {
        return value == (int) value;
    }

    public boolean isLong() {
        return value == (long) value;
    }

    @Override
    public @Nullable Object getObject() {
        return null;
    }

    public LogicNumber negation(InstructionProcessor processor) {
        if (literal.equals("0")) return this;
        if (literal.startsWith("-")) {
            return LogicNumber.create(processor, sourcePosition, literal.substring(1));
        } else {
            return LogicNumber.create(processor, sourcePosition, "-" + literal);
        }
    }

    public @Nullable LogicLiteral reciprocal(InstructionProcessor processor) {
        if (literal.equals("1")) return this;
        if (value == 0.0) return null;
        double r = 1 / value;
        return processor.createLiteral(SourcePosition.EMPTY, r, false).orElse(null);
    }

    @Override
    public String format(@Nullable InstructionProcessor instructionProcessor) {
        return Objects.requireNonNull(instructionProcessor).formatNumber(value);
    }

    @Override
    public String toString() {
        return "LogicNumber{" +
                "literal='" + literal + '\'' +
                ", value=" + value +
                '}';
    }

    public static LogicNumber create(int value) {
        return new LogicNumber(SourcePosition.EMPTY, String.valueOf(value), value);
    }

    public static LogicNumber create(long value) {
        return new LogicNumber(SourcePosition.EMPTY, String.valueOf(value), value);
    }

    public static LogicNumber create(SourcePosition sourcePosition, long value) {
        return new LogicNumber(sourcePosition, String.valueOf(value), value);
    }

    public static LogicNumber create(SourcePosition sourcePosition, String literal, long value) {
        return new LogicNumber(sourcePosition, literal, value);
    }

    public static LogicNumber create(InstructionProcessor processor, String literal) {
        return new LogicNumber(SourcePosition.EMPTY, literal, processor.parseNumber(literal));
    }

    public static LogicNumber create(InstructionProcessor processor, SourcePosition sourcePosition, String literal) {
        return new LogicNumber(sourcePosition, literal, processor.parseNumber(literal));
    }

    @Override
    public LogicNumber withSourcePosition(SourcePosition sourcePosition) {
        return new LogicNumber(sourcePosition, literal, value);
    }

    @Override
    public AstMindcodeNode asAstNode(SourcePosition position) {
        String nonNegativeLiteral = literal.startsWith("-") ? literal.substring(1) : literal;
        if (nonNegativeLiteral.startsWith("0b")) {
            return new AstLiteralBinary(position, literal);
        } else if (nonNegativeLiteral.startsWith("0x")) {
            return new AstLiteralHexadecimal(position, literal);
        } else if (nonNegativeLiteral.indexOf('.') >= 0 || nonNegativeLiteral.indexOf('e') >= 0 || nonNegativeLiteral.indexOf('E') >= 0) {
            return new AstLiteralFloat(position, literal);
        } else {
            return new AstLiteralDecimal(position, literal);
        }
    }
}
