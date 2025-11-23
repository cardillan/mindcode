package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstOperatorBinaryVisitor;
import info.teksol.mc.generated.ast.visitors.AstOperatorShortCircuitingVisitor;
import info.teksol.mc.generated.ast.visitors.AstOperatorUnaryVisitor;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstOperatorBinary;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstOperatorShortCircuiting;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstOperatorUnary;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import org.jspecify.annotations.NullMarked;

import static info.teksol.mc.mindcode.logic.arguments.Operation.*;

@NullMarked
public class OperatorsBuilder extends AbstractCodeBuilder implements AstOperatorBinaryVisitor<ValueStore>,
        AstOperatorShortCircuitingVisitor<ValueStore>,
        AstOperatorUnaryVisitor<ValueStore> {

    public OperatorsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitOperatorBinary(AstOperatorBinary node) {
        final ValueStore left = evaluate(node.getLeft());
        final ValueStore right = evaluate(node.getRight());
        final LogicVariable tmp = assembler.nextNodeResultTemp();
        return createOperation(node, node.getOperation(), tmp, left.getValue(assembler), right.getValue(assembler));
    }

    @Override
    public ValueStore visitOperatorShortCircuiting(AstOperatorShortCircuiting node) {
        return evaluateOperatorShortCircuiting(node, false);
    }

    @Override
    public ValueStore visitOperatorUnary(AstOperatorUnary node) {
        Operation operation = node.getOperation();

        if (operation.isBooleanNegation()) {
            UnwrappedNode unwrapped = unwrapNegation(node);
            if (unwrapped.expression() instanceof AstOperatorShortCircuiting condition) {
                ValueStore result = evaluateOperatorShortCircuiting(condition, unwrapped.negated());
                assembler.exitAstNode(condition);
                return result;
            }
        }

        final ValueStore operand = evaluate(node.getOperand());

        if (operation == ADD) {
            return operand;  // Unary plus is a no-op
        }

        final LogicVariable tmp = assembler.nextNodeResultTemp();
        LogicValue operandValue = operand.getValue(assembler);
        switch (operation) {
            case SUB -> assembler.createOp(SUB, tmp, LogicNumber.ZERO, operandValue);
            case BITWISE_NOT -> assembler.createOp(BITWISE_NOT, tmp, operandValue);
            case BOOLEAN_NOT, LOGICAL_NOT ->
                    assembler.createOp(EQUAL, tmp, operandValue, LogicBoolean.FALSE);
            default -> throw new MindcodeInternalError("Unsupported unary operation " + operation);
        }
        return tmp;
    }

    private ValueStore evaluateOperatorShortCircuiting(AstOperatorShortCircuiting node, boolean negated) {
        assembler.enterAstNode(node, AstContextType.IF);
        LogicLabel falseLabel = assembler.nextLabel();
        LogicLabel endLabel = assembler.nextLabel();

        assembler.setSubcontextType(AstSubcontextType.CONDITION, 1.0);
        evaluateCondition(node, falseLabel);

        LogicVariable tmp = assembler.nextNodeResultTemp();

        assembler.setSubcontextType(AstSubcontextType.BODY, 0.5);
        assembler.createSet(tmp,negated ? LogicBoolean.FALSE : LogicBoolean.TRUE);
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
        assembler.createJumpUnconditional(endLabel);

        assembler.createLabel(falseLabel);
        assembler.setSubcontextType(AstSubcontextType.BODY, 0.5);
        assembler.createSet(tmp, negated ? LogicBoolean.TRUE : LogicBoolean.FALSE);
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 0.5);
        assembler.createLabel(endLabel);

        assembler.clearSubcontextType();
        assembler.exitAstNode(node, AstContextType.IF);
        return tmp;
    }
}
