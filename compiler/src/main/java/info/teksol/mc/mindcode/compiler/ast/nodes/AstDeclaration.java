package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.InputPosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public abstract class AstDeclaration extends AstBaseMindcodeNode {

    protected AstDeclaration(InputPosition inputPosition) {
        super(inputPosition);
    }

    protected AstDeclaration(InputPosition inputPosition, List<AstMindcodeNode> children) {
        super(inputPosition, children);
    }

    protected AstDeclaration(InputPosition inputPosition, List<AstMindcodeNode> children,
            @Nullable AstDocComment docComment) {
        super(inputPosition, children, docComment);
    }
}
