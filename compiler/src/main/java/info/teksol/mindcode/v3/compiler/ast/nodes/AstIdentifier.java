package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AstIdentifier extends AstBaseMindcodeNode {
    private final @NotNull String name;
    private final boolean external;

    public AstIdentifier(InputPosition inputPosition, @NotNull String name) {
        super(inputPosition);
        this.name = Objects.requireNonNull(name);
        this.external = false;
    }

    public AstIdentifier(InputPosition inputPosition, @NotNull String name, boolean external) {
        super(inputPosition);
        this.name = Objects.requireNonNull(name);
        this.external = external;
    }

    public boolean isExternal() {
        return external;
    }

    public @NotNull String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
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

    @Override
    public String toString() {
        return "AstIdentifier{" +
               "names=" + name +
               ", external=" + external +
               '}';
    }
}
