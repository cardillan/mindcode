package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/// An AST node which can be used multiple times within the AST tree; the resulting value from the
/// first evaluation is reused.
@NullMarked
@AstNode
public class AstCachedNode extends AstExpression {
    private final AstExpression expression;

    public AstCachedNode(AstExpression expression) {
        super(expression.sourcePosition(), children(expression));
        this.expression = Objects.requireNonNull(expression);
        setProfile(expression.getProfile());
    }

    public AstExpression getExpression() {
        return expression;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstCachedNode that = (AstCachedNode) o;
        return expression.equals(that.expression);
    }

    @Override
    public int hashCode() {
        return expression.hashCode();
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.OPERATOR;
    }
}
