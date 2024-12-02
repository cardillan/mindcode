package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.logic.Operation;

import java.util.Objects;

public class AstOperatorUnary extends AstBaseMindcodeNode {
    private final Operation operation;
    private final AstMindcodeNode operand;

    public AstOperatorUnary(InputPosition inputPosition, Operation operation, AstMindcodeNode operand) {
        super(inputPosition);
        this.operation = Objects.requireNonNull(operation);
        this.operand = Objects.requireNonNull(operand);
    }

    public Operation getOperation() {
        return operation;
    }

    public AstMindcodeNode getOperand() {
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