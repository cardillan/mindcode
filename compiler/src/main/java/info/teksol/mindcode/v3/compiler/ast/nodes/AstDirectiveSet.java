package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

import java.util.List;
import java.util.Objects;

public class AstDirectiveSet extends AstBaseMindcodeNode {
    private final AstDirectiveValue option;
    private final List<AstDirectiveValue> values;

    public AstDirectiveSet(InputPosition inputPosition, AstDirectiveValue option, List<AstDirectiveValue> values) {
        super(inputPosition);
        this.option = Objects.requireNonNull(option);
        this.values = Objects.requireNonNull(values);
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
