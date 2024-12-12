package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AstFragment extends AstBaseMindcodeNode {

    protected AstFragment(InputPosition inputPosition) {
        super(inputPosition);
    }

    protected AstFragment(InputPosition inputPosition, @Nullable AstDocComment docComment) {
        super(inputPosition, docComment);
    }
}
