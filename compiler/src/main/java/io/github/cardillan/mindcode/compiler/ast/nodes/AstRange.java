package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@AstNode
// Note: AstRange isn't a true expression, as it cannot be represented in compiled code
// It is kept in the expression subtree because of the case statement
public class AstRange extends AstExpression {
    private final AstExpression first;
    private final AstExpression last;
    private final boolean exclusive;

    public AstRange(SourcePosition sourcePosition, AstExpression first, AstExpression last, boolean exclusive) {
        super(sourcePosition, children(first, last));
        this.first = first;
        this.last = last;
        this.exclusive = exclusive;
    }

    public AstExpression getFirst() {
        return first;
    }

    public AstExpression getLast() {
        return last;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstRange astRange = (AstRange) o;
        return exclusive == astRange.exclusive && first.equals(astRange.first) && last.equals(astRange.last);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + last.hashCode();
        result = 31 * result + Boolean.hashCode(exclusive);
        return result;
    }

}
