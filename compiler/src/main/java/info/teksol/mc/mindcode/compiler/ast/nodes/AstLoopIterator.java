package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode
public class AstLoopIterator extends AstFragment {
    private final AstExpression iterator;
    private final boolean outModifier;

    public AstLoopIterator(InputPosition inputPosition, AstExpression iterator, boolean outModifier) {
        super(inputPosition, children(iterator));
        this.iterator = iterator;
        this.outModifier = outModifier;
    }

    public AstExpression getIterator() {
        return iterator;
    }

    public boolean hasOutModifier() {
        return outModifier;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstLoopIterator that = (AstLoopIterator) o;
        return outModifier == that.outModifier && Objects.equals(iterator, that.iterator);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(iterator);
        result = 31 * result + Boolean.hashCode(outModifier);
        return result;
    }

}