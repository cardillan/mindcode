package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstAssignmentSimple extends AstExpression {
    private final AstExpression target;
    private final AstExpression value;

    public AstAssignmentSimple(InputPosition inputPosition, AstExpression target,
            AstExpression value) {
        super(inputPosition, children(target, value));
        this.target = Objects.requireNonNull(target);
        this.value = Objects.requireNonNull(value);
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

        AstAssignmentSimple that = (AstAssignmentSimple) o;
        return target.equals(that.target) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = target.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

}