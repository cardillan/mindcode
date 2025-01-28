package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import org.jspecify.annotations.NullMarked;

///  Represents an array
@NullMarked
public class LogicArray extends AbstractArgument {

    public LogicArray(ArgumentType argumentType, ValueMutability mutability) {
        super(ArgumentType.ARRAY, ValueMutability.MUTABLE);
    }

    @Override
    public String toMlog() {
        throw new MindcodeInternalError("No mlog representation for an array");
    }
}
