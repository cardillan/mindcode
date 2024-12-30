package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.InputPosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public abstract class AstLabeledStatement extends AstStatement {
    protected final @Nullable AstIdentifier label;

    protected AstLabeledStatement(InputPosition inputPosition, @Nullable AstIdentifier label) {
        super(inputPosition, children(label));
        this.label = label;
    }

    protected AstLabeledStatement(InputPosition inputPosition, @Nullable AstIdentifier label, List<AstMindcodeNode> otherChildren) {
        super(inputPosition,
                label == null ? otherChildren : children(list(label), otherChildren));
        this.label = label;
    }

    public @Nullable AstIdentifier getLabel() {
        return label;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstLabeledStatement that = (AstLabeledStatement) o;
        return Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(label);
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.LOOP;
    }
}
