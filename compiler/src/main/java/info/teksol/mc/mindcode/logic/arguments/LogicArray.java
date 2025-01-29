package info.teksol.mc.mindcode.logic.arguments;

import org.jspecify.annotations.NullMarked;

///  Represents an internal/external array
@NullMarked
public class LogicArray extends AbstractArgument {
    private final String arrayName;
    protected final String mlog;

    private LogicArray(String arrayName) {
        super(ArgumentType.ARRAY, ValueMutability.MUTABLE);
        this.arrayName = arrayName;
        this.mlog = "." + arrayName + "[]";
    }

    public String getArrayName() {
        return arrayName;
    }

    @Override
    public String toMlog() {
        return mlog;
    }

    public static LogicArray create(String arrayName) {
        return new LogicArray(arrayName);
    }
}
