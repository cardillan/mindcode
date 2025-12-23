package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode
public class AstOperatorInList extends AstExpression implements AstNegatable<AstOperatorInList> {
    private final boolean negated;
    private final AstExpression value;
    private final List<AstExpression> values;

    public AstOperatorInList(SourcePosition sourcePosition, boolean negated,
            AstExpression value, List<AstExpression> values) {
        super(sourcePosition, children(List.of(value), values));
        this.value = Objects.requireNonNull(value);
        this.values = Objects.requireNonNull(values);
        this.negated = negated;
    }

    public AstOperatorInList(AstOperatorInList other, boolean negated) {
        this(other.sourcePosition(), negated, other.value, other.values);
        setProfile(other.getProfile());
    }

    public AstExpression getValue() {
        return value;
    }

    public List<AstExpression> getValues() {
        return values;
    }

    @Override
    public boolean isNegated() {
        return negated;
    }

    @Override
    public AstOperatorInList negate() {
        return new AstOperatorInList(this, !negated);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstOperatorInList that = (AstOperatorInList) o;
        return negated == that.negated && value.equals(that.value) && values.equals(that.values);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(negated);
        result = 31 * result + value.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.OPERATOR;
    }
}
