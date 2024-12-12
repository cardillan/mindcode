package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.logic.Operation;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
@AstNode
public class AstOperatorUnary extends AstExpression {
    private final Operation operation;
    private final AstExpression operand;

    public AstOperatorUnary(InputPosition inputPosition, Operation operation,
            AstExpression operand) {
        super(inputPosition);
        this.operation = Objects.requireNonNull(operation);
        this.operand = Objects.requireNonNull(operand);
    }

    public Operation getOperation() {
        return operation;
    }

    public AstExpression getOperand() {
        return operand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstOperatorUnary that = (AstOperatorUnary) o;
        return operation == that.operation && operand.equals(that.operand);
    }

    @Override
    public int hashCode() {
        int result = operation.hashCode();
        result = 31 * result + operand.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AstOperatorUnary{" +
               "operation=" + operation +
               ", operand=" + operand +
               '}';
    }
}