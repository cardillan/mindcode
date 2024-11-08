package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class AstDirectiveValue extends AstBaseMindcodeNode {
    private final String value;

    public AstDirectiveValue(InputPosition inputPosition, String value) {
        super(inputPosition);
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstDirectiveValue that = (AstDirectiveValue) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "AstDirectiveValue{" +
                "value='" + value + '\'' +
                '}';
    }
}