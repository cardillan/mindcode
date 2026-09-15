package info.teksol.mc.mindcode.compiler.ast.nodes;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public interface ExternalStorage extends AstMindcodeNode {

    AstIdentifier getMemory();

    @Nullable AstRange getRange();

    default @Nullable AstExpression getStartIndex() {
        return null;
    }

    default boolean hasRangeOrIndex() {
        return getRange() != null || getStartIndex() != null;
    }

    default AstExpression getRangeOrIndex() {
        return getRange() != null ? getRange() : Objects.requireNonNull(getStartIndex());
    }
}
