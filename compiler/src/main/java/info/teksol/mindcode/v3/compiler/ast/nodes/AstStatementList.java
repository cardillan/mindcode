package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode
public class AstStatementList extends AstStatement {
    private final List<AstMindcodeNode> statements;

    public AstStatementList(InputPosition inputPosition, List<AstMindcodeNode> statements) {
        super(inputPosition, statements);
        this.statements = Objects.requireNonNull(statements);
    }

    public List<AstMindcodeNode> getStatements() {
        return statements;
    }

    @Override
    public boolean equals(Object o) {
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
