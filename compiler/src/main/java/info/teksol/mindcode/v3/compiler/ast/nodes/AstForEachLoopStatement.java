package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AstForEachLoopStatement extends AstLabeledStatement {
    private final @NotNull List<@NotNull AstLoopIterator> iterators;
    private final @NotNull List<@NotNull AstExpression> values;
    private final @NotNull List<@NotNull AstMindcodeNode> body;

    public AstForEachLoopStatement(@NotNull InputPosition inputPosition, @Nullable AstIdentifier loopLabel,
            @NotNull List<@NotNull AstLoopIterator> iterators, @NotNull List<@NotNull AstExpression> values,
            @NotNull List<@NotNull AstMindcodeNode> body) {
        super(inputPosition, loopLabel);
        this.iterators = iterators;
        this.values = values;
        this.body = body;
    }

    public @NotNull List<@NotNull AstLoopIterator> getIterators() {
        return iterators;
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

    @Override
    public String toString() {
        return "AstForEachLoopStatement{" +
               "iterators=" + iterators +
               ", values=" + values +
               ", body=" + body +
               ", label=" + label +
               '}';
    }
}
