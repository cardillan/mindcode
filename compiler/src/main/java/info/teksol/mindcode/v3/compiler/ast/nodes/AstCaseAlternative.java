package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AstCaseAlternative extends AstBaseMindcodeNode {
    private final @NotNull List<@NotNull AstMindcodeNode> values;
    private final @NotNull AstMindcodeNode body;

    public AstCaseAlternative(InputPosition inputPosition, @NotNull List<@NotNull AstMindcodeNode> values,
            @NotNull AstMindcodeNode body) {
        super(inputPosition);
        this.values = values;
        this.body = body;
    }

    public @NotNull List<@NotNull AstMindcodeNode> getValues() {
        return values;
    }

    public @NotNull AstMindcodeNode getBody() {
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
