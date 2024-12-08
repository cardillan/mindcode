package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AstStatementList extends AstStatement {
    private final @NotNull List<@NotNull AstMindcodeNode> expressions;

    public AstStatementList(@NotNull InputPosition inputPosition, @NotNull List<@NotNull AstMindcodeNode> expressions) {
        super(inputPosition);
        this.expressions = Objects.requireNonNull(expressions);
    }

    public @NotNull List<@NotNull AstMindcodeNode> getExpressions() {
        return expressions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstStatementList that = (AstStatementList) o;
        return Objects.equals(expressions, that.expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(expressions);
    }

    @Override
    public String toString() {
        return "AstStatementList{" +
               "expressions=" + expressions.stream().map(Object::toString).collect(Collectors.joining("\n", "\n", "\n")) +
               '}';
    }
}
