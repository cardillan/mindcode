package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@AstNode
public class AstCaseExpression extends AstExpression {
    private final AstExpression expression;
    private final List< AstCaseAlternative> alternatives;
    private final List< AstMindcodeNode> elseBranch;

    public AstCaseExpression(InputPosition inputPosition, AstExpression expression,
            List< AstCaseAlternative> alternatives, List< AstMindcodeNode> elseBranch) {
        super(inputPosition);
        this.expression = expression;
        this.alternatives = alternatives;
        this.elseBranch = elseBranch;
    }

    public AstExpression getExpression() {
        return expression;
    }

    public List< AstCaseAlternative> getAlternatives() {
        return alternatives;
    }

    public List< AstMindcodeNode> getElseBranch() {
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
