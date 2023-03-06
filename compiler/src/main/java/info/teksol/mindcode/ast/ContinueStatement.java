package info.teksol.mindcode.ast;

import java.util.Objects;

public class ContinueStatement extends BaseAstNode {
    private final String label;

    ContinueStatement(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ContinueStatement && Objects.equals(((ContinueStatement)obj).label, label);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(label);
    }

    @Override
    public String toString() {
        return "ContinueStatement{" + (label == null ? "" : "label='" + label + '\'') + '}';
    }
}
