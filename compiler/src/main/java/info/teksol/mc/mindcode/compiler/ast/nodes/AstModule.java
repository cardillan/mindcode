package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@AstNode
@NullMarked
public class AstModule extends AstStatementList {

    public AstModule(InputPosition inputPosition, List<AstMindcodeNode> expressions) {
        super(inputPosition, expressions);
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
