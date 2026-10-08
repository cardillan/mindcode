package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.TMP_VARIABLE;

@NullMarked
public class LinkArray extends AbstractArrayStore {
    private final LogicNumber startOffsetNumber;
    private final LogicArray logicArray;

    public LinkArray(String name) {
        super(SourcePosition.EMPTY, name, 0, -1, null, List.of());
        this.startOffsetNumber = LogicNumber.create(startOffset);
        logicArray = LogicArray.create(this);
    }

    private LinkArray(SourcePosition sourcePosition, String name, int startOffset, ArrayStore masterArray) {
        super(sourcePosition, name, startOffset, -1, masterArray, List.of());
        this.startOffsetNumber = LogicNumber.create(startOffset);
        logicArray = LogicArray.create(this);
    }

    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public boolean isDeclaredRemote() {
        return false;
    }

    @Override
    public ArrayType getArrayType() {
        return ArrayType.LINKS;
    }

    @Override
    public LogicArray getLogicArray() {
        return logicArray;
    }

    @Override
    public @Nullable MindcodeFunction getFunction() {
        return null;
    }

    @Override
    public LogicValue getArrayOffset() {
        return LogicNumber.ZERO;
    }

    @Override
    public boolean optimizeElementAccess() {
        return false;
    }

    @Override
    public ArrayStore subarray(SourcePosition sourcePosition, int start, int end) {
        return new LinkArray(sourcePosition, name, startOffset + start, this);
    }

    @Override
    public ArrayStore nonrecursive() {
        return this;
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, int index) {
        return new LinkedArrayElement(sourcePosition, LogicNumber.create(startOffset + index), creator.nextTemp());
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, SourcePosition sourcePosition, ValueStore index, boolean safeAccess) {
        if (startOffset == 0) {
            LogicValue fixedIndex = creator.defensiveCopy(index, TMP_VARIABLE);
            return new LinkedArrayElement(sourcePosition, fixedIndex, creator.nextTemp());
        } else {
            LogicVariable actualIndex = creator.nextTemp();
            creator.createOp(Operation.ADD, actualIndex, index.getValue(creator), startOffsetNumber);
            return new LinkedArrayElement(sourcePosition, actualIndex, creator.nextTemp());
        }
    }

    @Override
    public ArrayStore offset(int offset) {
        return new LinkArray(sourcePosition, name, startOffset + offset, getMasterArray());
    }

    private static class LinkedArrayElement implements ValueStore {
        private final SourcePosition sourcePosition;
        private final LogicValue index;
        private final LogicVariable transferVariable;

        public LinkedArrayElement(SourcePosition sourcePosition, LogicValue index, LogicVariable transferVariable) {
            this.sourcePosition = sourcePosition;
            this.index = index;
            this.transferVariable = transferVariable;
        }

        @Override
        public boolean isComplex() {
            return true;
        }

        @Override
        public boolean isLvalue() {
            return false;
        }

        @Override
        public LogicValue getValue(ContextfulInstructionCreator creator) {
            creator.createGetLink(transferVariable, index);
            return transferVariable;
        }

        @Override
        public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
            creator.createGetLink(target, index);
        }

        @Override
        public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
            throw new MindcodeInternalError("Writes to the 'links' array are not supported");
        }

        @Override
        public SourcePosition sourcePosition() {
            return sourcePosition;
        }

        @Override
        public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
            throw new MindcodeInternalError("Writes to the 'links' array are not supported");
        }

        @Override
        public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
            throw new MindcodeInternalError("Writes to the 'links' array are not supported");
        }

        @Override
        public void storeValue(ContextfulInstructionCreator creator) {
            throw new MindcodeInternalError("Writes to the 'links' array are not supported");
        }
    }
}
