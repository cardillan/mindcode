package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstBuiltInIdentifier extends AstExpression {
    private final String name;

    public AstBuiltInIdentifier(SourcePosition sourcePosition, String name) {
        super(sourcePosition);
        this.name = Objects.requireNonNull(name);
        if (name.charAt(0) != '@') {
            throw new IllegalArgumentException("Built-in name must start with @");
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstBuiltInIdentifier that = (AstBuiltInIdentifier) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}