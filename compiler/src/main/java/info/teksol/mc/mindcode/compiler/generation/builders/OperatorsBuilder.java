package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstOperatorBinaryVisitor;
import info.teksol.mc.generated.ast.visitors.AstOperatorUnaryVisitor;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstOperatorBinary;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstOperatorUnary;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;

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

        if (!processor.getProcessorVersion().atLeast(node.getOperation().getProcessorVersion())) {
            error(node, ERR.OPERATOR_REQUIRES_SPECIFIC_TARGET, node.getOperation().getMindcode(), node.getOperation().getProcessorVersion().versionName());
            return LogicVariable.INVALID;
        }

        // Handle special cases
        switch (node.getOperation()) {
            case BOOLEAN_OR -> {
                final LogicVariable tmp2 = assembler.unprotectedTemp();
                assembler.createOp(Operation.BOOLEAN_OR, tmp2, left.getValue(assembler), right.getValue(assembler));
                // Ensure the result is 0 or 1
                assembler.createOp(Operation.NOT_EQUAL, tmp, tmp2, LogicBoolean.FALSE);
                return tmp;
            }
            case STRICT_NOT_EQUAL -> {
                final LogicVariable tmp2 = assembler.unprotectedTemp();
                assembler.createOp(Operation.STRICT_EQUAL, tmp2, left.getValue(assembler), right.getValue(assembler));
                assembler.createOp(Operation.EQUAL, tmp, tmp2, LogicBoolean.FALSE);
                return tmp;
            }
            case EMOD -> {
                if (!processor.getProcessorVersion().atLeast(ProcessorVersion.V8A)) {
                    assembler.createOp(Operation.MOD, tmp, left.getValue(assembler), right.getValue(assembler));
                    assembler.createOp(Operation.ADD, tmp, tmp, right.getValue(assembler));
                    assembler.createOp(Operation.MOD, tmp, tmp, right.getValue(assembler));
                    return tmp;
                }
            }
        }

        assembler.createOp(node.getOperation(), tmp, left.getValue(assembler), right.getValue(assembler));
        return tmp;
    }

    @Override
    public ValueStore visitOperatorUnary(AstOperatorUnary node) {
        Operation operation = node.getOperation();
        final ValueStore operand = evaluate(node.getOperand());

        if (operation == Operation.ADD) {
            return operand;  // Unary plus is a no-op
        }

        final LogicVariable tmp = assembler.nextNodeResultTemp();
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
