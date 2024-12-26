package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.MessageConsumer;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class StackTracker extends AbstractMessageEmitter {
    private LogicVariable stackMemory = LogicVariable.INVALID;

    public StackTracker(MessageConsumer messageConsumer) {
        super(messageConsumer);
    }

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
