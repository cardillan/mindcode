package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class ExternalArray extends ArrayStore {

    private ExternalArray(AstIdentifier arrayIdentifier, List<ValueStore> elements) {
        super(arrayIdentifier, elements);
    }

    public static ArrayStore create(HeapTracker heapTracker, AstIdentifier identifier, int size) {
        return new ExternalArray(identifier, heapTracker.createArray(identifier, size));
    }
}
