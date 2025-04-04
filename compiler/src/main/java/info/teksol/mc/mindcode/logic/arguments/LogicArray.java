package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import org.jspecify.annotations.NullMarked;

import java.util.List;

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

    @Override
    public String toMlog() {
        return mlog;
    }

    public List<ValueStore> getElements() {
        return array.getElements();
    }

    public int getSize() {
        return array.getSize();
    }

    public static LogicArray create(ArrayStore array) {
        return new LogicArray(array, "[]");
    }

    public static LogicArray create(ArrayStore array, int start, int end) {
        return new LogicArray(array, "[" + start + "..." + end + "]");
    }
}
