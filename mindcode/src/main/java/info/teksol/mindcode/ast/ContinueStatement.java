package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.instructions.AstContextType;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class ContinueStatement extends ControlBlockAstNode {
    private final String label;

    ContinueStatement(Token startToken, String label) {
        super(startToken);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ContinueStatement statement && Objects.equals(statement.label, label);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(label);
    }

    @Override
    public String toString() {
        return "ContinueStatement{" + (label == null ? "" : "label='" + label + '\'') + '}';
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.CONTINUE;
    }
}
