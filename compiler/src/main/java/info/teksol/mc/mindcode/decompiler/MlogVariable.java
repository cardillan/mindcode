package info.teksol.mc.mindcode.decompiler;

import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public class MlogVariable implements MlogExpression, LogicArgument {
    private final String name;

    public MlogVariable(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public void replaceVariable(MlogVariable variable, MlogExpression expression) {
        // Do nothing
    }

    @Override
    public String toMlog() {
        return name;
    }

    @Override
    public void gatherInputVariables(List<MlogVariable> variables) {
        variables.add(this);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MlogVariable that = (MlogVariable) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
