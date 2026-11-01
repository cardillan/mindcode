package io.github.cardillan.mc.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mc.common.SourcePosition;
import io.github.cardillan.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@AstNode(printFlat = true)
public class AstContinueStatement extends AstLabeledStatement {

    public AstContinueStatement(SourcePosition sourcePosition, @Nullable AstIdentifier loopLabel) {
        super(sourcePosition, loopLabel);
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.CONTINUE;
    }
}
