package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.mindcode.compiler.generation.variables.InternalArray;
import org.jspecify.annotations.NullMarked;

import java.util.List;

///  Represents an internal/external array
@NullMarked
public class LogicArray extends AbstractArgument {
    protected final InternalArray array;
    protected final String mlog;

    public final LogicVariable readVal;
    public final LogicVariable readRet;
    public final LogicVariable writeVal;
    public final LogicVariable writeRet;

    private LogicArray(InternalArray array) {
        super(ArgumentType.ARRAY, ValueMutability.MUTABLE);
        this.array = array;
        this.mlog = "." + array.getName() + "[]";

        readVal = LogicVariable.arrayAccess(array.getName(), "*r");
        readRet = LogicVariable.arrayAccess(array.getName(), "*rret");
        writeVal = LogicVariable.arrayAccess(array.getName(), "*w");
        writeRet = LogicVariable.arrayAccess(array.getName(), "*wret");
    }

    public String getArrayName() {
        return array.getName();
    }

    public List<LogicVariable> getElements() {
        return array.getElements();
    }

    @Override
    public String toMlog() {
        return mlog;
    }

    public int getSize() {
        return array.getSize();
    }

    public String getReadJumpTableId() {
        return getArrayName() + "r";
    }

    public String getWriteJumpTableId() {
        return getArrayName() + "w";
    }

    public static LogicArray create(InternalArray array) {
        return new LogicArray(array);
    }
}
