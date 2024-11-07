package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

public class AstFormattablePlaceholder extends AstBaseMindcodeNode {

    public AstFormattablePlaceholder(InputPosition inputPosition) {
        super(inputPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return 17;
    }

    @Override
    public String toString() {
        return "AstFormattablePlaceholder{}";
    }
}
