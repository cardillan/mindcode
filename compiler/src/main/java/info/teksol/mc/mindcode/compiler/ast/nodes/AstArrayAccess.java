package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode
public class AstArrayAccess extends AstExpression {
    private final AstIdentifier array;
    private final AstExpression index;

    public AstArrayAccess(InputPosition inputPosition, AstIdentifier array, AstExpression index) {
        super(inputPosition, children(array, index));
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
    public boolean equals(@Nullable Object o) {
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
    public AstContextType getContextType() {
        return AstContextType.HEAP_ACCESS;
    }
}