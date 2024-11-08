package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class AstBuiltInIdentifier extends AstBaseMindcodeNode {
    private final String name;

    public AstBuiltInIdentifier(InputPosition inputPosition, String name) {
        super(inputPosition);
        this.name = Objects.requireNonNull(name);
        if (name.charAt(0) != '@') {
            throw new IllegalArgumentException("Built-in name must start with @");
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstBuiltInIdentifier that = (AstBuiltInIdentifier) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "AstBuiltInIdentifier{" +
                "name='" + name + '\'' +
                '}';
    }
}