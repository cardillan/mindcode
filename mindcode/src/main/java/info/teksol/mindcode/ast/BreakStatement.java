package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;

import java.util.Objects;

public class BreakStatement extends ControlBlockAstNode {
    private final String label;

    BreakStatement(InputPosition inputPosition, String label) {
        super(inputPosition);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BreakStatement statement && Objects.equals(statement.label, label);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(label);
    }

    @Override
    public String toString() {
        return "BreakStatement{" + (label == null ? "" : "label='" + label + '\'') + '}';
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.BREAK;
    }
}
