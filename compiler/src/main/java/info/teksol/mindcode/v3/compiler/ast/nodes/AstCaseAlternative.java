package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstCaseAlternative extends AstFragment {
    private final List<AstExpression> values;
    private final List<AstMindcodeNode> body;

    public AstCaseAlternative(InputPosition inputPosition, List<AstExpression> values,
            List<AstMindcodeNode> body) {
        super(inputPosition, children(values, body));
        this.values = values;
        this.body = body;
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

        AstCaseAlternative that = (AstCaseAlternative) o;
        return values.equals(that.values) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = values.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }

}