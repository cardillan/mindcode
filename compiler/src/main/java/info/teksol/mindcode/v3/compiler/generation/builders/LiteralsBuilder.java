package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.*;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.v3.compiler.ast.nodes.*;
import info.teksol.mindcode.v3.compiler.generation.AbstractBuilder;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.FormattableContent;
import info.teksol.mindcode.v3.compiler.generation.variables.MissingValue;
import info.teksol.mindcode.v3.compiler.generation.variables.ValueStore;
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

    public LiteralsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitFormattableLiteral(AstFormattableLiteral node) {
        List<ValueStore> parts = evaluateExpressions(node.getParts());
        return new FormattableContent(node.inputPosition(), parts);
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
