package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
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
