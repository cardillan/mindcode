package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class FunctionArgument extends BaseAstNode {
    private final AstNode expression;
    private final boolean inModifier;
    private final boolean outModifier;

    public FunctionArgument(InputPosition inputPosition, AstNode expression, boolean inModifier, boolean outModifier) {
        super(inputPosition, expression);
        this.expression = Objects.requireNonNull(expression);
        this.inModifier = inModifier;
        this.outModifier = outModifier;
    }

    public FunctionArgument(InputPosition inputPosition, boolean inModifier, boolean outModifier) {
        super(inputPosition);
        this.expression = null;
        this.inModifier = inModifier;
        this.outModifier = outModifier;
    }

    public AstNode getExpression() {
        return expression;
    }

    public boolean hasExpression() {
        return expression != null;
    }

    public boolean hasModifier() {
        return inModifier || outModifier;
    }

    public boolean hasInModifier() {
        return inModifier;
    }

    public boolean hasOutModifier() {
        return outModifier;
    }

    public boolean isInput() {
        return inModifier || !outModifier;
    }

    public boolean isOutput() {
        return outModifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionArgument that = (FunctionArgument) o;
        return inModifier == that.inModifier && outModifier == that.outModifier && expression.equals(that.expression);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(expression);
        result = 31 * result + Boolean.hashCode(inModifier);
        result = 31 * result + Boolean.hashCode(outModifier);
        return result;
    }

    @Override
    public String toString() {
        return "FunctionArgument{" +
                "expression=" + expression +
                ", inModifier=" + inModifier +
                ", outModifier=" + outModifier +
                '}';
    }
}
