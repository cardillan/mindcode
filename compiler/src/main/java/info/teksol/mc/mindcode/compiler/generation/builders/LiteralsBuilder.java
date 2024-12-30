package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.*;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.FormattableContent;
import info.teksol.mc.mindcode.compiler.generation.variables.MissingValue;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import org.jspecify.annotations.NullMarked;

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

    public LiteralsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitFormattableLiteral(AstFormattableLiteral node) {
        return new FormattableContent(node.inputPosition(), node.getParts());
    }

    @Override
    public ValueStore visitFormattablePlaceholder(AstFormattablePlaceholder node) {
        return MissingValue.FORMATTABLE_PLACEHOLDER;
    }

    @Override
    public ValueStore visitLiteralBinary(AstLiteralBinary node) {
        String literal = node.getLiteral();
        return LogicNumber.get(literal, Long.parseLong(literal, 2, literal.length(), 2));
    }

    @Override
    public ValueStore visitLiteralBoolean(AstLiteralBoolean node) {
        return node.getValue() ? LogicBoolean.TRUE : LogicBoolean.FALSE;
    }

    @Override
    public ValueStore visitLiteralColor(AstLiteralColor node) {
        return LogicColor.create(node.getLiteral());
    }

    @Override
    public ValueStore visitLiteralDecimal(AstLiteralDecimal node) {
        return visitNumericLiteral(node);
    }

    @Override
    public ValueStore visitLiteralEscape(AstLiteralEscape node) {
        return LogicString.create(node.getValue());
    }

    @Override
    public ValueStore visitLiteralFloat(AstLiteralFloat node) {
        return visitNumericLiteral(node);
    }

    @Override
    public ValueStore visitLiteralHexadecimal(AstLiteralHexadecimal node) {
        String literal = node.getLiteral();
        return LogicNumber.get(literal, Long.decode(literal));
    }

    @Override
    public ValueStore visitLiteralNull(AstLiteralNull node) {
        return LogicNull.NULL;
    }

    @Override
    public ValueStore visitLiteralString(AstLiteralString node) {
        return LogicString.create(node.getValue());
    }

    private LogicNumber visitNumericLiteral(AstLiteral node) {
        Optional<String> literal = processor.mlogRewrite(node.getLiteral());
        if (literal.isPresent()) {
            return LogicNumber.get(literal.get(), Double.parseDouble(literal.get()));
        } else {
            error(node, "Numeric literal '%s' does not have a valid mlog representation.", node.getLiteral());
            return LogicNumber.get(node.getLiteral(), Double.parseDouble(node.getLiteral()));
        }
    }

}
