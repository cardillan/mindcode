package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public abstract class AstExpression extends AstBaseMindcodeNode {

    @Override
    public AstNodeScope getScopeRestriction() {
        return AstNodeScope.LOCAL;
    }

    protected AstExpression(SourcePosition sourcePosition) {
        super(sourcePosition);
    }

    protected AstExpression(SourcePosition sourcePosition, List<AstMindcodeNode> children) {
        super(sourcePosition, children);
    }

    protected AstExpression(SourcePosition sourcePosition, List<AstMindcodeNode> children,
            @Nullable AstDocComment docComment) {
        super(sourcePosition, children, docComment);
    }
}
