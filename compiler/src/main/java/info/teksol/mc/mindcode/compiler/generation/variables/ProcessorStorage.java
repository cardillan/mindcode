package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public record ProcessorStorage(@Nullable LogicVariable processor, @Nullable String name) {

    public String getName() {
        return Objects.requireNonNull(name);
    }

    public LogicVariable getProcessor() {
        return Objects.requireNonNull(processor);
    }
}
