package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AstContinueStatement extends AstLabeledStatement {

    public AstContinueStatement(@NotNull InputPosition inputPosition, @Nullable AstIdentifier loopLabel) {
        super(inputPosition, loopLabel);
    }

    @Override
    public String toString() {
        return "AstContinueStatement{" +
               "label=" + label +
               '}';
    }
}
