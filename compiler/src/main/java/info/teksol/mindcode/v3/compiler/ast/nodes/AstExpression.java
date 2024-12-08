package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AstExpression extends AstBaseMindcodeNode {

    protected AstExpression(@NotNull InputPosition inputPosition) {
        super(inputPosition);
    }

    protected AstExpression(@NotNull InputPosition inputPosition, @Nullable AstDocComment docComment) {
        super(inputPosition, docComment);
    }
}
