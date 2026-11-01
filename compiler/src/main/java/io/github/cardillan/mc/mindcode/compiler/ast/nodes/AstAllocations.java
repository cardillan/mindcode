package io.github.cardillan.mc.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode(printFlat = true)
public class AstAllocations extends AstDeclaration {
    private final List<AstAllocation> allocations;

    public AstAllocations(SourcePosition sourcePosition, List<AstAllocation> allocations) {
        super(sourcePosition, children(allocations));
        this.allocations = allocations;
    }

    public List<AstAllocation> getAllocations() {
        return allocations;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstAllocations that = (AstAllocations) o;
        return allocations.equals(that.allocations);
    }

    @Override
    public int hashCode() {
        return allocations.hashCode();
    }
}
