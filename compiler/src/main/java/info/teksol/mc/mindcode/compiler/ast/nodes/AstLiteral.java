package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.InputPosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public abstract class AstLiteral extends AstExpression {
    protected final String literal;

    protected AstLiteral(InputPosition inputPosition, String literal) {
        super(inputPosition);
        this.literal = Objects.requireNonNull(literal);
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstLiteral that = (AstLiteral) o;
        return literal.equals(that.literal);
    }

    @Override
    public int hashCode() {
        return literal.hashCode();
    }

    public abstract AstLiteral withInputPosition(InputPosition inputPosition);

    // TODO Warn when integer conversion produces precision loss
    //      Determine whether something bad may happen with double literals (infinity?)
    //      or integer literals (larger than Long.MAX_VALUE?)
    public abstract double getDoubleValue();

    public abstract long getLongValue();
}
