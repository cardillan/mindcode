package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.logic.arguments.LogicArray;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface ArrayStore extends ValueStore {

    ArrayType getArrayType();

    String getName();

    int getSize();

    default int getFullSize() {
        return getElements().size();
    }

    ArrayStore getMasterArray();

    LogicArray getLogicArray();

    boolean valid();

    /// True when declared remote, even if the actual array type is INTERNAL
    boolean isDeclaredRemote();

    @Nullable MindcodeFunction getFunction();

    /// An array offset. Equal to LogicNumber.ZERO except for arrays with floating array offset.
    LogicValue getArrayOffset();

    default boolean hasArrayOffset() {
        return getArrayOffset() != LogicNumber.ZERO;
    }

    List<ValueStore> getElements();

    ArrayStore subarray(SourcePosition sourcePosition, int start, int end);

    ArrayStore offset(int offset);

    ArrayStore nonrecursive();

    ValueStore getElement(ContextfulInstructionCreator creator, int index);

    ValueStore getElement(ContextfulInstructionCreator creator, SourcePosition sourcePosition, ValueStore index);

    /// Returns true if this array store can benefit from replacing a random-element-access with direct access
    /// External arrays do not benefit, but internal and remote ones do, except arrays with floating array offset
    boolean optimizeElementAccess();

    default boolean isRemote() {
        return getArrayType() == ArrayType.REMOTE || getArrayType() == ArrayType.REMOTE_SHARED;
    }

    default LogicVariable getProcessor() {
        throw new UnsupportedOperationException("Not supported for class " + getClass().getSimpleName());
    }

    /// Provides a start offset against the backing store. For internal arrays, it is always 0. For internal subarrays,
    /// it is the offset inside the backing array. For external arrays, it is always the offset inside the external
    /// memory block.
    int getStartOffset();

    enum ArrayType {
        /// Residing in the current processor.
        /// Even arrays declared `remote` are internal within the current module
        INTERNAL,

        /// Constant array elements, no variables involved
        CONSTANT,

        /// Residing in a memory cell or memory bank
        EXTERNAL,

        /// Residing in a remote processor
        REMOTE,

        /// Residing in remote processors, multiplexed
        REMOTE_SHARED,
    }
}
