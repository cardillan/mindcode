package info.teksol.mindcode.logic;

import info.teksol.mindcode.MindcodeException;

import java.util.Objects;
import java.util.Set;

public class LogicBuiltIn extends AbstractArgument implements LogicValue {

    public static final LogicBuiltIn COUNTER = create("counter");
    public static final LogicBuiltIn UNIT = create("unit");

    private final String name;

    private LogicBuiltIn(String name) {
        super(ArgumentType.BUILT_IN);
        this.name = Objects.requireNonNull(name);
        if (name.startsWith("@")) {
            throw new MindcodeException("Unexpected '@' at the beginning of property name " + name);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isConstant() {
        return !isVolatile();
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
        return "@" + name;
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
    private static final Set<String> VOLATILE_NAMES = Set.of("counter", "time", "tick", "second", "minute",
            "waveNumber", "waveTime", "unit", "links");
}
