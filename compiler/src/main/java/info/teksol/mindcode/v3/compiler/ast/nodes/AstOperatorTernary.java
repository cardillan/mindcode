package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode
public class AstOperatorTernary extends AstExpression {
    private final AstExpression condition;
    private final AstExpression trueBranch;
    private final AstExpression falseBranch;

    public AstOperatorTernary(InputPosition inputPosition, AstExpression condition,
            AstExpression trueBranch, AstExpression falseBranch) {
        super(inputPosition, children(condition, trueBranch, falseBranch));
        this.condition = Objects.requireNonNull(condition);
        this.trueBranch = Objects.requireNonNull(trueBranch);
        this.falseBranch = Objects.requireNonNull(falseBranch);
    }

    public AstExpression getCondition() {
        return condition;
    }

    public AstExpression getTrueBranch() {
        return trueBranch;
    }

    public AstExpression getFalseBranch() {
        return falseBranch;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstOperatorTernary that = (AstOperatorTernary) o;
        return condition.equals(that.condition) && trueBranch.equals(that.trueBranch) && falseBranch.equals(that.falseBranch);
    }

    @Override
    public int hashCode() {
        int result = condition.hashCode();
        result = 31 * result + trueBranch.hashCode();
        result = 31 * result + falseBranch.hashCode();
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.IF;
    }
}