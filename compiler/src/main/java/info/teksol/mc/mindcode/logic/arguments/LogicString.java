package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.ContextFactory;
import info.teksol.mc.mindcode.compiler.PositionalMessage;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLiteralString;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.util.EscapeClass;
import info.teksol.mc.util.UtfUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class LogicString extends AbstractArgument implements LogicLiteral {
    public static final int MAX_STRING_SIZE = 65535;
    public static final LogicString NEW_LINE = new LogicString(SourcePosition.EMPTY, "\\n", "\n");

    private final SourcePosition sourcePosition;
    private final String quotedLiteral;
    private final String nakedLiteral;
    private @Nullable String value;

    private LogicString(SourcePosition sourcePosition, String quotedLiteral, String nakedLiteral, @Nullable String value) {
        super(ArgumentType.STRING_LITERAL, ValueMutability.CONSTANT);
        this.sourcePosition = sourcePosition;
        this.quotedLiteral = Objects.requireNonNull(quotedLiteral);
        this.nakedLiteral = Objects.requireNonNull(nakedLiteral);
        this.value = value;

        int size = UtfUtils.utf8EncodedLength(nakedLiteral);
        if (size > MAX_STRING_SIZE) {
            ContextFactory.getMessageContext().addMessage(PositionalMessage.error(sourcePosition,
                    ERR.STRING_SIZE_LIMIT_EXCEEDED, MAX_STRING_SIZE, size - MAX_STRING_SIZE));
        }
    }

    private LogicString(SourcePosition sourcePosition, String nakedLiteral, @Nullable String value) {
        this(sourcePosition, '"' + nakedLiteral + '"', nakedLiteral, value);
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
    }

    public String getNakedLiteral() {
        return nakedLiteral;
    }

    public String getStringValue() {
        return value == null ? (value = UtfUtils.unescape(nakedLiteral)) : value;
    }

    @Override
    public String format(@Nullable InstructionProcessor instructionProcessor) {
        return getStringValue();
    }

    @Override
    public String toMlog() {
        return quotedLiteral;
    }

    @Override
    public String toString() {
        return "LogicString{" +
                "stringValue='" + value + '\'' +
                '}';
    }

    /// Used to create strings that do not need escapes
    /// No argument validation is performed!!!
    public static LogicString createSimple(String string) {
        return new LogicString(SourcePosition.EMPTY, string, string);
    }

    /// Used to create strings that might need escapes
    public static LogicString createRaw(String string) {
        return new LogicString(SourcePosition.EMPTY, UtfUtils.escape(EscapeClass.MINIMAL, string), string);
    }

    /// Used to create strings using already escaped naked literal
    public static LogicString createEscaped(String string) {
        return new LogicString(SourcePosition.EMPTY, string, null);
    }

    public static LogicString create(SourcePosition sourcePosition, String literal) {
        return new LogicString(sourcePosition, literal, null);
    }

    public static LogicString createRaw(SourcePosition sourcePosition, String string) {
        return new LogicString(sourcePosition, UtfUtils.escape(EscapeClass.MINIMAL, string), string);
    }

    public boolean isObject() {
        return true;
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
        return getStringValue();
    }

    @Override
    public LogicString withSourcePosition(SourcePosition sourcePosition) {
        return new LogicString(sourcePosition, quotedLiteral, nakedLiteral, value);
    }

    @Override
    public AstMindcodeNode asAstNode(SourcePosition position) {
        return new AstLiteralString(position, nakedLiteral);
    }
}
