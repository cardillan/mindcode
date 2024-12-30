package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;

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
    public boolean isLvalue() {
        return false;
    }

    @Override
    public String toMlog() {
        return "null";
        // Effectively the same as null, but visible in the code
        //return ":void";
    }
}
