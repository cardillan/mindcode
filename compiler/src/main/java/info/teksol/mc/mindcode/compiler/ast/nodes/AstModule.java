package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@AstNode
@NullMarked
public class AstModule extends AstStatementList {

    public AstModule(SourcePosition sourcePosition, List<AstMindcodeNode> expressions) {
        super(sourcePosition, expressions);
    }

    @Override
    public AstNodeScope getScope() {
        return AstNodeScope.GLOBAL;
    }

    @Override
    public AstNodeScope getScopeRestriction() {
        return AstNodeScope.GLOBAL;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.ROOT;
    }

    @Override
    public AstSubcontextType getSubcontextType() {
        return AstSubcontextType.BODY;
    }
}
