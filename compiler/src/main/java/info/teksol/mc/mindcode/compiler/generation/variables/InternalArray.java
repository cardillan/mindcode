package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.stream.IntStream;

@NullMarked
public class InternalArray extends ArrayStore {

    private InternalArray(AstIdentifier arrayIdentifier, List<ValueStore> elements) {
        super(arrayIdentifier, elements);
    }

    public static InternalArray create(AstIdentifier identifier, int size) {
        return new InternalArray(identifier, IntStream.range(0, size)
                .mapToObj(index -> (ValueStore) LogicVariable.arrayElement(identifier, index)).toList());
    }

    public static InternalArray createInvalid(AstIdentifier identifier, int size) {
        return new InternalArray(identifier, IntStream.of(size)
                .mapToObj(index -> (ValueStore) LogicVariable.INVALID).toList());
    }
}
