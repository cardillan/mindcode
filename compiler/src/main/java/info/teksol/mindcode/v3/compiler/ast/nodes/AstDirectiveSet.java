package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class AstDirectiveSet extends AstDeclaration {
    private final @NotNull AstDirectiveValue option;
    private final @NotNull List<@NotNull AstDirectiveValue> values;

    public AstDirectiveSet(@NotNull InputPosition inputPosition, @NotNull AstDirectiveValue option,
            @NotNull List<@NotNull AstDirectiveValue> values) {
        super(inputPosition);
        this.option = Objects.requireNonNull(option);
        this.values = Objects.requireNonNull(values);
    }

    public @NotNull AstDirectiveValue getOption() {
        return option;
    }

    public @NotNull List<@NotNull AstDirectiveValue> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstDirectiveSet that = (AstDirectiveSet) o;
        return option.equals(that.option) && values.equals(that.values);
    }

    @Override
    public int hashCode() {
        int result = option.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AstDirectiveSet{" +
                "option=" + option +
                ", values=" + values +
                '}';
    }
}
