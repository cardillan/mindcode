package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public abstract class AstDeclaration extends AstBaseMindcodeNode {

    @Override
    public AstNodeScope getScopeRestriction() {
        return AstNodeScope.GLOBAL;
    }

    @Override
    public boolean reportAllScopeErrors() {
        return true;
    }

    protected AstDeclaration(SourcePosition sourcePosition) {
        super(sourcePosition);
    }

    protected AstDeclaration(SourcePosition sourcePosition, List<AstMindcodeNode> children) {
        super(sourcePosition, children);
    }

    protected AstDeclaration(SourcePosition sourcePosition, List<AstMindcodeNode> children,
            @Nullable AstDocComment docComment) {
        super(sourcePosition, children, docComment);
    }
}
