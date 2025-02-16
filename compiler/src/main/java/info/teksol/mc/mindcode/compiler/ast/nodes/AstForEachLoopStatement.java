package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstForEachLoopStatement extends AstLabeledStatement {
    private final List<AstIteratorsValuesGroup> iteratorGroups;
    private final List<AstMindcodeNode> body;

    public AstForEachLoopStatement(SourcePosition sourcePosition, @Nullable AstIdentifier loopLabel,
            List<AstIteratorsValuesGroup> iteratorGroups, List<AstMindcodeNode> body) {
        super(sourcePosition, loopLabel, children(iteratorGroups, body));
        this.iteratorGroups = iteratorGroups;
        this.body = body;
    }

    public List<AstIteratorsValuesGroup> getIteratorGroups() {
        return iteratorGroups;
    }

    public List<AstMindcodeNode> getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AstForEachLoopStatement that = (AstForEachLoopStatement) o;
        return iteratorGroups.equals(that.iteratorGroups) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + iteratorGroups.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }
}
