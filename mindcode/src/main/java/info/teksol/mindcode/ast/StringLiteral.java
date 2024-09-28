package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.SourceFile;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicLiteral;
import info.teksol.mindcode.logic.LogicString;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class StringLiteral extends ConstantAstNode {
    protected final String text;

    public StringLiteral(Token startToken, SourceFile sourceFile, String text) {
        super(startToken, sourceFile);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public LogicLiteral toLogicLiteral(InstructionProcessor instructionProcessor) {
        return LogicString.create(getText());
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
