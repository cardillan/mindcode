package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public abstract class AstLabeledExpression extends AstExpression {
    protected final @Nullable AstIdentifier label;

    protected AstLabeledExpression(SourcePosition sourcePosition, @Nullable AstIdentifier label) {
        super(sourcePosition, children(label));
        this.label = label;
    }

    protected AstLabeledExpression(SourcePosition sourcePosition, @Nullable AstIdentifier label, List<AstMindcodeNode> otherChildren) {
        super(sourcePosition,
                label == null ? otherChildren : children(list(label), otherChildren));
        this.label = label;
    }

    public @Nullable AstIdentifier getLabel() {
        return label;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstLabeledExpression that = (AstLabeledExpression) o;
        return Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(label);
    }
}
