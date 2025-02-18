package info.teksol.mc.mindcode.compiler.ast.nodes;

import org.jspecify.annotations.Nullable;

public interface ExternalStorage extends AstMindcodeNode {

    AstIdentifier getMemory();

    @Nullable AstRange getRange();

    default @Nullable AstExpression getStartIndex() {
        return null;
    }
}
