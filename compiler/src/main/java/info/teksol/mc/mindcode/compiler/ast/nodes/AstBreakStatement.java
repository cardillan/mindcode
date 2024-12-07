package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@AstNode(printFlat = true)
public class AstBreakStatement extends AstLabeledStatement {

    public AstBreakStatement(SourcePosition sourcePosition, @Nullable AstIdentifier loopLabel) {
        super(sourcePosition, loopLabel);
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.BREAK;
    }
}
