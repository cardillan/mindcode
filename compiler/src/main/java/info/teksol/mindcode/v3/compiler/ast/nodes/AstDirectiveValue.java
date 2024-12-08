package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AstDirectiveValue extends AstFragment {
    private final @NotNull String value;

    public AstDirectiveValue(@NotNull InputPosition inputPosition, @NotNull String value) {
        super(inputPosition);
        this.value = Objects.requireNonNull(value);
    }

    public @NotNull String getValue() {
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