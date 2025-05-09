package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;

@AstNode
@NullMarked
public class AstModule extends AstStatement {
    public static AstModule DEFAULT = new AstModule(SourcePosition.EMPTY,
            null, List.of(), Collections.emptySortedSet());

    /// Module declaration
    private final @Nullable AstModuleDeclaration declaration;

    /// List of module statements. Module declaration, if any, is included in the list
    private final List<AstMindcodeNode> statements;

    /// Empty for local modules. For remote modules, identifies the processor(s) containing module code.
    private final SortedSet<AstIdentifier> remoteProcessors;

    public AstModule(SourcePosition sourcePosition, @Nullable AstModuleDeclaration declaration, List<AstMindcodeNode> statements,
            SortedSet<AstIdentifier> remoteProcessors) {
        super(sourcePosition, children(list(declaration), statements));
        this.declaration = declaration;
        this.statements = statements;
        this.remoteProcessors = remoteProcessors;
    }

    public @Nullable AstModuleDeclaration getDeclaration() {
        return declaration;
    }

    public String getModuleName() {
        return declaration == null ? "" : declaration.getModuleName();
    }

    public List<AstMindcodeNode> getStatements() {
        return statements;
    }

    public SortedSet<AstIdentifier> getRemoteProcessors() {
        return remoteProcessors;
    }

    public boolean matchesProcessor(String name) {
        return remoteProcessors.stream().anyMatch(identifier -> identifier.getName().equals(name));
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
