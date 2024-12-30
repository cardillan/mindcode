package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstRangedForLoopStatement extends AstLabeledStatement {
    private final AstExpression variable;
    private final AstRange range;
    private final List<AstMindcodeNode> body;

    public AstRangedForLoopStatement(InputPosition inputPosition, @Nullable AstIdentifier loopLabel,
            AstExpression variable, AstRange range, List<AstMindcodeNode> body) {
        super(inputPosition, loopLabel, children(list(variable, range), body));
        this.variable = variable;
        this.range = range;
        this.body = body;
    }

    public AstExpression getVariable() {
        return variable;
    }

    public AstRange getRange() {
        return range;
    }

    public List<AstMindcodeNode> getBody() {
        return body;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AstRangedForLoopStatement that = (AstRangedForLoopStatement) o;
        return variable.equals(that.variable) && range.equals(that.range) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + variable.hashCode();
        result = 31 * result + range.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }

}
