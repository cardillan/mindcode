package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ERR;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

/// Represents an array (internal or external). Provides means for accessing array elements statically or dynamically.
@NullMarked
public abstract class AbstractArrayStore extends CompoundValueStore implements ArrayStore {
    /// Name of the array
    protected final String name;

    /// Size of the array. May be different from the number of elements if the array is a local array in a
    /// recursive function, in which case the starting element is floating.
    protected final int size;

    /// Contains the master array
    protected final @Nullable ArrayStore masterArray;

    /// Actual list of elements
    protected final List<ValueStore> elements;

    public AbstractArrayStore(SourcePosition sourcePosition, String name, int size, @Nullable ArrayStore masterArray, List<ValueStore> elements) {
        super(sourcePosition, ERR.ARRAY_FORBIDDEN);
        this.name = name;
        this.size = size;
        this.elements = elements;
        this.masterArray = masterArray;
    }

    public ArrayStore getMasterArray() {
        return masterArray == null ? this : masterArray;
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public List<ValueStore> getElements() {
        return elements;
    }
}
