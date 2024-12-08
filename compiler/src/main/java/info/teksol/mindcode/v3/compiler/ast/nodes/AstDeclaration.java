package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AstDeclaration extends AstBaseMindcodeNode {

    protected AstDeclaration(@NotNull InputPosition inputPosition) {
        super(inputPosition);
    }

    protected AstDeclaration(@NotNull InputPosition inputPosition, @Nullable AstDocComment docComment) {
        super(inputPosition, docComment);
    }
}
