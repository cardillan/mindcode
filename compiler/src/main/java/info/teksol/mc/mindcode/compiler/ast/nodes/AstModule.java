package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@AstNode
@NullMarked
public class AstModule extends AstStatement {
    private final @Nullable AstModuleDeclaration declaration;
    private final List<AstMindcodeNode> statements;

    public AstModule(SourcePosition sourcePosition, @Nullable AstModuleDeclaration declaration, List<AstMindcodeNode> statements) {
        super(sourcePosition, children(list(declaration), statements));
        this.declaration = declaration;
        this.statements = statements;
    }

    public @Nullable AstModuleDeclaration getDeclaration() {
        return declaration;
    }

    public List<AstMindcodeNode> getStatements() {
        return statements;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstModule astModule = (AstModule) o;
        return Objects.equals(declaration, astModule.declaration) && statements.equals(astModule.statements);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(declaration);
        result = 31 * result + statements.hashCode();
        return result;
    }

    @Override
    public AstNodeScope getScope() {
        return AstNodeScope.GLOBAL;
    }

    @Override
    public AstNodeScope getScopeRestriction() {
        return AstNodeScope.GLOBAL;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.ROOT;
    }

    @Override
    public AstSubcontextType getSubcontextType() {
        return AstSubcontextType.BODY;
    }
}
