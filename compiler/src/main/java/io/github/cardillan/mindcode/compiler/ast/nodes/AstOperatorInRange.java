package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode
public class AstOperatorInRange extends AstExpression implements AstNegatable<AstOperatorInRange> {
    private final boolean negated;
    private final AstExpression value;
    private final AstRange range;

    public AstOperatorInRange(SourcePosition sourcePosition, boolean negated,
            AstExpression value, AstRange range) {
        super(sourcePosition, children(value, range));
        this.negated = negated;
        this.value = Objects.requireNonNull(value);
        this.range = Objects.requireNonNull(range);
    }

    private AstOperatorInRange(AstOperatorInRange other, boolean negated) {
        this(other.sourcePosition(), negated, other.value, other.range);
        setProfile(other.getProfile());
    }

    public AstExpression getValue() {
        return value;
    }

    public AstRange getRange() {
        return range;
    }

    @Override
    public boolean isNegated() {
        return negated;
    }

    @Override
    public AstOperatorInRange negate() {
        return new AstOperatorInRange(this, !negated);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstOperatorInRange that = (AstOperatorInRange) o;
        return negated == that.negated && value.equals(that.value) && range.equals(that.range);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(negated);
        result = 31 * result + value.hashCode();
        result = 31 * result + range.hashCode();
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.OPERATOR;
    }
}
