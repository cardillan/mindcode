package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AstAssignmentSimple extends AstExpression {
    private final @NotNull AstExpression target;
    private final @NotNull AstExpression value;

    public AstAssignmentSimple(@NotNull InputPosition inputPosition, @NotNull AstExpression target,
            @NotNull AstExpression value) {
        super(inputPosition);
        this.target = Objects.requireNonNull(target);
        this.value = Objects.requireNonNull(value);
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

        AstAssignmentSimple that = (AstAssignmentSimple) o;
        return target.equals(that.target) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = target.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AstAssignmentSimple{" +
               "target=" + target +
               ", value=" + value +
               '}';
    }
}