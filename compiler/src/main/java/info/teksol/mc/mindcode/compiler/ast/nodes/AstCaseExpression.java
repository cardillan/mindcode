package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstCaseExpression extends AstExpression {
    private final AstExpression expression;
    private final List<AstCaseAlternative> alternatives;
    private final List<AstMindcodeNode> elseBranch;

    public AstCaseExpression(SourcePosition sourcePosition, AstExpression expression,
            List<AstCaseAlternative> alternatives, List<AstMindcodeNode> elseBranch) {
        super(sourcePosition, children(list(expression), alternatives, elseBranch));
        this.expression = expression;
        this.alternatives = alternatives;
        this.elseBranch = elseBranch;
    }

    public AstExpression getExpression() {
        return expression;
    }

    public List<AstCaseAlternative> getAlternatives() {
        return alternatives;
    }

    public List<AstMindcodeNode> getElseBranch() {
        return elseBranch;
    }

    @Override
    public boolean equals(@Nullable Object o) {
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
    public AstContextType getContextType() {
        return AstContextType.CASE;
    }
}
