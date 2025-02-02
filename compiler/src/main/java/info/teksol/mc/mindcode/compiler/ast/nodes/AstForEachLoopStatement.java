package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstForEachLoopStatement extends AstLabeledStatement {
    private final List<AstLoopIteratorGroup> iterators;
    private final List<AstExpressionList> valueLists;
    private final List<AstMindcodeNode> body;

    public AstForEachLoopStatement(SourcePosition sourcePosition, @Nullable AstIdentifier loopLabel,
            List<AstLoopIteratorGroup> iterators, List<AstExpressionList> valueLists, List<AstMindcodeNode> body) {
        super(sourcePosition, loopLabel, children(iterators, valueLists, body));
        this.iterators = iterators;
        this.valueLists = valueLists;
        this.body = body;
    }

    public List<AstLoopIteratorGroup> getIterators() {
        return iterators;
    }

    public List<AstExpressionList> getValueLists() {
        return valueLists;
    }

    public List<AstMindcodeNode> getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AstForEachLoopStatement that = (AstForEachLoopStatement) o;
        return iterators.equals(that.iterators) && valueLists.equals(that.valueLists) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + iterators.hashCode();
        result = 31 * result + valueLists.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }
}
