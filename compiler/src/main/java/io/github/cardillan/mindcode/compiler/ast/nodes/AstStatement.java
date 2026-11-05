package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public abstract class AstStatement extends AstBaseMindcodeNode {

    @Override
    public AstNodeScope getScopeRestriction() {
        return AstNodeScope.LOCAL;
    }

    protected AstStatement(SourcePosition sourcePosition) {
        super(sourcePosition);
    }

    protected AstStatement(SourcePosition sourcePosition, List<AstMindcodeNode> children) {
        super(sourcePosition, children);
    }

    protected AstStatement(SourcePosition sourcePosition, List<AstMindcodeNode> children,
            @Nullable AstDocComment docComment) {
        super(sourcePosition, children, docComment);
    }
}
