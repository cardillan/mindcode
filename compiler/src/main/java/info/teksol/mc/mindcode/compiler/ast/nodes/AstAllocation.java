package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstAllocation extends AstDeclaration {
    public enum AllocationType { HEAP, STACK }

    private final AllocationType type;
    private final AstExternalParameters parameters;

    public AstAllocation(SourcePosition sourcePosition, AllocationType type, AstExternalParameters parameters) {
        super(sourcePosition, children(parameters));
        this.type = type;
        this.parameters = parameters;
    }

    @Override
    public AstNodeScope getScopeRestriction() {
        // Allocation produces its own error messages
        return AstNodeScope.NONE;
    }

    public AllocationType getType() {
        return type;
    }

    public AstExternalParameters getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstAllocation that = (AstAllocation) o;
        return type == that.type && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + Objects.hashCode(parameters);
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.ALLOCATION;
    }
}
