package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.emulator.MindustryString;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLiteralString;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class LogicString extends AbstractArgument implements LogicLiteral {
    public static final LogicString NEW_LINE = create(SourcePosition.EMPTY, "\\n");

    private final SourcePosition sourcePosition;
    private final String value;
    private final String literal;

    private LogicString(SourcePosition sourcePosition, String literal, String value) {
        super(ArgumentType.STRING_LITERAL, ValueMutability.CONSTANT);
        this.sourcePosition = sourcePosition;
        this.literal = Objects.requireNonNull(literal);
        this.value = Objects.requireNonNull(value);
    }

    private LogicString(SourcePosition sourcePosition, String value) {
        this(sourcePosition, "\"" + value + "\"", value);
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
    }

    @Override
    public String format(@Nullable InstructionProcessor instructionProcessor) {
        return value;
    }

    @Override
    public String toMlog() {
        return literal;
    }

    @Override
    public String toString() {
        return "LogicString{" +
                "stringValue='" + value + '\'' +
                '}';
    }

    public static LogicString create(String value) {
        return new LogicString(SourcePosition.EMPTY, value);
    }

    public static LogicString create(SourcePosition sourcePosition, String value) {
        return new LogicString(sourcePosition, value);
    }

    public boolean isObject() {
        return true;
    }

    public String getValue() {
        return value;
    }

    @Override
    public double getDoubleValue() {
        return 1.0;
    }

    @Override
    public long getLongValue() {
        return 1;
    }

    @Override
    public Object getObject() {
        return new MindustryString(value);
    }

    @Override
    public LogicString withSourcePosition(SourcePosition sourcePosition) {
        return new LogicString(sourcePosition, literal, value);
    }

    @Override
    public AstMindcodeNode asAstNode(SourcePosition position) {
        return new AstLiteralString(position, value);
    }
}

