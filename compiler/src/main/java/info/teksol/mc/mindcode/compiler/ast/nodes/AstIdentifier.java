package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstIdentifier extends AstExpression {
    private final String name;
    private final boolean external;

    public AstIdentifier(InputPosition inputPosition, String name) {
        super(inputPosition);
        this.name = Objects.requireNonNull(name);
        this.external = false;
    }

    public AstIdentifier(InputPosition inputPosition, String name, boolean external) {
        super(inputPosition);
        this.name = Objects.requireNonNull(name);
        this.external = external;
    }

    public boolean isExternal() {
        return external;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstIdentifier that = (AstIdentifier) o;
        return external == that.external && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Boolean.hashCode(external);
        return result;
    }

}