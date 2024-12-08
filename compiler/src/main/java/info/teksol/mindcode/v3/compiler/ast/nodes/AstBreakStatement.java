package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AstBreakStatement extends AstLabeledStatement {

    public AstBreakStatement(@NotNull InputPosition inputPosition, @Nullable AstIdentifier loopLabel) {
        super(inputPosition, loopLabel);
    }

    @Override
    public String toString() {
        return "AstBreakStatement{" +
               "label=" + label +
               '}';
    }
}
