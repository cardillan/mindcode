package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstMlogBlock extends AstDeclaration {
    private final List<AstMlogVariable> variables;
    private final List<AstMlogStatement> statements;

    public AstMlogBlock(SourcePosition sourcePosition, List<AstMlogVariable> variables, List<AstMlogStatement> statements) {
        super(sourcePosition, children(variables, statements));
        this.variables = variables;
        this.statements = statements;
    }

    public List<AstMlogVariable> getVariables() {
        return variables;
    }

    public AstMlogVariable getVariable(int index) {
        return variables.get(index);
    }

    public List<AstMlogStatement> getStatements() {
        return statements;
    }

    public AstMlogStatement getStatement(int index) {
        return statements.get(index);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstMlogBlock that = (AstMlogBlock) o;
        return variables.equals(that.variables) && statements.equals(that.statements);
    }

    @Override
    public int hashCode() {
        int result = variables.hashCode();
        result = 31 * result + statements.hashCode();
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.MLOG;
    }

    @Override
    public AstSubcontextType getSubcontextType() {
        return AstSubcontextType.BODY;
    }
}
