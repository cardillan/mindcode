package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.InternalArray;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import org.jspecify.annotations.NullMarked;

import java.util.List;

///  Represents an internal/remote array
@NullMarked
public class LogicArray extends AbstractArgument {
    protected final InternalArray array;
    protected final String mlog;

    public final LogicVariable readInd;
    public final LogicVariable readVal;
    public final LogicVariable readRet;
    public final LogicVariable writeInd;
    public final LogicVariable writeVal;
    public final LogicVariable writeRet;

    private LogicArray(InternalArray array, String range) {
        super(ArgumentType.ARRAY, ValueMutability.MUTABLE);
        this.array = array;
        this.mlog = array.getName() + range;

        readInd = LogicVariable.arrayIndex(array.getName(), "*rind");
        readVal = LogicVariable.arrayReadAccess(array.getName());
        readRet = LogicVariable.arrayReturn(array.getName(), "*rret");
        writeInd = LogicVariable.arrayIndex(array.getName(), "*wind");
        writeVal = LogicVariable.arrayWriteAccess(array.getName());
        writeRet = LogicVariable.arrayReturn(array.getName(), "*wret");
    }

    public InternalArray getInternalArray() {
        return array;
    }

    public ArrayStore.ArrayType getArrayType() {
        return array.getArrayType();
    }

    public String getArrayName() {
        return array.getName();
    }

    public List<ValueStore> getElements() {
        return array.getElements();
    }

    @Override
    public String toMlog() {
        return mlog;
    }

    public int getSize() {
        return array.getSize();
    }

    public String getInitJumpTableId() {
        return getArrayName() + "-i";
    }

    public String getReadJumpTableId() {
        return getArrayName() + "-r";
    }

    public String getWriteJumpTableId() {
        return getArrayName() + "-w";
    }

    public static LogicArray create(InternalArray array) {
        return new LogicArray(array, "[]");
    }

    public static LogicArray create(InternalArray array, int start, int end) {
        return new LogicArray(array, "[" + start + "..." + end + "]");
    }
}
