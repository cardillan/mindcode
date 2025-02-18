package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode
public class AstExternalParameters extends AstFragment implements ExternalStorage {
    private final AstIdentifier memory;
    private final @Nullable AstRange range;
    private final @Nullable AstExpression startIndex;

    public AstExternalParameters(SourcePosition sourcePosition, AstIdentifier memory, @Nullable AstRange range, @Nullable AstExpression startIndex) {
        super(sourcePosition, children(memory, range, startIndex));
        this.memory = memory;
        this.range = range;
        this.startIndex = startIndex;
    }


    public AstIdentifier getMemory() {
        return memory;
    }

    public @Nullable AstRange getRange() {
        return range;
    }

    public @Nullable AstExpression getStartIndex() {
        return startIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstExternalParameters that = (AstExternalParameters) o;
        return memory.equals(that.memory) && Objects.equals(range, that.range) && Objects.equals(startIndex, that.startIndex);
    }

    @Override
    public int hashCode() {
        int result = memory.hashCode();
        result = 31 * result + Objects.hashCode(range);
        result = 31 * result + Objects.hashCode(startIndex);
        return result;
    }
}
