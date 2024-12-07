package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode
public class AstOperatorBinary extends AstExpression {
    private final Operation operation;
    private final AstExpression left;
    private final AstExpression right;

    public AstOperatorBinary(SourcePosition sourcePosition, Operation operation,
            AstExpression left, AstExpression right) {
        super(sourcePosition, children(left, right));
        this.operation = Objects.requireNonNull(operation);
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
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
    public boolean equals(@Nullable Object o) {
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
    public AstContextType getContextType() {
        return AstContextType.OPERATOR;
    }
}