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
public class AstAssignment extends AstExpression {
    private final @Nullable Operation operation;
    private final AstExpression target;
    private final AstExpression value;

    public AstAssignment(SourcePosition sourcePosition, @Nullable Operation operation, AstExpression target, AstExpression value) {
        super(sourcePosition, children(target, value));
        this.operation = operation;
        this.target = Objects.requireNonNull(target);
        this.value = Objects.requireNonNull(value);
    }

    public @org.jetbrains.annotations.Nullable Operation getOperation() {
        return operation;
    }

    public AstExpression getTarget() {
        return target;
    }

    public AstExpression getValue() {
        return value;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstAssignment that = (AstAssignment) o;
        return operation == that.operation && target.equals(that.target) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(operation);
        result = 31 * result + target.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.ASSIGNMENT;
    }
}