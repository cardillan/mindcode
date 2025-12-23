package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode
public class AstOperatorShortCircuiting extends AstExpression implements AstNegatable<AstOperatorShortCircuiting> {
    private final Operation operation;
    private final AstExpression left;
    private final AstExpression right;
    private final boolean negated;

    private AstOperatorShortCircuiting(SourcePosition sourcePosition, Operation operation,
            AstExpression left, AstExpression right, boolean negated) {
        super(sourcePosition, children(left, right));
        this.operation = Objects.requireNonNull(operation);
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
        this.negated = negated;
    }

    public AstOperatorShortCircuiting(SourcePosition sourcePosition, Operation operation,
            AstExpression left, AstExpression right) {
        this(sourcePosition, operation, left, right, false);
    }

    private AstOperatorShortCircuiting(AstOperatorShortCircuiting other, boolean negated) {
        this(other.sourcePosition(), other.operation, other.left, other.right, negated);
        setProfile(other.getProfile());
    }

    public Operation getOperation() {
        return operation;
    }

    public AstExpression getLeft() {
        return left;
    }

    public AstExpression getRight() {
        return right;
    }

    @Override
    public boolean isNegated() {
        return negated;
    }

    @Override
    public AstOperatorShortCircuiting negate() {
        return new AstOperatorShortCircuiting(this, !negated);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstOperatorShortCircuiting that = (AstOperatorShortCircuiting) o;
        return operation == that.operation && left.equals(that.left) && right.equals(that.right) && negated == that.negated;
    }

    @Override
    public int hashCode() {
        int result = operation.hashCode();
        result = 31 * result + left.hashCode();
        result = 31 * result + right.hashCode();
        result = 31 * result + Boolean.hashCode(negated);
        return result;
    }
}
