package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.AstOperatorBinaryVisitor;
import info.teksol.generated.ast.visitors.AstOperatorUnaryVisitor;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstOperatorBinary;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstOperatorUnary;
import info.teksol.mindcode.v3.compiler.generation.AbstractBuilder;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class OperatorsBuilder extends AbstractBuilder implements AstOperatorBinaryVisitor<NodeValue>, AstOperatorUnaryVisitor<NodeValue> {

    public OperatorsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public NodeValue visitOperatorBinary(AstOperatorBinary node) {
        final NodeValue left = evaluate(node.getLeft());
        final NodeValue right = evaluate(node.getRight());
        final LogicVariable tmp = nextNodeResultTemp();

        switch (node.getOperation()) {
            case BOOLEAN_OR -> {
                final LogicVariable tmp2 = unprotectedTemp();
                assembler.createOp(Operation.BOOLEAN_OR, tmp2, left.getValue(assembler), right.getValue(assembler));
                // Ensure the result is 0 or 1
                assembler.createOp(Operation.NOT_EQUAL, tmp, tmp2, LogicBoolean.FALSE);
            }
            case STRICT_NOT_EQUAL -> {
                final LogicVariable tmp2 = unprotectedTemp();
                assembler.createOp(Operation.STRICT_EQUAL, tmp2, left.getValue(assembler), right.getValue(assembler));
                assembler.createOp(Operation.EQUAL, tmp, tmp2, LogicBoolean.FALSE);
            }
            default -> {
                assembler.createOp(node.getOperation(), tmp, left.getValue(assembler), right.getValue(assembler));
            }
        }
        return tmp;
    }

    @Override
    public NodeValue visitOperatorUnary(AstOperatorUnary node) {
        Operation operation = node.getOperation();
        final NodeValue operand = evaluate(node.getOperand());

        if (operation == Operation.ADD) {
            return operand;  // Unary plus is a no-op
        }

        final LogicVariable tmp = nextNodeResultTemp();
        LogicValue operandValue = operand.getValue(assembler);
        switch (operation) {
            case SUB -> assembler.createOp(Operation.SUB, tmp, LogicNumber.ZERO, operandValue);
            case BITWISE_NOT -> assembler.createOp(Operation.BITWISE_NOT, tmp, operandValue);
            case BOOLEAN_NOT, LOGICAL_NOT ->
                    assembler.createOp(Operation.EQUAL, tmp, operandValue, LogicBoolean.FALSE);
            default -> throw new MindcodeInternalError("Unsupported unary operation " + operation);
        }
        return tmp;
    }
}
