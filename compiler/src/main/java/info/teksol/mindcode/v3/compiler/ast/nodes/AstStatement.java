package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AstStatement extends AstBaseMindcodeNode {

    protected AstStatement(InputPosition inputPosition) {
        super(inputPosition);
    }

    protected AstStatement(InputPosition inputPosition, @Nullable AstDocComment docComment) {
        super(inputPosition, docComment);
    }
}
