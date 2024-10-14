package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;

import java.util.Objects;

public class IfExpression extends ControlBlockAstNode {
    private final AstNode condition;
    private final AstNode trueBranch;
    private final AstNode falseBranch;

    IfExpression(InputPosition inputPosition, AstNode condition, AstNode trueBranch, AstNode falseBranch) {
        super(inputPosition, condition, trueBranch, falseBranch);
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    public AstNode getCondition() {
        return condition;
    }

    public AstNode getTrueBranch() {
        return trueBranch;
    }

    public AstNode getFalseBranch() {
        return falseBranch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IfExpression that = (IfExpression) o;
        return Objects.equals(condition, that.condition) &&
                Objects.equals(trueBranch, that.trueBranch) &&
                Objects.equals(falseBranch, that.falseBranch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, trueBranch, falseBranch);
    }

    @Override
    public String toString() {
        return "IfExpression{" +
                "condition=" + condition +
                ", trueBranch=" + trueBranch +
                ", falseBranch=" + falseBranch +
                '}';
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.IF;
    }
}
