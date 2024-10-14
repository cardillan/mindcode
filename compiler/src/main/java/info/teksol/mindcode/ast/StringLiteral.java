package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicLiteral;
import info.teksol.mindcode.logic.LogicString;

import java.util.Objects;

public class StringLiteral extends ConstantAstNode {
    protected final String text;

    public StringLiteral(InputPosition inputPosition, String text) {
        super(inputPosition);
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
    public StringLiteral withInputPosition(InputPosition inputPosition) {
        return new StringLiteral(inputPosition, text);
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
