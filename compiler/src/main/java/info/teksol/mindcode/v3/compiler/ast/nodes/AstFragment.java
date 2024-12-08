package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AstFragment extends AstBaseMindcodeNode {

    protected AstFragment(@NotNull InputPosition inputPosition) {
        super(inputPosition);
    }

    protected AstFragment(@NotNull InputPosition inputPosition, @Nullable AstDocComment docComment) {
        super(inputPosition, docComment);
    }
}
