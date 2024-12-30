package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstForEachLoopStatement extends AstLabeledStatement {
    private final List<AstLoopIterator> iterators;
    private final List<AstExpression> values;
    private final List<AstMindcodeNode> body;

    public AstForEachLoopStatement(InputPosition inputPosition, @Nullable AstIdentifier loopLabel,
            List<AstLoopIterator> iterators, List<AstExpression> values,
            List<AstMindcodeNode> body) {
        super(inputPosition, loopLabel, children(iterators, values, body));
        this.iterators = iterators;
        this.values = values;
        this.body = body;
    }

    public List<AstLoopIterator> getIterators() {
        return iterators;
    }

    public List<AstExpression> getValues() {
        return values;
    }

    public List<AstMindcodeNode> getBody() {
        return body;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AstForEachLoopStatement that = (AstForEachLoopStatement) o;
        return iterators.equals(that.iterators) && values.equals(that.values) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + iterators.hashCode();
        result = 31 * result + values.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }

}
