package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicBoolean;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class BooleanLiteral extends ConstantAstNode {
    private final boolean value;

    public BooleanLiteral(Token startToken, InputFile inputFile, boolean value) {
        super(startToken, inputFile);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public LogicBoolean toLogicLiteral(InstructionProcessor instructionProcessor) {
        return value ? LogicBoolean.TRUE : LogicBoolean.FALSE;
    }

    @Override
    public BooleanLiteral withToken(Token startToken) {
        return new BooleanLiteral(startToken, sourceFile(), value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanLiteral that = (BooleanLiteral) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "BooleanLiteral{" +
                "value=" + value +
                '}';
    }

    @Override
    public double getAsDouble() {
        return value ? 1.0 : 0.0;
    }
}
