package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import info.teksol.mc.util.IntRange;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.TMP_VARIABLE;

@NullMarked
public class BuiltInArray extends AbstractArrayStore {
    private final LogicValue count;
    private final Accessor accessor;

    public static BuiltInArray createLinks(String name) {
        return new BuiltInArray(name, LogicBuiltIn.LINKS, ContextfulInstructionCreator::createGetLink, -1);
    }

    public static BuiltInArray createAllItems(String name) {
        return new BuiltInArray(name, LogicBuiltIn.ITEM_COUNT, lookupAccessor(LogicKeyword.ITEM), -1);
    }

    public static BuiltInArray createItems(String name) {
        return new BuiltInArray(name, LogicNumber.create(16), lookupAccessor(LogicKeyword.ITEM), 16);
    }

    public static BuiltInArray createLiquids(String name) {
        return new BuiltInArray(name, LogicBuiltIn.LIQUID_COUNT, lookupAccessor(LogicKeyword.LIQUID), -1);
    }

    public static BuiltInArray createUnitTypes(String name) {
        return new BuiltInArray(name, LogicBuiltIn.UNIT_COUNT, lookupAccessor(LogicKeyword.UNIT), -1);
    }

    public static BuiltInArray createBlocks(String name) {
        return new BuiltInArray(name, LogicBuiltIn.BLOCK_COUNT, lookupAccessor(LogicKeyword.BLOCK), -1);
    }

    public static BuiltInArray createTeams(String name) {
        return new BuiltInArray(name, LogicBuiltIn.TEAM_COUNT, lookupAccessor(LogicKeyword.TEAM), -1);
    }

    private BuiltInArray(String name, LogicValue count, Accessor accessor, int size) {
        super(SourcePosition.EMPTY, name, ArrayType.LINKS, 0, size, null, false);
        this.count = count;
        this.accessor = accessor;
    }

    private BuiltInArray(SourcePosition sourcePosition, String name, LogicValue count, Accessor accessor, int startOffset, ArrayStore masterArray) {
        super(sourcePosition, name, ArrayType.LINKS, startOffset, -1, masterArray, false);
        this.count = count;
        this.accessor = accessor;
    }

    public LogicValue getCount() {
        return count;
    }

    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public boolean optimizeElementAccess() {
        return false;
    }

    // TODO support fixed-sized built-in arrays
    @Override
    public ArrayStore subarray(SourcePosition sourcePosition, IntRange range) {
        return new BuiltInArray(sourcePosition, name, count, accessor, startOffset + range.min(), masterArray);
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, int index) {
        return new BuiltInArrayElement(sourcePosition, LogicNumber.create(startOffset + index), creator.nextTemp());
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, SourcePosition sourcePosition, ValueStore index, boolean safeAccess) {
        if (startOffset == 0) {
            LogicValue fixedIndex = creator.defensiveCopy(index, TMP_VARIABLE);
            return new BuiltInArrayElement(sourcePosition, fixedIndex, creator.nextTemp());
        } else {
            LogicVariable actualIndex = creator.nextTemp();
            creator.createOp(Operation.ADD, actualIndex, index.getValue(creator), startOffsetNumber);
            return new BuiltInArrayElement(sourcePosition, actualIndex, creator.nextTemp());
        }
    }

    @Override
    public ArrayStore offset(int offset) {
        return new BuiltInArray(sourcePosition, name, count, accessor, startOffset + offset, masterArray);
    }

    private interface Accessor {
        void createAccess(ContextfulInstructionCreator creator, LogicVariable result, LogicValue index);
    }

    private static Accessor lookupAccessor(LogicKeyword type) {
        return (creator, result, index) -> creator.createLookup(type, result, index);
    }

    private class BuiltInArrayElement implements ValueStore {
        private final SourcePosition sourcePosition;
        private final LogicValue index;
        private final LogicVariable transferVariable;

        public BuiltInArrayElement(SourcePosition sourcePosition, LogicValue index, LogicVariable transferVariable) {
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
            accessor.createAccess(creator, transferVariable, index);
            return transferVariable;
        }

        @Override
        public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
            accessor.createAccess(creator, target, index);
        }

        @Override
        public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
            throw new MindcodeInternalError("Writes to the '%s' array are not supported", name);
        }

        @Override
        public SourcePosition sourcePosition() {
            return sourcePosition;
        }

        @Override
        public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
            throw new MindcodeInternalError("Writes to the '%s' array are not supported", name);
        }

        @Override
        public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
            throw new MindcodeInternalError("Writes to the '%s' array are not supported", name);
        }

        @Override
        public void storeValue(ContextfulInstructionCreator creator) {
            throw new MindcodeInternalError("Writes to the '%s' array are not supported", name);
        }
    }
}
