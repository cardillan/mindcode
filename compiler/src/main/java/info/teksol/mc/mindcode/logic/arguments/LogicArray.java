package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.ArrayType;
import org.jspecify.annotations.NullMarked;

/// A wrapper around ArrayStore to allow using ArrayStore instances as instruction arguments
@NullMarked
public class LogicArray extends AbstractArgument {
    protected final ArrayStore array;
    protected final String mlog;

    protected LogicArray(ArrayStore array, String range) {
        super(ArgumentType.ARRAY, ValueMutability.MUTABLE);
        this.array = array;
        this.mlog = array.getName() + range;
    }

    public ArrayStore getArrayStore() {
        return array;
    }

    public ArrayType getArrayType() {
        return array.getArrayType();
    }

    @Override
    public String toMlog() {
        return mlog;
    }

    public int size() {
        return array.size();
    }

    public int fullSize() {
        return array.fullSize();
    }

    public boolean isDeclaredRemote() {
        return array.isDeclaredRemote();
    }

    public int getStartOffset() {
        return array.getStartOffset();
    }

    public static LogicArray create(ArrayStore array) {
        return new LogicArray(array, "[]");
    }

    public static LogicArray create(ArrayStore array, int start, int end) {
        return new LogicArray(array, "[" + start + "..." + end + "]");
    }

    public boolean sameArray(LogicArray other) {
        return array.getMasterArray() == other.array.getMasterArray();
    }
}
