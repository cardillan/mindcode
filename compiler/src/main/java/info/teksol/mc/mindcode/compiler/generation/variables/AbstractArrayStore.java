package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ERR;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/// Represents an array (internal or external). Provides means for accessing array elements statically or dynamically.
///
/// @param <E> precise type representing the element of the array
@NullMarked
public abstract class AbstractArrayStore extends CompoundValueStore implements ArrayStore {
    protected final String name;
    protected final List<ValueStore> elements;

    public AbstractArrayStore(SourcePosition sourcePosition, String name, List<ValueStore> elements) {
        super(sourcePosition, ERR.ARRAY_FORBIDDEN);
        this.name = name;
        this.elements = elements;
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
        return elements.size();
    }

    @Override
    public List<ValueStore> getElements() {
        return elements;
    }
}
