package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

public class AstParentheses extends AstExpression {
    private final @NotNull AstExpression expression;

    public AstParentheses(@NotNull InputPosition inputPosition, @NotNull AstExpression expression) {
        super(inputPosition);
        this.expression = expression;
    }

    public @NotNull AstExpression getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstParentheses that = (AstParentheses) o;
        return expression.equals(that.expression);
    }

    @Override
    public int hashCode() {
        return expression.hashCode();
    }

    @Override
    public String toString() {
        return "AstParentheses{" +
               "expression=" + expression +
               '}';
    }
}
