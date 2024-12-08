package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AstCaseExpression extends AstExpression {
    private final @NotNull AstExpression expression;
    private final @NotNull List<@NotNull AstCaseAlternative> alternatives;
    private final @NotNull List<@NotNull AstMindcodeNode> elseBranch;

    public AstCaseExpression(@NotNull InputPosition inputPosition, @NotNull AstExpression expression,
            @NotNull List<@NotNull AstCaseAlternative> alternatives,@NotNull List<@NotNull AstMindcodeNode> elseBranch) {
        super(inputPosition);
        this.expression = expression;
        this.alternatives = alternatives;
        this.elseBranch = elseBranch;
    }

    public @NotNull AstExpression getExpression() {
        return expression;
    }

    public @NotNull List<@NotNull AstCaseAlternative> getAlternatives() {
        return alternatives;
    }

    public @NotNull List<@NotNull AstMindcodeNode> getElseBranch() {
        return elseBranch;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstCaseExpression that = (AstCaseExpression) o;
        return expression.equals(that.expression) && alternatives.equals(that.alternatives) && elseBranch.equals(that.elseBranch);
    }

    @Override
    public int hashCode() {
        int result = expression.hashCode();
        result = 31 * result + alternatives.hashCode();
        result = 31 * result + elseBranch.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AstCaseExpression{" +
               "expression=" + expression +
               ", alternatives=" + alternatives +
               ", elseBranch=" + elseBranch +
               '}';
    }
}
