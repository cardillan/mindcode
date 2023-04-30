package info.teksol.mindcode.ast;

import info.teksol.mindcode.logic.Condition;

import java.util.Objects;

public abstract class Range extends BaseAstNode {
    private final AstNode firstValue;
    private final AstNode lastValue;

    Range(AstNode firstValue, AstNode lastValue) {
        super(firstValue, lastValue);
        this.firstValue = firstValue;
        this.lastValue = lastValue;
    }

    public AstNode getFirstValue() {
        return firstValue;
    }

    public AstNode getLastValue() {
        return lastValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range that = (Range) o;
        return Objects.equals(firstValue, that.firstValue) &&
                Objects.equals(lastValue, that.lastValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstValue, lastValue);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "firstValue=" + firstValue +
                ", lastValue=" + lastValue +
                '}';
    }

    public BinaryOp buildLoopExitCondition(AstNode name) {
        return new BinaryOp(
                name,
                maxValueComparison().getMindcode(),
                getLastValue()
        );
    }

    public abstract String operator();
    public abstract Condition maxValueComparison();
}
