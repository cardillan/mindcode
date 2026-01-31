package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
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
