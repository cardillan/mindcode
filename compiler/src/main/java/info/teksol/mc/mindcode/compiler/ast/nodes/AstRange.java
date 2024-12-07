package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@AstNode
// Note: AstRange isn't a true expression, as it cannot be represented in compiled code
// It is kept in the expression subtree because of the case statement
public class AstRange extends AstExpression {
    private final AstExpression firstValue;
    private final AstExpression lastValue;
    private final boolean exclusive;

    public AstRange(SourcePosition sourcePosition, AstExpression firstValue,
            AstExpression lastValue, boolean exclusive) {
        super(sourcePosition, children(firstValue, lastValue));
        this.firstValue = firstValue;
        this.lastValue = lastValue;
        this.exclusive = exclusive;
    }

    public AstExpression getFirstValue() {
        return firstValue;
    }

    public AstExpression getLastValue() {
        return lastValue;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstRange astRange = (AstRange) o;
        return exclusive == astRange.exclusive && firstValue.equals(astRange.firstValue) && lastValue.equals(astRange.lastValue);
    }

    @Override
    public int hashCode() {
        int result = firstValue.hashCode();
        result = 31 * result + lastValue.hashCode();
        result = 31 * result + Boolean.hashCode(exclusive);
        return result;
    }

}
