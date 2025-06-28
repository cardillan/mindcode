package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstExpression;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface ArrayStore extends ValueStore {

    ArrayType getArrayType();

    String getName();

    int getSize();

    List<ValueStore> getElements();

    ArrayStore subarray(SourcePosition sourcePosition, int start, int end);

    ValueStore getElement(ContextfulInstructionCreator creator, AstExpression node, ValueStore index);

    /// Returns true if this array store benefits from replacing a random element access with direct access
    /// External arrays do not benefit, but internal and remote ones do
    default boolean optimizeElementAccess() {
        return getArrayType() != ArrayType.EXTERNAL;
    }

    default ArrayStore topArray() {
        return this;
    }

    /// Provides a start offset against the backing store. For internal arrays, it is always 0. For internal subarrays,
    /// it is the offset inside the backing array. For external arrays, it is always the offset inside the external
    /// memory block.
    int getStartOffset();

    enum ArrayType {
        INTERNAL,
        EXTERNAL,
        REMOTE,
        REMOTE_SHARED,
    }
}
