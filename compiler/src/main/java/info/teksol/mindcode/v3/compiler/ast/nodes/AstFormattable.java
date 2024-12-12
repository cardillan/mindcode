package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode
public class AstFormattable extends AstExpression {
    protected final List< AstExpression> parts;

    public AstFormattable(InputPosition inputPosition, List< AstExpression> parts) {
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

        AstFormattable that = (AstFormattable) o;
        return Objects.equals(parts, that.parts);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parts);
    }

    @Override
    public String toString() {
        return "AstFormattable{" +
                "parts=" + parts +
                '}';
    }
}
