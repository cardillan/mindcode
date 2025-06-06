package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstOperatorBinaryVisitor;
import info.teksol.mc.generated.ast.visitors.AstOperatorUnaryVisitor;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstOperatorBinary;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstOperatorUnary;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import org.jspecify.annotations.NullMarked;

import static info.teksol.mc.mindcode.logic.arguments.Operation.*;

@NullMarked
public class OperatorsBuilder extends AbstractBuilder implements AstOperatorBinaryVisitor<ValueStore>, AstOperatorUnaryVisitor<ValueStore> {

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
    public ValueStore visitOperatorUnary(AstOperatorUnary node) {
        Operation operation = node.getOperation();
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
}
