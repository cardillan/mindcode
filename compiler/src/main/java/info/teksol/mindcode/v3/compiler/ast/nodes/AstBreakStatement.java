package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode
public class AstBreakStatement extends AstLabeledStatement {

    public AstBreakStatement(InputPosition inputPosition, @Nullable AstIdentifier loopLabel) {
        super(inputPosition, loopLabel);
    }

    @Override
    public String toString() {
        return "AstBreakStatement{" +
               "label=" + label +
               '}';
    }
}
