package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.*;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.FormattableContent;
import info.teksol.mc.mindcode.compiler.generation.variables.MissingValue;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public class LiteralsBuilder extends AbstractCodeBuilder implements
        AstFormattableLiteralVisitor<ValueStore>,
        AstFormattablePlaceholderVisitor<ValueStore>,
        AstLiteralBinaryVisitor<ValueStore>,
        AstLiteralBooleanVisitor<ValueStore>,
        AstLiteralCharVisitor<ValueStore>,
        AstLiteralColorVisitor<ValueStore>,
        AstLiteralDecimalVisitor<ValueStore>,
        AstLiteralEscapeVisitor<ValueStore>,
        AstLiteralFloatVisitor<ValueStore>,
        AstLiteralHexadecimalVisitor<ValueStore>,
        AstLiteralNamedColorVisitor<ValueStore>,
        AstLiteralNullVisitor<ValueStore>,
        AstLiteralStringVisitor<ValueStore> {
    private static final String INVALID_LONG_VALUE = "9223372036854775807";

    public LiteralsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitFormattableLiteral(AstFormattableLiteral node) {
        return new FormattableContent(node.sourcePosition(), node.getParts());
    }

    @Override
    public ValueStore visitFormattablePlaceholder(AstFormattablePlaceholder node) {
        return new MissingValue(node.sourcePosition());
    }

    @Override
    public ValueStore visitLiteralBinary(AstLiteralBinary node) {
        String literal = node.getLiteral();
        return visitIntegerLiteral(node, 2, 2);
    }

    @Override
    public ValueStore visitLiteralBoolean(AstLiteralBoolean node) {
        return node.getValue() ? LogicBoolean.TRUE : LogicBoolean.FALSE;
    }

    @Override
    public ValueStore visitLiteralChar(AstLiteralChar node) {
        return LogicNumber.create(node.sourcePosition(), node.getLongValue());
    }

    @Override
    public ValueStore visitLiteralColor(AstLiteralColor node) {
        if (!processor.getProcessorVersion().atLeast(ProcessorVersion.V7)) {
            error(node, ERR.LITERAL_COLOR_REQUIRES_TARGET_7);
        }

        return LogicColor.create(node.sourcePosition(), node.getLiteral());
    }

    @Override
    public ValueStore visitLiteralDecimal(AstLiteralDecimal node) {
        return visitIntegerLiteral(node, 0, 10);
    }

    @Override
    public ValueStore visitLiteralEscape(AstLiteralEscape node) {
        return LogicString.create(node.sourcePosition(), node.getValue());
    }

    @Override
    public ValueStore visitLiteralFloat(AstLiteralFloat node) {
        return visitNumericLiteral(node);
    }

    @Override
    public ValueStore visitLiteralHexadecimal(AstLiteralHexadecimal node) {
        return visitIntegerLiteral(node, 2, 16);
    }

    @Override
    public ValueStore visitLiteralNamedColor(AstLiteralNamedColor node) {
        String color = node.getLiteral().substring(2, node.getLiteral().length() - 1);

        if (!processor.getProcessorVersion().atLeast(ProcessorVersion.V8A)) {
            error(node, ERR.LITERAL_NAMED_COLOR_REQUIRES_TARGET_8);
        } else if (!processor.isValidColorName(color)) {
            strictError(node, ERR.NAMED_COLOR_NOT_RECOGNIZED, color);
        }

        return LogicNamedColor.create(node.sourcePosition(), node.getLiteral());
    }

    @Override
    public ValueStore visitLiteralNull(AstLiteralNull node) {
        return LogicNull.NULL;
    }

    @Override
    public ValueStore visitLiteralString(AstLiteralString node) {
        boolean fullEscapes = processorVersion.supportsUnicodeEscapes();
        StringBuilder sbr = new StringBuilder();
        String chars = node.getValue();
        boolean backslashEscape = false;

        for (int pos = 0; pos < chars.length(); pos++) {
            int start = pos;
            if (chars.charAt(pos) == '\\') {
                if (++pos >= chars.length()) {
                    error(node.sourcePosition().columnOffset(start + 1), ERR.LITERAL_INVALID_ESCAPE);
                    sbr.append("\\\\");
                    break;
                }

                char c = chars.charAt(pos);
                backslashEscape = false;
                switch (c) {
                    case 'u' -> {
                        if (!fullEscapes) {
                            error(node.sourcePosition().columnOffset(start + 1), ERR.LITERAL_UNSUPPORTED_ESCAPE, c);
                            break;
                        }
                        if (pos + 4 >= chars.length()) {
                            error(node.sourcePosition().columnOffset(start + 1), ERR.LITERAL_INVALID_UNICODE_ESCAPE);
                            sbr.append("\\\\u");
                            break;
                        }
                        for (int j = 0; j < 4; j++) {
                            if (Character.digit(chars.charAt(pos + 1), 16) == -1) {
                                error(node.sourcePosition().columnOffset(start + 1), ERR.LITERAL_INVALID_UNICODE_ESCAPE);
                                sbr.append('\\');
                                break;
                            }
                            pos++;
                        }
                        sbr.append(chars, start, pos + 1);
                    }
                    case '$' -> sbr.append('$');
                    case 'n' -> sbr.append("\\n");
                    case '\\' -> {
                        backslashEscape = true;
                        sbr.append("\\\\");
                    }
                    case '"' -> {
                        if (!fullEscapes) {
                            error(node.sourcePosition().columnOffset(start + 1), ERR.LITERAL_UNSUPPORTED_ESCAPE, c);
                            sbr.append("''");
                        } else {
                            sbr.append('\\').append(c);
                        }
                    }
                    default -> {
                        error(node.sourcePosition().columnOffset(start + 1), ERR.LITERAL_INVALID_ESCAPE);
                        sbr.append("\\\\");
                        pos--;
                    }
                }
            } else {
                if (backslashEscape && !fullEscapes && chars.charAt(pos) == 'n') {
                    error(node.sourcePosition().columnOffset(start), ERR.LITERAL_UNSUPPORTED_NEWLINE_ESCAPE);
                }
                backslashEscape = false;
                sbr.append(chars.charAt(pos));
            }
        }

        return LogicString.create(node.sourcePosition(), sbr.toString());
    }

    private LogicNumber visitIntegerLiteral(AstLiteral node, int start, int radix) {
        String literal = node.getLiteral();
        if (literal.isEmpty()) throw new MindcodeInternalError("Empty literal.");

        char ch = literal.charAt(0);
        boolean negative = ch == '-';
        int beginIndex = start + (ch == '+' || ch == '-' ? 1 : 0);
        try {
            long absValue = radix == 10
                    ? Long.parseLong(literal, beginIndex, literal.length(), radix)
                    : Long.parseUnsignedLong(literal, beginIndex, literal.length(), radix);

            if (!processor.isValidIntegerLiteral(absValue) && !processor.isValidHexLiteral(absValue)) {
                error(node, ERR.LITERAL_NO_VALID_REPRESENTATION, absValue);
            } else if (!node.isSuppressWarning() && (absValue == Long.MIN_VALUE || Math.abs(absValue) >= (1L << 53))) {
                warn(node, WARN.LITERAL_UNSAFE_DECIMAL_RANGE, literal);
            }

            long value = negative ? -absValue : absValue;
            return processor.isValidHexLiteral(value)
                    ? LogicNumber.create(node.sourcePosition(), literal, value)
                    : LogicNumber.create(node.sourcePosition(), value);
        } catch (NumberFormatException e) {
            error(node, ERR.LITERAL_INTEGER_TOO_LARGE, literal, getMaxLiteralValue(radix));
            return LogicNumber.create(node.sourcePosition(), INVALID_LONG_VALUE, Long.MAX_VALUE);
        }
    }

    private String getMaxLiteralValue(int radix) {
        return switch (radix) {
            case 2 -> "0b" + Long.toString(Long.MAX_VALUE, radix);
            case 10 -> Long.toString(Long.MAX_VALUE, radix);
            case 16 -> "0x" + Long.toString(Long.MAX_VALUE, radix);
            default -> throw new MindcodeInternalError("Invalid radix: " + radix);
        };
    }

    private LogicNumber visitNumericLiteral(AstLiteral node) {
        Optional<String> literal = processor.mlogRewrite(node.sourcePosition(), node.getLiteral(), true);
        if (literal.isPresent()) {
            return LogicNumber.create(processor, node.sourcePosition(), literal.get());
        } else {
            error(node, ERR.LITERAL_NO_VALID_REPRESENTATION, node.getLiteral());
            return LogicNumber.create(processor, node.sourcePosition(), INVALID_LONG_VALUE);
        }
    }

}
