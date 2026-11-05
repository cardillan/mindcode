package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public abstract class AstFragment extends AstBaseMindcodeNode {

    @Override
    public AstNodeScope getScopeRestriction() {
        return AstNodeScope.NONE;
    }

    protected AstFragment(SourcePosition sourcePosition) {
        super(sourcePosition);
    }

    protected AstFragment(SourcePosition sourcePosition, List<AstMindcodeNode> children) {
        super(sourcePosition, children);
    }

    protected AstFragment(SourcePosition sourcePosition, List<AstMindcodeNode> children,
            @Nullable AstDocComment docComment) {
        super(sourcePosition, children, docComment);
    }
}
