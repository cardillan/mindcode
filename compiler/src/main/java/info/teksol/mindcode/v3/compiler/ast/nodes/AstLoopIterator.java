package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;

public class AstLoopIterator extends AstFragment {
    private final @UnknownNullability AstIdentifier iterator;
    private final boolean outModifier;

    public AstLoopIterator(@NotNull InputPosition inputPosition, @UnknownNullability AstIdentifier iterator, boolean outModifier) {
        super(inputPosition);
        this.iterator = iterator;
        this.outModifier = outModifier;
    }

    public @UnknownNullability AstIdentifier getIterator() {
        return iterator;
    }

    public boolean isOutModifier() {
        return outModifier;
    }

    @Override
    public boolean equals(Object o) {
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

    @Override
    public String toString() {
        return "AstLoopIterator{" +
               "iterator=" + iterator +
               ", outModifier=" + outModifier +
               '}';
    }
}
