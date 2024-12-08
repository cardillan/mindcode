package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AstAllocation extends AstBaseMindcodeNode {
    public enum AllocationType { HEAP, STACK }

    private final @NotNull AllocationType type;
    private final @NotNull AstIdentifier memory;
    private final @Nullable AstRange range;

    public AstAllocation(@NotNull InputPosition inputPosition, @NotNull AllocationType type, @NotNull AstIdentifier memory,
            @Nullable AstRange range) {
        super(inputPosition);
        this.type = type;
        this.memory = memory;
        this.range = range;
    }

    public @NotNull AllocationType getType() {
        return type;
    }

    public @NotNull AstIdentifier getMemory() {
        return memory;
    }

    public @Nullable AstRange getRange() {
        return range;
    }

    @Override
    public boolean equals(Object o) {
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
    public String toString() {
        return "AstAllocation{" +
               "type=" + type +
               ", memory=" + memory +
               ", range=" + range +
               '}';
    }
}
