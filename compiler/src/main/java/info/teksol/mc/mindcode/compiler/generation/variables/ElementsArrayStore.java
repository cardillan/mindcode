package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

/// Array implementation with a pre-created list of elements.
@NullMarked
public abstract class ElementsArrayStore extends AbstractArrayStore {
    /// Actual list of elements
    protected final List<ValueStore> elements;

    public ElementsArrayStore(SourcePosition sourcePosition, String name, ArrayType arrayType, int startOffset, int size,
            @Nullable ElementsArrayStore masterArray, List<ValueStore> elements, boolean subarray) {
        super(sourcePosition, name, arrayType, startOffset, size, masterArray, subarray);
        this.elements = elements;
    }

    public ElementsArrayStore getMasterArray() {
        return masterArray == null ? this : (ElementsArrayStore) masterArray;
    }

    public List<ValueStore> getElements() {
        return elements;
    }

    public int fullSize() {
        return getElements().size();
    }
}
