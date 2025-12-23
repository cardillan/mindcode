package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode
public class AstOperatorTernary extends AstExpression implements AstNegatable<AstOperatorTernary> {
    private final AstExpression condition;
    private final AstMindcodeNode trueBranch;
    private final AstMindcodeNode falseBranch;
    private final boolean negated;

    private AstOperatorTernary(SourcePosition sourcePosition, AstExpression condition,
            AstMindcodeNode trueBranch, AstMindcodeNode falseBranch, boolean negated) {
        super(sourcePosition, children(condition, trueBranch, falseBranch));
        this.condition = Objects.requireNonNull(condition);
        this.trueBranch = Objects.requireNonNull(trueBranch);
        this.falseBranch = Objects.requireNonNull(falseBranch);
        this.negated = negated;
    }

    public AstOperatorTernary(SourcePosition sourcePosition, AstExpression condition,
            AstMindcodeNode trueBranch, AstMindcodeNode falseBranch) {
        this(sourcePosition, condition, trueBranch, falseBranch, false);
    }

    private AstOperatorTernary(AstOperatorTernary other, boolean negated) {
        this(other.sourcePosition(), other.condition, other.trueBranch, other.falseBranch, negated);
        setProfile(other.getProfile());
    }

    public AstExpression getCondition() {
        return condition;
    }

    public AstMindcodeNode getTrueBranch() {
        return trueBranch;
    }

    public AstMindcodeNode getFalseBranch() {
        return falseBranch;
    }

    @Override
    public boolean isNegated() {
        return negated;
    }

    @Override
    public AstOperatorTernary negate() {
        return new AstOperatorTernary(this, !negated);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstOperatorTernary that = (AstOperatorTernary) o;
        return condition.equals(that.condition) && trueBranch.equals(that.trueBranch) && falseBranch.equals(that.falseBranch)
                && negated == that.negated;
    }

    @Override
    public int hashCode() {
        int result = condition.hashCode();
        result = 31 * result + trueBranch.hashCode();
        result = 31 * result + falseBranch.hashCode();
        result = 31 * result + Boolean.hashCode(negated);
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.IF;
    }
}
