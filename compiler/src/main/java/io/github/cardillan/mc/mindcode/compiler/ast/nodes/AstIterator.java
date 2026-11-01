package io.github.cardillan.mc.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode
public class AstIterator extends AstFragment {
    private final AstExpression iterator;
    private final boolean outModifier;

    public AstIterator(SourcePosition sourcePosition, AstExpression iterator, boolean outModifier) {
        super(sourcePosition, children(iterator));
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

        AstIterator that = (AstIterator) o;
        return outModifier == that.outModifier && Objects.equals(iterator, that.iterator);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(iterator);
        result = 31 * result + Boolean.hashCode(outModifier);
        return result;
    }

}
