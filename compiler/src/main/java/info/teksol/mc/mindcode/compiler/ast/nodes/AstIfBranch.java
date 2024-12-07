package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstIfBranch extends AstFragment {
    private final AstExpression condition;
    private final List<AstMindcodeNode> body;

    public AstIfBranch(SourcePosition sourcePosition, AstExpression condition,
            List<AstMindcodeNode> body) {
        super(sourcePosition, children(list(condition), body));
        this.condition = condition;
        this.body = body;
    }

    public AstExpression getCondition() {
        return condition;
    }

    public List<AstMindcodeNode> getBody() {
        return body;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstIfBranch that = (AstIfBranch) o;
        return condition.equals(that.condition) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = condition.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }

}
