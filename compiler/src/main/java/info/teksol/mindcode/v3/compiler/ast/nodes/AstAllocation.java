package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstAllocation extends AstDeclaration {
    public enum AllocationType { HEAP, STACK }

    private final AllocationType type;
    private final AstIdentifier memory;
    private final @Nullable AstRange range;

    public AstAllocation(InputPosition inputPosition, AllocationType type, AstIdentifier memory,
            @Nullable AstRange range) {
        super(inputPosition, children(memory, range));
        this.type = type;
        this.memory = memory;
        this.range = range;
    }

    public AllocationType getType() {
        return type;
    }

    public AstIdentifier getMemory() {
        return memory;
    }

    public @Nullable AstRange getRange() {
        return range;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstAllocation that = (AstAllocation) o;
        return type == that.type && memory.equals(that.memory) && Objects.equals(range, that.range);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + memory.hashCode();
        result = 31 * result + Objects.hashCode(range);
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.ALLOCATION;
    }
}