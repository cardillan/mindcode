package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class AstIdentifier extends AstBaseMindcodeNode {
    private final String name;

    public AstIdentifier(InputPosition inputPosition, String name) {
        super(inputPosition);
        this.name = Objects.requireNonNull(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstIdentifier that = (AstIdentifier) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "AstIdentifier{" +
                "name='" + name + '\'' +
                '}';
    }
}