package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@NullMarked
public class LogicBuiltIn extends AbstractArgument implements LogicValue {
    private static final Map<String, LogicBuiltIn> CACHE = new HashMap<>();

    public static final LogicBuiltIn COUNTER = createAndCache("@counter", true);
    public static final LogicBuiltIn LINKS = createAndCache("@links", true);
    public static final LogicBuiltIn UNIT = createAndCache("@unit", true);
    public static final LogicBuiltIn THIS = createAndCache("@this", false);
    public static final LogicBuiltIn WAIT = createAndCache("@wait", false);
    public static final LogicBuiltIn ID = createAndCache("@id", false);
    public static final LogicBuiltIn X = createAndCache("@x", false);
    public static final LogicBuiltIn Y = createAndCache("@y", false);
    public static final LogicBuiltIn THIS_X = createAndCache("@thisx", false);
    public static final LogicBuiltIn THIS_Y = createAndCache("@thisy", false);
    public static final LogicBuiltIn TYPE = createAndCache("@type", false);
    public static final LogicBuiltIn SIZE = createAndCache("@size", false);

    private final SourcePosition sourcePosition;
    private final String name;
    private final @Nullable MindustryContent object;

    private LogicBuiltIn(SourcePosition sourcePosition, String name, @Nullable MindustryContent object, boolean isVolatile) {
        super(ArgumentType.BUILT_IN, computeMutability(object, isVolatile));
        this.sourcePosition = sourcePosition;
        this.name = Objects.requireNonNull(name);
        this.object = object;

        if (!name.startsWith("@")) {
            throw new MindcodeInternalError(String.format("No '@' at the beginning of logic built-in '%s'", name));
        }
    }

    protected LogicBuiltIn(SourcePosition sourcePosition, String name) {
        super(ArgumentType.BUILT_IN, ValueMutability.CONSTANT);
        this.sourcePosition = sourcePosition;
        this.name = name;
        this.object = null;
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

    public static LogicBuiltIn create(MindustryContent object, boolean isVolatile) {
        return new LogicBuiltIn(SourcePosition.EMPTY, object.name(), object, isVolatile);
    }

    // Only for built-ins created by test code
    public static LogicBuiltIn createAndCache(String name, boolean isVolatile) {
        LogicBuiltIn builtIn = new LogicBuiltIn(SourcePosition.EMPTY, name, null, isVolatile);
        CACHE.put(name, builtIn);
        return builtIn;
    }

    public static LogicBuiltIn create(InstructionProcessor processor, SourcePosition sourcePosition, String name) {
        LogicBuiltIn cached = CACHE.get(name);
        return cached != null ? cached : new LogicBuiltIn(sourcePosition, name, processor.getMetadata().getNamedContent(name),
                    processor.isVolatileBuiltIn(name));
    }

    // Uses latest metadata version
    public static LogicBuiltIn createForUnitTests(String name, boolean isVolatile) {
        return new LogicBuiltIn(SourcePosition.EMPTY, name, MindustryMetadata.getLatest().getNamedContent(name), isVolatile);
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
