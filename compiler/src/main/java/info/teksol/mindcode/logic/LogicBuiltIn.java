package info.teksol.mindcode.logic;

import info.teksol.evaluator.LogicReadable;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.mimex.MindustryContent;
import info.teksol.mindcode.mimex.MindustryContents;
import info.teksol.mindcode.v3.compiler.generation.CodeAssembler;
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

    private final String name;
    private final @Nullable MindustryContent object;

    private LogicBuiltIn(String name) {
        super(ArgumentType.BUILT_IN);
        this.name = Objects.requireNonNull(name);
        if (!name.startsWith("@")) {
            throw new MindcodeInternalError(String.format("No '@' at the beginning of property name '%s'", name));
        }
        this.object = MindustryContents.get(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isConstant() {
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
    public MindustryContent getObject() {
        return object;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public String format(InstructionProcessor instructionProcessor) {
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
        return new LogicBuiltIn(name);
    }

    // ValueStore methods

    @Override
    public boolean isWritable() {
        // @counter is writable. Some other built-ins might be as well.
        return true;
    }

    @Override
    public boolean isLvalue() {
        // @counter is writable. Some other built-ins might be as well.
        return true;
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
