package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public enum LogicVoid implements LogicValue {
    VOID;

    @Override
    public SourcePosition sourcePosition() {
        return SourcePosition.EMPTY;
    }

    @Override
    public ArgumentType getType() {
        return ArgumentType.NULL_LITERAL;
    }

    @Override
    public ValueMutability getMutability() {
        return ValueMutability.CONSTANT;
    }

    @Override
    public boolean isImmutable() {
        return false;
    }

    @Override
    public String format(@Nullable InstructionProcessor instructionProcessor) {
        throw new MindcodeInternalError("void doesn't have a text representation.");
    }

    @Override
    public boolean isLvalue() {
        return false;
    }

    @Override
    public String toMlog() {
        // TODO After introducing types, VOID materializing in code will be an error
        //throw new MindcodeInternalError("attempt to evaluate void");
        return "null";
    }
}
