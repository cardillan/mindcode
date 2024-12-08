package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AstLoopStatement extends AstBaseMindcodeNode {
    protected final @Nullable AstIdentifier loopLabel;

    public AstLoopStatement(@NotNull InputPosition inputPosition, @Nullable AstIdentifier loopLabel) {
        super(inputPosition);
        this.loopLabel = loopLabel;
    }

    public @Nullable AstIdentifier getLoopLabel() {
        return loopLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstLoopStatement that = (AstLoopStatement) o;
        return Objects.equals(loopLabel, that.loopLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(loopLabel);
    }
}
