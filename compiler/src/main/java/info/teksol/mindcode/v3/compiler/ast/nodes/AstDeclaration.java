package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AstDeclaration extends AstBaseMindcodeNode {

    protected AstDeclaration(InputPosition inputPosition) {
        super(inputPosition);
    }

    protected AstDeclaration(InputPosition inputPosition, @Nullable AstDocComment docComment) {
        super(inputPosition, docComment);
    }
}
