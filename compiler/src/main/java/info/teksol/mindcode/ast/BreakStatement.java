package info.teksol.mindcode.ast;

import java.util.Objects;

public class BreakStatement implements AstNode {
    private final String label;

    public BreakStatement(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BreakStatement && Objects.equals(((BreakStatement)obj).label, label);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(label);
    }

    @Override
    public String toString() {
        return "Break{" + (label == null ? "" : "label='" + label) + "'}";
    }
}
