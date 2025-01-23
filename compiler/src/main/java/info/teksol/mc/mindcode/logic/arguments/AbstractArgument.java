package info.teksol.mc.mindcode.logic.arguments;

import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public abstract class AbstractArgument implements LogicArgument {
    protected final ArgumentType argumentType;
    protected final ValueMutability mutability;

    public AbstractArgument(ArgumentType argumentType, ValueMutability mutability) {
        this.argumentType = Objects.requireNonNull(argumentType);
        this.mutability = Objects.requireNonNull(mutability);
    }

    @Override
    public ArgumentType getType() {
        return argumentType;
    }

    @Override
    public ValueMutability getMutability() {
        return mutability;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof LogicArgument other && LogicArgument.isEqual(this, other));
    }

    @Override
    public int hashCode() {
        return toMlog().hashCode();
    }
}
