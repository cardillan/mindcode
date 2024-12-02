package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class AstOperatorTernary extends AstBaseMindcodeNode {
    private final AstMindcodeNode condition;
    private final AstMindcodeNode trueBranch;
    private final AstMindcodeNode falseBranch;

    public AstOperatorTernary(InputPosition inputPosition, AstMindcodeNode condition, AstMindcodeNode trueBranch, AstMindcodeNode falseBranch) {
        super(inputPosition);
        this.condition = Objects.requireNonNull(condition);
        this.trueBranch = Objects.requireNonNull(trueBranch);
        this.falseBranch = Objects.requireNonNull(falseBranch);
    }

    public AstMindcodeNode getCondition() {
        return condition;
    }

    public AstMindcodeNode getTrueBranch() {
        return trueBranch;
    }

    public AstMindcodeNode getFalseBranch() {
        return falseBranch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstOperatorTernary that = (AstOperatorTernary) o;
        return condition.equals(that.condition) && trueBranch.equals(that.trueBranch) && falseBranch.equals(that.falseBranch);
    }

    @Override
    public int hashCode() {
        int result = condition.hashCode();
        result = 31 * result + trueBranch.hashCode();
        result = 31 * result + falseBranch.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AstOperatorTernary{" +
               "condition=" + condition +
               ", trueBranch=" + trueBranch +
               ", falseBranch=" + falseBranch +
               '}';
    }
}