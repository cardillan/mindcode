package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode
public class AstEnhancedComment extends AstStatement {
    protected final List< AstExpression> parts;

    public AstEnhancedComment(InputPosition inputPosition, List< AstExpression> parts) {
        super(inputPosition);
        this.parts = List.copyOf(Objects.requireNonNull(parts));
    }

    public List< AstExpression> getParts() {
        return parts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstEnhancedComment that = (AstEnhancedComment) o;
        return Objects.equals(parts, that.parts);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parts);
    }

    @Override
    public String toString() {
        return "AstEnhancedComment{" +
               "parts=" + parts +
               '}';
    }
}
