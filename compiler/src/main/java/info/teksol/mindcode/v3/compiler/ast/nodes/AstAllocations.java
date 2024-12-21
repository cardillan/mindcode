package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@AstNode(printFlat = true)
public class AstAllocations extends AstDeclaration {
    private final List<AstAllocation> allocations;

    public AstAllocations(InputPosition inputPosition, List<AstAllocation> allocations) {
        super(inputPosition, children(allocations));
        this.allocations = allocations;
    }

    public List<AstAllocation> getAllocations() {
        return allocations;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstAllocations that = (AstAllocations) o;
        return allocations.equals(that.allocations);
    }

    @Override
    public int hashCode() {
        return allocations.hashCode();
    }
}
