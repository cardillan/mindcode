package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstLoopStatement extends AstLabeledStatement {
    private final List<AstMindcodeNode> body;

    public AstLoopStatement(SourcePosition sourcePosition, @Nullable AstIdentifier loopLabel, List<AstMindcodeNode> body) {
        super(sourcePosition, loopLabel, body);
        this.body = body;
    }

    @Override
    public AstNodeScope getScopeRestriction() {
        return AstNodeScope.NONE;
    }

    public List<AstMindcodeNode> getBody() {
        return body;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AstLoopStatement that = (AstLoopStatement) o;
        return body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }
}
