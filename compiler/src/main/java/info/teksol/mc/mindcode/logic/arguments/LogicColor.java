package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.InputPosition;
import info.teksol.mc.evaluator.Color;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLiteralColor;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
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

    public static LogicColor create(double doubleValue, String literal) {
        return new LogicColor(doubleValue, literal);
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

    @Override
    public AstMindcodeNode asAstNode(InputPosition position) {
        return new AstLiteralColor(position, literal);
    }
}
