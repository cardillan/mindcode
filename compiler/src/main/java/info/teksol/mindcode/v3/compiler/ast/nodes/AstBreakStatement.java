package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@AstNode(printFlat = true)
public class AstBreakStatement extends AstLabeledStatement {

    public AstBreakStatement(InputPosition inputPosition, @Nullable AstIdentifier loopLabel) {
        super(inputPosition, loopLabel);
    }

}
