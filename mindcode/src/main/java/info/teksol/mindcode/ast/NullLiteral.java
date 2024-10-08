package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.SourceFile;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicLiteral;
import info.teksol.mindcode.logic.LogicNull;
import org.antlr.v4.runtime.Token;

public class NullLiteral extends ConstantAstNode {

    public NullLiteral(Token startToken, SourceFile sourceFile) {
        super(startToken, sourceFile);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NullLiteral;
    }

    @Override
    public int hashCode() {
        return 65491987;
    }

    @Override
    public LogicLiteral toLogicLiteral(InstructionProcessor instructionProcessor) {
        return LogicNull.NULL;
    }

    @Override
    public NullLiteral withToken(Token startToken) {
        return new NullLiteral(startToken, sourceFile());
    }

    @Override
    public String toString() {
        return "NullLiteral{}";
    }

    @Override
    public double getAsDouble() {
        return 0.0;
    }
}
