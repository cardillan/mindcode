package info.teksol.mindcode.logic;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;

public enum LogicVoid implements LogicValue {
    VOID;

    @Override
    public ArgumentType getType() {
        return ArgumentType.NULL_LITERAL;
    }

    @Override
    public boolean canEvaluate() {
        return false;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public String format(InstructionProcessor instructionProcessor) {
        throw new MindcodeInternalError("void doesn't have a text representation.");
    }

    @Override
    public String toMlog() {
        return "null";
        //throw new MindcodeInternalError("void doesn't have an mlog representation.");
    }
}
