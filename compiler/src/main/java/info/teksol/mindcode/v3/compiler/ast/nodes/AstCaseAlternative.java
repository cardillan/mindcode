package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AstCaseAlternative extends AstFragment {
    private final @NotNull List<@NotNull AstExpression> values;
    private final @NotNull List<@NotNull AstMindcodeNode> body;

    public AstCaseAlternative(@NotNull InputPosition inputPosition, @NotNull List<@NotNull AstExpression> values,
            @NotNull List<@NotNull AstMindcodeNode> body) {
        super(inputPosition);
        this.values = values;
        this.body = body;
    }

    public @NotNull List<@NotNull AstExpression> getValues() {
        return values;
    }

    public @NotNull List<@NotNull AstMindcodeNode> getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
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

    @Override
    public String toString() {
        return "AstCaseAlternative{" +
               "values=" + values +
               ", body=" + body +
               '}';
    }
}
