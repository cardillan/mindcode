package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstAllocation extends AstDeclaration {
    public enum AllocationType { HEAP, STACK }

    private final AllocationType type;
    private final @Nullable AstExternalParameters parameters;
    private final @Nullable List<AstExpression> inlinedSpecification;

    public AstAllocation(SourcePosition sourcePosition, AllocationType type, AstExternalParameters parameters) {
        super(sourcePosition, children(parameters));
        this.type = type;
        this.parameters = parameters;
        this.inlinedSpecification = null;
    }

    public AstAllocation(SourcePosition sourcePosition, AllocationType type, List<AstExpression> inlinedSpecification) {
        super(sourcePosition, children(inlinedSpecification));
        this.type = type;
        this.parameters = null;
        this.inlinedSpecification = inlinedSpecification;
    }

    @Override
    public AstNodeScope getScopeRestriction() {
        // Allocation produces its own error messages
        return AstNodeScope.NONE;
    }

    public AllocationType getType() {
        return type;
    }

    public @Nullable AstExternalParameters getParameters() {
        return parameters;
    }

    public @Nullable List<AstExpression> getInlinedSpecification() {
        return inlinedSpecification;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstAllocation that = (AstAllocation) o;
        return type == that.type && Objects.equals(parameters, that.parameters) && Objects.equals(inlinedSpecification, that.inlinedSpecification);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + Objects.hashCode(parameters);
        result = 31 * result + Objects.hashCode(inlinedSpecification);
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.ALLOCATION;
    }
}
