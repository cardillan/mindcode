package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.evaluator.Color;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLiteralColor;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class LogicColor extends AbstractArgument implements LogicLiteral {
    private final SourcePosition sourcePosition;
    private final double doubleValue;
    private final String literal;

    private LogicColor(SourcePosition sourcePosition, double doubleValue, String literal) {
        super(ArgumentType.COLOR_LITERAL, ValueMutability.CONSTANT);
        this.sourcePosition = sourcePosition;
        this.doubleValue = doubleValue;
        this.literal = Objects.requireNonNull(literal);
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
    }

    @Override
    public String format(@Nullable InstructionProcessor instructionProcessor) {
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

    public static LogicColor create(SourcePosition sourcePosition, String literal) {
        return new LogicColor(sourcePosition, Color.parseColor(literal), literal);
    }

    public static LogicColor create(SourcePosition sourcePosition, double doubleValue, String literal) {
        return new LogicColor(sourcePosition, doubleValue, literal);
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
    public @Nullable Object getObject() {
        return null;
    }

    @Override
    public LogicColor withSourcePosition(SourcePosition sourcePosition) {
        return new LogicColor(sourcePosition, doubleValue, literal);
    }

    @Override
    public AstMindcodeNode asAstNode(SourcePosition position) {
        return new AstLiteralColor(position, literal);
    }
}
