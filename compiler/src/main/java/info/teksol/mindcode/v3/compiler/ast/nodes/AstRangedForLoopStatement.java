package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AstRangedForLoopStatement extends AstLoopStatement {
    private final @NotNull AstIdentifier variable;
    private final @NotNull AstRange range;
    private final @NotNull List<@NotNull AstMindcodeNode> body;

    public AstRangedForLoopStatement(@NotNull InputPosition inputPosition, @Nullable AstIdentifier loopLabel,
            @NotNull AstIdentifier variable, @NotNull AstRange range, @NotNull List<@NotNull AstMindcodeNode> body) {
        super(inputPosition, loopLabel);
        this.variable = variable;
        this.range = range;
        this.body = body;
    }

    public @NotNull AstIdentifier getVariable() {
        return variable;
    }

    public @NotNull AstRange getRange() {
        return range;
    }

    public @NotNull List<@NotNull AstMindcodeNode> getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AstRangedForLoopStatement that = (AstRangedForLoopStatement) o;
        return variable.equals(that.variable) && range.equals(that.range) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + variable.hashCode();
        result = 31 * result + range.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AstRangedForLoopStatement{" +
               "variable=" + variable +
               ", range=" + range +
               ", body=" + body +
               ", loopLabel=" + loopLabel +
               '}';
    }
}
