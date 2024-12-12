package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class AstArrayAccess extends AstExpression {
    private final AstIdentifier array;
    private final AstExpression index;

    public AstArrayAccess(InputPosition inputPosition, AstIdentifier array, AstExpression index) {
        super(inputPosition);
        this.array = Objects.requireNonNull(array);
        this.index = Objects.requireNonNull(index);
    }

    public AstIdentifier getArray() {
        return array;
    }

    public AstExpression getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstArrayAccess that = (AstArrayAccess) o;
        return array.equals(that.array) && index.equals(that.index);
    }

    @Override
    public int hashCode() {
        int result = array.hashCode();
        result = 31 * result + index.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AstArrayAccess{" +
               ", array=" + array +
               ", index=" + index +
               '}';
    }
}