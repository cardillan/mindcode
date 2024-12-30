package info.teksol.mc.mindcode.logic.arguments;

import java.util.Objects;

public abstract class AbstractArgument implements LogicArgument {
    protected final ArgumentType argumentType;

    public AbstractArgument(ArgumentType argumentType) {
        this.argumentType = Objects.requireNonNull(argumentType);
    }

    @Override
    public ArgumentType getType() {
        return argumentType;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof LogicArgument a && LogicArgument.isEqual(this, a));
    }

    @Override
    public int hashCode() {
        return toMlog().hashCode();
    }
}
