package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class StackTracker {
    private LogicVariable stackMemory = LogicVariable.INVALID;

    public boolean isValid() {
        return stackMemory != LogicVariable.INVALID;
    }

    public void setStackMemory(LogicVariable stackMemory) {
        this.stackMemory = Objects.requireNonNull(stackMemory);
    }

    public LogicVariable getStackMemory() {
        return stackMemory;
    }
}
