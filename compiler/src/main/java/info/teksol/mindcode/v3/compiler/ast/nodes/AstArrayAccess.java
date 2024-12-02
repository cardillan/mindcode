package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AstArrayAccess extends AstBaseMindcodeNode {
    private final @NotNull AstIdentifier array;
    private final @NotNull AstMindcodeNode index;

    public AstArrayAccess(InputPosition inputPosition, @NotNull AstIdentifier array, @NotNull AstMindcodeNode index) {
        super(inputPosition);
        this.array = Objects.requireNonNull(array);
        this.index = Objects.requireNonNull(index);
    }

    public @NotNull AstIdentifier getArray() {
        return array;
    }

    public @NotNull AstMindcodeNode getIndex() {
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