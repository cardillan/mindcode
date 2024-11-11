package info.teksol.mindcode.logic;

import info.teksol.evaluator.LogicReadable;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.mimex.MindustryContent;
import info.teksol.mindcode.mimex.MindustryContents;

import java.util.Objects;
import java.util.Set;

public class LogicBuiltIn extends AbstractArgument implements LogicValue, LogicReadable {
    public static final LogicBuiltIn COUNTER = create("@counter");
    public static final LogicBuiltIn UNIT = create("@unit");

    private final String name;
    private final MindustryContent object;

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
    public String format() {
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

    // @unit is volatile. It changes through the ubind instruction, but this is not known to the data flow optimizer.
    private static final Set<String> VOLATILE_NAMES = Set.of("@counter", "@time", "@tick", "@second", "@minute",
            "@waveNumber", "@waveTime", "@unit", "@links");
}
