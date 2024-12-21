package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@AstNode(printFlat = true)
public class AstContinueStatement extends AstLabeledStatement {

    public AstContinueStatement(InputPosition inputPosition, @Nullable AstIdentifier loopLabel) {
        super(inputPosition, loopLabel);
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.CONTINUE;
    }
}
