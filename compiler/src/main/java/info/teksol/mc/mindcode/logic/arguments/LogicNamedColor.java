package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class LogicNamedColor extends AbstractArgument implements LogicValue {
    private final SourcePosition sourcePosition;
    private final String literal;

    private LogicNamedColor(SourcePosition sourcePosition, String literal) {
        super(ArgumentType.NAMED_COLOR_LITERAL, ValueMutability.IMMUTABLE);
        this.sourcePosition = sourcePosition;
        this.literal = Objects.requireNonNull(literal);
    }

    @Override
    public boolean isLvalue() {
        return false;
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
    }

    @Override
    public String format(@Nullable InstructionProcessor instructionProcessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toMlog() {
        return literal;
    }

    @Override
    public String toString() {
        return "LogicNamedColor{" +
                "literal='" + literal + '\'' +
                '}';
    }

    public static LogicNamedColor create(SourcePosition sourcePosition, String literal) {
        return new LogicNamedColor(sourcePosition, literal);
    }

    public boolean isObject() {
        return false;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public LogicNamedColor withSourcePosition(SourcePosition sourcePosition) {
        return new LogicNamedColor(sourcePosition, literal);
    }
}
