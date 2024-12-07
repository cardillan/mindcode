package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstWhileLoopStatement extends AstLabeledStatement {
    private final AstExpression condition;
    private final List<AstMindcodeNode> body;
    private final boolean entryCondition;

    public AstWhileLoopStatement(SourcePosition sourcePosition, @Nullable AstIdentifier loopLabel,
            AstExpression condition, List<AstMindcodeNode> body, boolean entryCondition) {
        super(sourcePosition, loopLabel, children(list(condition), body));
        this.condition = condition;
        this.body = body;
        this.entryCondition = entryCondition;
    }

    public AstExpression getCondition() {
        return condition;
    }

    public List<AstMindcodeNode> getBody() {
        return body;
    }

    public boolean isEntryCondition() {
        return entryCondition;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AstWhileLoopStatement that = (AstWhileLoopStatement) o;
        return entryCondition == that.entryCondition && condition.equals(that.condition) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + condition.hashCode();
        result = 31 * result + body.hashCode();
        result = 31 * result + Boolean.hashCode(entryCondition);
        return result;
    }
}
