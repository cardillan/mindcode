package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.SourceFile;
import info.teksol.mindcode.compiler.generator.AstContextType;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class BreakStatement extends ControlBlockAstNode {
    private final String label;

    BreakStatement(Token startToken, SourceFile sourceFile, String label) {
        super(startToken, sourceFile);
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
