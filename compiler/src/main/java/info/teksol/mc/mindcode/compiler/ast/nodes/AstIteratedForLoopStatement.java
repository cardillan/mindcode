package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode
public class AstIteratedForLoopStatement extends AstLabeledStatement {
    private final List<AstExpression> initialize;
    private final @Nullable AstExpression condition;
    private final List<AstExpression> update;
    private final List<AstMindcodeNode> body;

    public AstIteratedForLoopStatement(SourcePosition sourcePosition, @Nullable AstIdentifier loopLabel,
            List<AstExpression> initialize, @Nullable AstExpression condition,
            List<AstExpression> update, List<AstMindcodeNode> body) {
        super(sourcePosition, loopLabel, children(initialize, list(condition), update, body));
        this.initialize = initialize;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    public List<AstExpression> getInitialize() {
        return initialize;
    }

    public @Nullable AstExpression getCondition() {
        return condition;
    }

    public List<AstExpression> getUpdate() {
        return update;
    }

    public List<AstMindcodeNode> getBody() {
        return body;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AstIteratedForLoopStatement that = (AstIteratedForLoopStatement) o;
        return initialize.equals(that.initialize) && Objects.equals(condition, that.condition) && update.equals(that.update) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + initialize.hashCode();
        result = 31 * result + Objects.hashCode(condition);
        result = 31 * result + update.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }

}
