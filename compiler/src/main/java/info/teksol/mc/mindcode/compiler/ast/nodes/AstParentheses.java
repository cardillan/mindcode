package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@AstNode(printFlat = true)
public class AstParentheses extends AstExpression {
    private final AstExpression expression;

    public AstParentheses(SourcePosition sourcePosition, AstExpression expression) {
        super(sourcePosition, children(expression));
        this.expression = expression;
    }

    public AstExpression getExpression() {
        return expression;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstParentheses that = (AstParentheses) o;
        return expression.equals(that.expression);
    }

    @Override
    public int hashCode() {
        return expression.hashCode();
    }

}
