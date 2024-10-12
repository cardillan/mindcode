package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicLiteral;
import info.teksol.mindcode.logic.LogicNull;

public class NullLiteral extends ConstantAstNode {

    public NullLiteral(InputPosition inputPosition) {
        super(inputPosition);
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
    public NullLiteral withInputPosition(InputPosition inputPosition) {
        return new NullLiteral(inputPosition);
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
