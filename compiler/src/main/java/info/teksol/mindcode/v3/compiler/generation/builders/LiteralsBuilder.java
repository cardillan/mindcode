package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.*;
import info.teksol.mindcode.logic.LogicBoolean;
import info.teksol.mindcode.logic.LogicNull;
import info.teksol.mindcode.logic.LogicNumber;
import info.teksol.mindcode.logic.LogicString;
import info.teksol.mindcode.v3.compiler.ast.nodes.*;
import info.teksol.mindcode.v3.compiler.generation.AbstractBuilder;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public class LiteralsBuilder extends AbstractBuilder implements
        AstLiteralBooleanVisitor<NodeValue>,
        AstLiteralNullVisitor<NodeValue>,
        AstLiteralBinaryVisitor<NodeValue>,
        AstLiteralDecimalVisitor<NodeValue>,
        AstLiteralFloatVisitor<NodeValue>,
        AstLiteralHexadecimalVisitor<NodeValue>,
        AstLiteralStringVisitor<NodeValue>,
        AstLiteralEscapeVisitor<NodeValue>
{

    public LiteralsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public NodeValue visitLiteralBinary(AstLiteralBinary node) {
        String literal = node.getLiteral();
        return LogicNumber.get(literal, Long.parseLong(literal, 2, literal.length(), 2));
    }

    @Override
    public NodeValue visitLiteralBoolean(AstLiteralBoolean node) {
        return node.getValue() ? LogicBoolean.TRUE : LogicBoolean.FALSE;
    }

    @Override
    public NodeValue visitLiteralDecimal(AstLiteralDecimal node) {
        return visitNumericLiteral(node);
    }

    @Override
    public NodeValue visitLiteralEscape(AstLiteralEscape node) {
        return LogicString.create(node.getValue());
    }

    @Override
    public NodeValue visitLiteralFloat(AstLiteralFloat node) {
        return visitNumericLiteral(node);
    }

    @Override
    public NodeValue visitLiteralHexadecimal(AstLiteralHexadecimal node) {
        String literal = node.getLiteral();
        return LogicNumber.get(literal, Long.decode(literal));
    }

    @Override
    public NodeValue visitLiteralNull(AstLiteralNull node) {
        return LogicNull.NULL;
    }

    @Override
    public NodeValue visitLiteralString(AstLiteralString node) {
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
