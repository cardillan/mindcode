package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class AstWhileLoopStatement extends AstLabeledStatement {
    private final @Nullable AstExpression condition;
    private final @NotNull List<@NotNull AstMindcodeNode> body;
    private final boolean entryCondition;

    public AstWhileLoopStatement(@NotNull InputPosition inputPosition, @Nullable AstIdentifier loopLabel,
            @Nullable AstExpression condition, @NotNull List<@NotNull AstMindcodeNode> body, boolean entryCondition) {
        super(inputPosition, loopLabel);
        this.condition = condition;
        this.body = body;
        this.entryCondition = entryCondition;
    }

    public @Nullable AstExpression getCondition() {
        return condition;
    }

    public @NotNull List<@NotNull AstMindcodeNode> getBody() {
        return body;
    }

    public boolean isEntryCondition() {
        return entryCondition;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AstWhileLoopStatement that = (AstWhileLoopStatement) o;
        return entryCondition == that.entryCondition && Objects.equals(condition, that.condition) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(condition);
        result = 31 * result + body.hashCode();
        result = 31 * result + Boolean.hashCode(entryCondition);
        return result;
    }

    @Override
    public String toString() {
        return "AstWhileLoopStatement{" +
               "condition=" + condition +
               ", body=" + body +
               ", entryCondition=" + entryCondition +
               ", label=" + label +
               '}';
    }
}
