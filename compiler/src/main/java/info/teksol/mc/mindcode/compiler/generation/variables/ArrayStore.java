package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstExpression;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;

import java.util.List;

public interface ArrayStore extends ValueStore {

    ArrayType getArrayType();

    String getName();

    int getSize();

    List<ValueStore> getElements();

    ArrayStore subarray(SourcePosition sourcePosition, int start, int end);

    ValueStore getElement(CodeAssembler assembler, AstExpression node, ValueStore index);

    default ArrayStore topArray() {
        return this;
    }

    /// Provides a start offset against the backing store. For internal arrays, it is always 0. For internal subarrays,
    /// it is the offset inside the top array. For external arrays, it is always the offset inside the external
    /// memory block.
    int getStartOffset();

    enum ArrayType {
        INTERNAL,
        EXTERNAL,
        REMOTE,
    }
}
