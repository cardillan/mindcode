package info.teksol.mindcode.ast;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicLiteral;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class FormattableLiteral extends StringLiteral {

    public FormattableLiteral(Token startToken, String text) {
        super(startToken, text);
    }

    @Override
    public LogicLiteral toLogicLiteral(InstructionProcessor instructionProcessor) {
        throw new MindcodeInternalError("No mlog representation of a formattable string literal.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormattableLiteral that = (FormattableLiteral) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public String toString() {
        return "FormattableLiteral{" +
                "text='" + text + '\'' +
                '}';
    }
}
