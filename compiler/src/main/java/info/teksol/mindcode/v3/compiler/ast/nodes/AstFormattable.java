package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

import java.util.List;
import java.util.Objects;

public class AstFormattable extends AstBaseMindcodeNode {
    protected final List<AstMindcodeNode> parts;

    public AstFormattable(InputPosition inputPosition, List<AstMindcodeNode> parts) {
        super(inputPosition);
        this.parts = List.copyOf(Objects.requireNonNull(parts));
    }

    public List<AstMindcodeNode> getParts() {
        return parts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstFormattable that = (AstFormattable) o;
        return Objects.equals(parts, that.parts);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parts);
    }

    @Override
    public String toString() {
        return "AstFormattable{" +
                "parts=" + parts +
                '}';
    }
}
