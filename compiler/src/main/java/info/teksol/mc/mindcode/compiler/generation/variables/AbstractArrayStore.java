package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.logic.arguments.LogicArray;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/// Represents an array (internal or external). Provides means for accessing array elements statically or dynamically.
@NullMarked
public abstract class AbstractArrayStore extends CompoundValueStore implements ArrayStore {
    /// Name of the array
    protected final String name;

    ///  Type of the array
    protected final ArrayType arrayType;

    /// The starting offset of the array: used with subarrays or external arrays starting at a non-zero index
    protected final int startOffset;

    protected final LogicNumber startOffsetNumber;

    /// Size of the array. May be different from the number of elements if the array is a local array in a
    /// recursive function, in which case the starting element is floating.
    protected final int size;

    /// Contains the master array: source of the subarray
    protected final ArrayStore masterArray;

    protected final LogicArray logicArray;

    public AbstractArrayStore(SourcePosition sourcePosition, String name, ArrayType arrayType, int startOffset, int size,
            @Nullable ArrayStore masterArray, boolean subarray) {
        super(sourcePosition, ERR.ARRAY_FORBIDDEN);
        this.name = name;
        this.arrayType = arrayType;
        this.startOffset = startOffset;
        this.startOffsetNumber = LogicNumber.create(startOffset);
        this.size = size;
        this.masterArray = masterArray == null ? this : masterArray;
        this.logicArray = subarray ? LogicArray.create(this, startOffset, startOffset + size) : LogicArray.create(this);
    }

    @Override
    public ArrayType getArrayType() {
        return arrayType;
    }

    @Override
    public boolean isDeclaredRemote() {
        return false;
    }

    @Override
    public LogicValue getArrayOffset() {
        return LogicNumber.ZERO;
    }

    @Override
    public ArrayStore nonrecursive() {
        return this;
    }

    @Override
    public @Nullable MindcodeFunction getFunction() {
        return null;
    }

    @Override
    public LogicArray getLogicArray() {
        return logicArray;
    }

    public ArrayStore getMasterArray() {
        return masterArray;
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
    public int getStartOffset() {
        return startOffset;
    }

    @Override
    public int size() {
        return size;
    }

    public int fullSize() {
        return size;
    }
}
