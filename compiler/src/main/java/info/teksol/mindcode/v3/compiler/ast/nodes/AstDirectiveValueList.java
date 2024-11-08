package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

import java.util.List;
import java.util.Objects;

// Transient class, not part of a final tree
public class AstDirectiveValueList extends AstBaseMindcodeNode {
    private final List<AstDirectiveValue> values;

    public AstDirectiveValueList(InputPosition inputPosition, List<AstDirectiveValue> values) {
        super(inputPosition);
        this.values = Objects.requireNonNull(values);
    }

    public List<AstDirectiveValue> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstDirectiveValueList that = (AstDirectiveValueList) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(values);
    }

    @Override
    public String toString() {
        return "AstDirectiveValueList{" +
                "values=" + values +
                '}';
    }
}
