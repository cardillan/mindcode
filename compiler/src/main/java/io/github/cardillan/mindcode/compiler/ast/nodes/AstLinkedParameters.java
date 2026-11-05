package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstLinkedParameters extends AstFragment {
    private final @Nullable AstBuiltInIdentifier type;

    public AstLinkedParameters(SourcePosition sourcePosition, @Nullable AstBuiltInIdentifier type) {
        super(sourcePosition, children(type));
        this.type = type;
    }

    public @Nullable AstBuiltInIdentifier getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstLinkedParameters that = (AstLinkedParameters) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return type == null ? 0 : type.hashCode();
    }
}
