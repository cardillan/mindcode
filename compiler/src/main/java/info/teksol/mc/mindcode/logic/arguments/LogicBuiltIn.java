package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.evaluator.LogicReadable;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.mimex.MindustryContents;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

@NullMarked
public class LogicBuiltIn extends AbstractArgument implements LogicValue, LogicReadable {
    // TODO Volatile detection should be moved to InstructionProcessor
    // @unit is volatile. It changes through the ubind instruction, but this is not known to the data flow optimizer.
    private static final Set<String> VOLATILE_NAMES = Set.of("@counter", "@time", "@tick", "@second", "@minute",
            "@waveNumber", "@waveTime", "@unit", "@links");

    public static final LogicBuiltIn COUNTER = create("@counter");
    public static final LogicBuiltIn UNIT = create("@unit");
    public static final LogicBuiltIn WAIT = create("@wait");

    private final SourcePosition sourcePosition;
    private final String name;
    private final @Nullable MindustryContent object;

    private LogicBuiltIn(SourcePosition sourcePosition, String name) {
        super(ArgumentType.BUILT_IN);
        this.sourcePosition = sourcePosition;
        this.name = Objects.requireNonNull(name);
        if (!name.startsWith("@")) {
            throw new MindcodeInternalError(String.format("No '@' at the beginning of property name '%s'", name));
        }
        this.object = MindustryContents.get(name);
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isConstant() {
        // MUSTDO Use just !isVolatile(). Make sure isConstant() is correctly used throughout the code base.
        return canEvaluate();
    }

    @Override
    public boolean canEvaluate() {
        return object != null && !isVolatile();
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

    /**
     * Determines whether the identifier is a volatile variable, i.e. a variable whose value can change
     * independently of the program.
     *
     * @return true if this denotes a volatile variable
     */
    public boolean isVolatile() {
        return VOLATILE_NAMES.contains(name);
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

    public static LogicBuiltIn create(String name) {
        return new LogicBuiltIn(SourcePosition.EMPTY, name);
    }

    public static LogicBuiltIn create(SourcePosition sourcePosition, String name) {
        return new LogicBuiltIn(sourcePosition, name);
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
        // @wait is a special case used with message() world function
        // This solution isn't entirely clean, but will have to do for now
        return false; //"@wait".equals(name);
    }

    @Override
    public void setValue(CodeAssembler assembler, LogicValue value) {
        assembler.createSet(this, value);
    }

    @Override
    public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        LogicValue.super.writeValue(assembler, valueSetter);
    }
}
