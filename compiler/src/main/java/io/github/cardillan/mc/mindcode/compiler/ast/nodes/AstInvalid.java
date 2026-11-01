package io.github.cardillan.mc.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode
public class AstInvalid extends AstBaseMindcodeNode {

    public AstInvalid(SourcePosition sourcePosition) {
        super(sourcePosition);
    }

    @Override
    public AstNodeScope getScope() {
        return AstNodeScope.NONE;
    }

    @Override
    public AstNodeScope getScopeRestriction() {
        return AstNodeScope.NONE;
    }
}
