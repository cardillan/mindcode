package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode
public class AstRange extends AstExpression {
    private final AstExpression firstValue;
    private final AstExpression lastValue;
    private final boolean exclusive;

    public AstRange(InputPosition inputPosition, AstExpression firstValue,
            AstExpression lastValue, boolean exclusive) {
        super(inputPosition);
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
    public boolean equals(Object o) {
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

    @Override
    public String toString() {
        return "AstRange{" +
               "firstValue=" + firstValue +
               ", lastValue=" + lastValue +
               ", exclusive=" + exclusive +
               '}';
    }
}
