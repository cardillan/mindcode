package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode
public class AstExternalParameters extends AstFragment {
    private final @Nullable AstIdentifier memory;
    private final @Nullable AstRange range;
    private final @Nullable AstExpression startIndex;
    private final @Nullable List<AstExpression> inlinedBlockList;

    public AstExternalParameters(SourcePosition sourcePosition, AstIdentifier memory, @Nullable AstRange range, @Nullable AstExpression startIndex) {
        super(sourcePosition, children(memory, range, startIndex));
        this.memory = memory;
        this.range = range;
        this.startIndex = startIndex;
        this.inlinedBlockList = null;
    }

    public AstExternalParameters(SourcePosition sourcePosition, List<AstExpression> inlinedBlockList) {
        super(sourcePosition, children(inlinedBlockList));
        this.memory = null;
        this.range = null;
        this.startIndex = null;
        this.inlinedBlockList = inlinedBlockList;
    }

    public @Nullable AstIdentifier getMemory() {
        return memory;
    }

    public @Nullable AstRange getRange() {
        return range;
    }

    public @Nullable AstExpression getStartIndex() {
        return startIndex;
    }

    public @Nullable List<AstExpression> getInlinedBlockList() {
        return inlinedBlockList;
    }

    public boolean hasRangeOrIndex() {
        return getRange() != null || getStartIndex() != null;
    }

    public AstExpression getRangeOrIndex() {
        return getRange() != null ? getRange() : Objects.requireNonNull(getStartIndex());
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstExternalParameters that = (AstExternalParameters) o;
        return Objects.equals(memory, that.memory) && Objects.equals(range, that.range)
                && Objects.equals(startIndex, that.startIndex) && Objects.equals(inlinedBlockList, that.inlinedBlockList);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(memory);
        result = 31 * result + Objects.hashCode(range);
        result = 31 * result + Objects.hashCode(startIndex);
        result = 31 * result + Objects.hashCode(inlinedBlockList);
        return result;
    }
}
