package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstFormattableLiteral extends AstExpression {
    protected final List<AstExpression> parts;

    public AstFormattableLiteral(InputPosition inputPosition, List<AstExpression> parts) {
        super(inputPosition, children(parts));
        this.parts = List.copyOf(Objects.requireNonNull(parts));
    }

    public List<AstExpression> getParts() {
        return parts;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstFormattableLiteral that = (AstFormattableLiteral) o;
        return Objects.equals(parts, that.parts);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parts);
    }

}