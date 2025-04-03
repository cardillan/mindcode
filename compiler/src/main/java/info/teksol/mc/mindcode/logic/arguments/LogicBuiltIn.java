package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.mimex.MindustryContents;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

@NullMarked
public class LogicBuiltIn extends AbstractArgument implements LogicValue {
    public static final LogicBuiltIn COUNTER = create("@counter", true);
    public static final LogicBuiltIn LINKS = create("@links", true);
    public static final LogicBuiltIn THIS = create("@this", false);
    public static final LogicBuiltIn UNIT = create("@unit", true);
    public static final LogicBuiltIn WAIT = create("@wait", false);

    private final SourcePosition sourcePosition;
    private final String name;
    private final @Nullable MindustryContent object;

    private LogicBuiltIn(SourcePosition sourcePosition, String name, @Nullable Object object, boolean isVolatile) {
        super(ArgumentType.BUILT_IN, computeMutability(object, isVolatile));
        this.sourcePosition = sourcePosition;
        this.name = Objects.requireNonNull(name);
        this.object = MindustryContents.get(name);

        if (!name.startsWith("@")) {
            throw new MindcodeInternalError(String.format("No '@' at the beginning of property name '%s'", name));
        }
    }

    private static ValueMutability computeMutability(@Nullable Object object, boolean isVolatile) {
        return isVolatile ? ValueMutability.VOLATILE : object == null ? ValueMutability.IMMUTABLE : ValueMutability.CONSTANT;
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
    }

    public String getName() {
        return name;
    }

    @Override
    public double getDoubleValue() {
        return 1.0;
    }

    @Override
    public long getLongValue() {
        return 1;
    }

    @Override
    public @Nullable MindustryContent getObject() {
        return object;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public String format(@Nullable InstructionProcessor instructionProcessor) {
        if (object != null) {
            return object.format();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String toMlog() {
        return name;
    }

    @Override
    public String toString() {
        return "LogicBuiltIn{" +
                "name='" + name + '\'' +
                '}';
    }

    public static LogicBuiltIn create(String name, boolean isVolatile) {
        return new LogicBuiltIn(SourcePosition.EMPTY, name, MindustryContents.get(name), isVolatile);
    }

    public static LogicBuiltIn create(InstructionProcessor processor, SourcePosition sourcePosition, String name) {
        return switch (name) {
            case "@counter" -> COUNTER;
            case "@links" -> LINKS;
            case "@this" -> THIS;
            case "@unit" -> UNIT;
            case "@wait" -> WAIT;
            default -> new LogicBuiltIn(sourcePosition, name, MindustryContents.get(name), processor.isVolatileBuiltIn(name));
        };
    }

    // ValueStore methods

    @Override
    public boolean isWritable() {
        // @counter is writable.
        // @wait is a special case used with message() world function
        // This solution isn't entirely clean, but will have to do for now
        return equals(COUNTER) || equals(WAIT);
    }

    @Override
    public boolean isLvalue() {
        return false;
    }

    @Override
    public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
        creator.createSet(this, value);
    }

    @Override
    public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
        LogicValue.super.writeValue(creator, valueSetter);
    }
}
