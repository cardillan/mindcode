package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AstLabeledStatement extends AstStatement {
    protected final @Nullable AstIdentifier label;

    protected AstLabeledStatement(@NotNull InputPosition inputPosition, @Nullable AstIdentifier label) {
        super(inputPosition);
        this.label = label;
    }

    public @Nullable AstIdentifier getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstLabeledStatement that = (AstLabeledStatement) o;
        return Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(label);
    }
}
