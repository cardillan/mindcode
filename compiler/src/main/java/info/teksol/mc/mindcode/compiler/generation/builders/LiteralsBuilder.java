package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.*;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.FormattableContent;
import info.teksol.mc.mindcode.compiler.generation.variables.MissingValue;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Optional;

@NullMarked
public class LiteralsBuilder extends AbstractBuilder implements
        AstFormattableLiteralVisitor<ValueStore>,
        AstFormattablePlaceholderVisitor<ValueStore>,
        AstLiteralBinaryVisitor<ValueStore>,
        AstLiteralBooleanVisitor<ValueStore>,
        AstLiteralColorVisitor<ValueStore>,
        AstLiteralDecimalVisitor<ValueStore>,
        AstLiteralEscapeVisitor<ValueStore>,
        AstLiteralFloatVisitor<ValueStore>,
        AstLiteralHexadecimalVisitor<ValueStore>,
        AstLiteralNullVisitor<ValueStore>,
        AstLiteralStringVisitor<ValueStore>
{
    private static final String MAX_LONG_VALUE = "9223372036854775808";

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
    public ValueStore visitLiteralColor(AstLiteralColor node) {
        if (!processor.isSupported(Opcode.PACKCOLOR, List.of())) {
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
    public ValueStore visitLiteralNull(AstLiteralNull node) {
        return LogicNull.NULL;
    }

    @Override
    public ValueStore visitLiteralString(AstLiteralString node) {
        return LogicString.create(node.sourcePosition(), node.getValue());
    }

    private LogicNumber visitIntegerLiteral(AstLiteral node, int beginIndex, int radix) {
        String literal = node.getLiteral();
        boolean negative = literal.startsWith("-");
        try {
            long value = Long.parseLong(literal, beginIndex + (negative ? 1 : 0), literal.length(), radix);

            if (!node.isSuppressWarning() && value > (1L << 52)) {
                warn(node, WARN.LITERAL_UNSAFE_DECIMAL_RANGE, literal);
            }
            return LogicNumber.create(node.sourcePosition(), literal, negative ? -value : value);
        } catch (NumberFormatException e) {
            error(node, ERR.LITERAL_INTEGER_TOO_LARGE, literal, getMaxLiteralValue(radix));
            return LogicNumber.create(node.sourcePosition(), MAX_LONG_VALUE, Long.MAX_VALUE);
        }
    }

    private String getMaxLiteralValue(int radix) {
        return switch (radix) {
            case  2 -> "0b" + Long.toString(Long.MAX_VALUE, radix);
            case 10 -> Long.toString(Long.MAX_VALUE, radix);
            case 16 -> "0x" + Long.toString(Long.MAX_VALUE, radix);
            default -> throw new MindcodeInternalError("Invalid radix: " + radix);
        };
    }

    private LogicNumber visitNumericLiteral(AstLiteral node) {
        Optional<String> literal = processor.mlogRewrite(node.sourcePosition(), node.getLiteral());
        if (literal.isPresent()) {
            return LogicNumber.create(processor, node.sourcePosition(), literal.get());
        } else {
            error(node, ERR.LITERAL_NO_VALID_REPRESENTATION, node.getLiteral());
            return LogicNumber.create(processor, node.sourcePosition(), MAX_LONG_VALUE);
        }
    }

}
