package info.teksol.mc.mindcode.compiler.ast.nodes;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
}
