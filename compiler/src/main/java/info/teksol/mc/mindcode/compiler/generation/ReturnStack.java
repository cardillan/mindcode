package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/// Class maintaining the stack of active functions and their return variables. Used to resolve return
/// statements inside inline functions, which can be nested.
@NullMarked
public class ReturnStack {
    private final Deque<ReturnRecord> stack = new ArrayDeque<>();

    public void enterFunction(LogicLabel returnLabel, LogicValue returnValue) {
        stack.push(new ReturnRecord(returnLabel, returnValue));
    }

    public void exitFunction() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("exitFunction on empty stack");
        }

        stack.pop();
    }

    public @Nullable ReturnRecord getReturnRecord() {
        return stack.peek();
    }

    public LogicLabel getReturnLabel() {
        return stack.isEmpty() ? LogicLabel.INVALID : Objects.requireNonNull(stack.peek()).label();
    }

    // TODO returnValue will be ValueStore
    public record ReturnRecord(LogicLabel label, LogicValue value) {
        public ReturnRecord {
            Objects.requireNonNull(label);
            Objects.requireNonNull(value);
        }
    }
}
