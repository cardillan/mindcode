package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.logic.Condition;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public abstract class Range extends BaseAstNode {
    private final AstNode firstValue;
    private final AstNode lastValue;

    Range(Token startToken, InputFile inputFile, AstNode firstValue, AstNode lastValue) {
        super(startToken, inputFile, firstValue, lastValue);
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

    public abstract String operator();

    public abstract Condition maxValueComparison();
}
