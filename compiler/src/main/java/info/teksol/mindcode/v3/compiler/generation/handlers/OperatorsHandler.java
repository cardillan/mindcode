package info.teksol.mindcode.v3.compiler.generation.handlers;

import info.teksol.generated.ast.visitors.AstOperatorBinaryVisitor;
import info.teksol.generated.ast.visitors.AstOperatorUnaryVisitor;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstOperatorBinary;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstOperatorUnary;
import info.teksol.mindcode.v3.compiler.generation.AstNodeHandler;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class OperatorsHandler extends BaseHandler implements AstOperatorBinaryVisitor<NodeValue>, AstOperatorUnaryVisitor<NodeValue> {

    public OperatorsHandler(CodeGeneratorContext context, AstNodeHandler mainNodeVisitor) {
        super(context, mainNodeVisitor);
    }

    @Override
    public NodeValue visitOperatorBinary(AstOperatorBinary node) {
        final NodeValue left = visit(node.getLeft());
        final NodeValue right = visit(node.getRight());
        final LogicVariable tmp = nextNodeResultTemp();

        switch (node.getOperation()) {
            case BOOLEAN_OR -> {
                final LogicVariable tmp2 = processor.nextTemp();
                codeBuilder.createOp(Operation.BOOLEAN_OR, tmp2, left.getValue(codeBuilder), right.getValue(codeBuilder));
                // Ensure the result is 0 or 1
                codeBuilder.createOp(Operation.NOT_EQUAL, tmp, tmp2, LogicBoolean.FALSE);
            }
            case STRICT_NOT_EQUAL -> {
                final LogicVariable tmp2 = processor.nextTemp();
                codeBuilder.createOp(Operation.STRICT_EQUAL, tmp2, left.getValue(codeBuilder), right.getValue(codeBuilder));
                codeBuilder.createOp(Operation.EQUAL, tmp, tmp2, LogicBoolean.FALSE);
            }
            default -> {
                codeBuilder.createOp(node.getOperation(), tmp, left.getValue(codeBuilder), right.getValue(codeBuilder));
            }
        }
        return tmp;
    }

    @Override
    public NodeValue visitOperatorUnary(AstOperatorUnary node) {
        Operation operation = node.getOperation();
        final NodeValue operand = visit(node.getOperand());

        if (operation == Operation.ADD) {
            return operand;  // Unary plus is a no-op
        }

        final LogicVariable tmp = nextNodeResultTemp();
        LogicValue operandValue = operand.getValue(codeBuilder);
        switch (operation) {
            case SUB -> codeBuilder.createOp(Operation.SUB, tmp, LogicNumber.ZERO, operandValue);
            case BITWISE_NOT -> codeBuilder.createOp(Operation.BITWISE_NOT, tmp, operandValue);
            case BOOLEAN_NOT, LOGICAL_NOT ->
                    codeBuilder.createOp(Operation.EQUAL, tmp, operandValue, LogicBoolean.FALSE);
            default -> throw new MindcodeInternalError("Unsupported unary operation " + operation);
        }
        return tmp;
    }
}
