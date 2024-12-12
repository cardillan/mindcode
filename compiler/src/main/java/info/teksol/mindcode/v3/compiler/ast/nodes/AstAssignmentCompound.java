package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.logic.Operation;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
@AstNode
public class AstAssignmentCompound extends AstExpression {
    private final Operation operation;
    private final AstExpression target;
    private final AstExpression value;

    public AstAssignmentCompound(InputPosition inputPosition, Operation operation,
            AstExpression target, AstExpression value) {
        super(inputPosition);
        this.operation = Objects.requireNonNull(operation);
        this.target = Objects.requireNonNull(target);
        this.value = Objects.requireNonNull(value);
    }

    public Operation getOperation() {
        return operation;
    }

    public AstExpression getTarget() {
        return target;
    }

    public AstExpression getValue() {
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