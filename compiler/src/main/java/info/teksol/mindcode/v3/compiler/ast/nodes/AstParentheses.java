package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AstParentheses extends AstExpression {
    private final AstExpression expression;

    public AstParentheses(InputPosition inputPosition, AstExpression expression) {
        super(inputPosition);
        this.expression = expression;
    }

    public AstExpression getExpression() {
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
