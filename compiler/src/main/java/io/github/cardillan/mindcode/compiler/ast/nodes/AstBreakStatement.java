package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.mindcode.compiler.astcontext.AstContextType;
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
