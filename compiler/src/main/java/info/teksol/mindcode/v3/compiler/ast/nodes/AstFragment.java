package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public abstract class AstFragment extends AstBaseMindcodeNode {

    protected AstFragment(InputPosition inputPosition) {
        super(inputPosition);
    }

    protected AstFragment(InputPosition inputPosition, List<AstMindcodeNode> children) {
        super(inputPosition, children);
    }

    protected AstFragment(InputPosition inputPosition, List<AstMindcodeNode> children,
            @Nullable AstDocComment docComment) {
        super(inputPosition, children, docComment);
    }
}
