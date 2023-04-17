package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.instructions.InstructionProcessor;

import java.util.Objects;

public class StringLiteral extends ConstantAstNode {
    private final String text;

    public StringLiteral(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getLiteral(InstructionProcessor instructionProcessor) {
        return "\"" + getText().replaceAll("\"", "'") + "\"";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringLiteral that = (StringLiteral) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return "StringLiteral{" +
                "text='" + text + '\'' +
                '}';
    }

    @Override
    public double getAsDouble() {
        return 1.0;
    }
}
