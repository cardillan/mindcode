package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode
public class AstStatementList extends AstStatement {
    private final List<AstMindcodeNode> statements;

    public AstStatementList(SourcePosition sourcePosition, List<AstMindcodeNode> statements) {
        super(sourcePosition, statements);
        this.statements = Objects.requireNonNull(statements);
    }

    public List<AstMindcodeNode> getStatements() {
        return statements;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstStatementList that = (AstStatementList) o;
        return Objects.equals(statements, that.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(statements);
    }

}
