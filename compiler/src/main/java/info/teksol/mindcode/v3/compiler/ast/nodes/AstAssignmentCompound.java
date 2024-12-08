package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.logic.Operation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AstAssignmentCompound extends AstExpression {
    private final @NotNull Operation operation;
    private final @NotNull AstExpression target;
    private final @NotNull AstExpression value;

    public AstAssignmentCompound(@NotNull InputPosition inputPosition, @NotNull Operation operation,
            @NotNull AstExpression target, @NotNull AstExpression value) {
        super(inputPosition);
        this.operation = Objects.requireNonNull(operation);
        this.target = Objects.requireNonNull(target);
        this.value = Objects.requireNonNull(value);
    }

    public @NotNull Operation getOperation() {
        return operation;
    }

    public @NotNull AstExpression getTarget() {
        return target;
    }

    public @NotNull AstExpression getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstAssignmentCompound that = (AstAssignmentCompound) o;
        return operation.equals(that.operation) && target.equals(that.target) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = operation.hashCode();
        result = 31 * result + target.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AstAssignmentCompound{" +
               "operation=" + operation +
               ", target=" + target +
               ", value=" + value +
               '}';
    }
}