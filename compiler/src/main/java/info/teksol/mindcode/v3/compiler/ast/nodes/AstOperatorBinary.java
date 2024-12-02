package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.logic.Operation;

import java.util.Objects;

public class AstOperatorBinary extends AstBaseMindcodeNode {
    private final Operation operation;
    private final AstMindcodeNode left;
    private final AstMindcodeNode right;

    public AstOperatorBinary(InputPosition inputPosition, Operation operation, AstMindcodeNode left, AstMindcodeNode right) {
        super(inputPosition);
        this.operation = Objects.requireNonNull(operation);
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }

    public Operation getOperation() {
        return operation;
    }

    public AstMindcodeNode getLeft() {
        return left;
    }

    public AstMindcodeNode getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstOperatorBinary that = (AstOperatorBinary) o;
        return operation == that.operation && left.equals(that.left) && right.equals(that.right);
    }

    @Override
    public int hashCode() {
        int result = operation.hashCode();
        result = 31 * result + left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AstOperatorBinary{" +
               "operation=" + operation +
               ", left=" + left +
               ", right=" + right +
               '}';
    }
}