package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.logic.Operation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AstOperatorBinary extends AstBaseMindcodeNode {
    private final @NotNull Operation operation;
    private final @NotNull AstMindcodeNode left;
    private final @NotNull AstMindcodeNode right;

    public AstOperatorBinary(@NotNull InputPosition inputPosition, @NotNull Operation operation,
            @NotNull AstMindcodeNode left, @NotNull AstMindcodeNode right) {
        super(inputPosition);
        this.operation = Objects.requireNonNull(operation);
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }

    public @NotNull Operation getOperation() {
        return operation;
    }

    public @NotNull AstMindcodeNode getLeft() {
        return left;
    }

    public @NotNull AstMindcodeNode getRight() {
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