package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public abstract class AstLiteral extends AstExpression {
    protected final String literal;
    protected final boolean suppressWarning;

    protected AstLiteral(SourcePosition sourcePosition, String literal, boolean suppressWarning) {
        super(sourcePosition);
        this.literal = Objects.requireNonNull(literal);
        this.suppressWarning = suppressWarning;
    }

    public String getLiteral() {
        return literal;
    }

    public boolean isSuppressWarning() {
        return suppressWarning;
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

    public abstract AstLiteral withSourcePosition(SourcePosition sourcePosition);

    public abstract double getDoubleValue();

    public abstract long getLongValue();

    public int getIntValue() {
        return (int) getLongValue();
    }
}
