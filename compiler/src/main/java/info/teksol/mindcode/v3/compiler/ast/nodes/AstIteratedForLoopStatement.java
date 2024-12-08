package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class AstIteratedForLoopStatement extends AstLabeledStatement {
    private final @NotNull List<@NotNull AstExpression> initialize;
    private final @Nullable AstExpression condition;
    private final @NotNull List<@NotNull AstExpression> update;
    private final @NotNull List<@NotNull AstMindcodeNode> body;

    public AstIteratedForLoopStatement(@NotNull InputPosition inputPosition, @Nullable AstIdentifier loopLabel,
            @NotNull List<@NotNull AstExpression> initialize, @Nullable AstExpression condition,
            @NotNull List<@NotNull AstExpression> update, @NotNull List<@NotNull AstMindcodeNode> body) {
        super(inputPosition, loopLabel);
        this.initialize = initialize;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    public @NotNull List<@NotNull AstExpression> getInitialize() {
        return initialize;
    }

    public @Nullable AstExpression getCondition() {
        return condition;
    }

    public @NotNull List<@NotNull AstExpression> getUpdate() {
        return update;
    }

    public @NotNull List<@NotNull AstMindcodeNode> getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
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

    @Override
    public String toString() {
        return "AstIteratedForLoopStatement{" +
               "initialize=" + initialize +
               ", condition=" + condition +
               ", update=" + update +
               ", body=" + body +
               ", label=" + label +
               '}';
    }
}
