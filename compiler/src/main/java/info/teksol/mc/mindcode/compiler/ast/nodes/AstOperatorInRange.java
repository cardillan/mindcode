package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode
public class AstOperatorInRange extends AstExpression {
    private final boolean negation;
    private final AstExpression value;
    private final AstRange range;

    public AstOperatorInRange(SourcePosition sourcePosition, boolean negation,
            AstExpression value, AstRange range) {
        super(sourcePosition, children(value, range));
        this.negation = negation;
        this.value = Objects.requireNonNull(value);
        this.range = Objects.requireNonNull(range);
    }

    public boolean isNegation() {
        return negation;
    }

    public AstExpression getValue() {
        return value;
    }

    public AstRange getRange() {
        return range;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstOperatorInRange that = (AstOperatorInRange) o;
        return negation == that.negation && value.equals(that.value) && range.equals(that.range);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(negation);
        result = 31 * result + value.hashCode();
        result = 31 * result + range.hashCode();
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.OPERATOR;
    }
}
