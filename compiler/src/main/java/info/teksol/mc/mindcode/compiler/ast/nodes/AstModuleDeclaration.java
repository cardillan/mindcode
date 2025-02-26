package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@AstNode(printFlat = true)
public class AstModuleDeclaration extends AstDeclaration {
    private final AstIdentifier name;

    public AstModuleDeclaration(SourcePosition sourcePosition, AstIdentifier name) {
        super(sourcePosition, children(name));
        this.name = name;
    }

    public AstIdentifier getName() {
        return name;
    }

    public String getParameterName() {
        return name.getName();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstModuleDeclaration that = (AstModuleDeclaration) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
