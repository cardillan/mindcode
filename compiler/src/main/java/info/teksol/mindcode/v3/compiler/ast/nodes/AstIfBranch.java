package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AstIfBranch extends AstBaseMindcodeNode {
    private final @NotNull AstMindcodeNode condition;
    private final @NotNull List<@NotNull AstMindcodeNode> body;

    public AstIfBranch(@NotNull InputPosition inputPosition, @NotNull AstMindcodeNode condition,
            @NotNull List<@NotNull AstMindcodeNode> body) {
        super(inputPosition);
        this.condition = condition;
        this.body = body;
    }

    public @NotNull AstMindcodeNode getCondition() {
        return condition;
    }

    public @NotNull List<@NotNull AstMindcodeNode> getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
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

    @Override
    public String toString() {
        return "AstIfBranch{" +
               "condition=" + condition +
               ", body=" + body +
               '}';
    }
}
