package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicLiteral;

import java.util.Objects;

public class FormattableLiteral extends StringLiteral {

    public FormattableLiteral(InputPosition inputPosition, String text) {
        super(inputPosition, text);
    }

    @Override
    public LogicLiteral toLogicLiteral(InstructionProcessor instructionProcessor) {
        throw new MindcodeInternalError("No mlog representation of a formattable string literal.");
    }

    @Override
    public String getLiteralValue() {
        return "$\"" + text + '"';
    }

    @Override
    public FormattableLiteral withInputPosition(InputPosition inputPosition) {
        return new FormattableLiteral(inputPosition, text);
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
