package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicLiteral;

public class VoidLiteral extends ConstantAstNode {

    public VoidLiteral(InputPosition inputPosition) {
        super(inputPosition);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VoidLiteral;
    }

    @Override
    public int hashCode() {
        return 72841685;
    }

    @Override
    public LogicLiteral toLogicLiteral(InstructionProcessor instructionProcessor) {
        throw new MindcodeInternalError("No literal representation of void");
    }

    @Override
    public VoidLiteral withInputPosition(InputPosition inputPosition) {
        return new VoidLiteral(inputPosition);
    }

    @Override
    public String toString() {
        return "VoidLiteral{}";
    }

    @Override
    public double getAsDouble() {
        throw new MindcodeInternalError("No numerical value of void");
    }
}
