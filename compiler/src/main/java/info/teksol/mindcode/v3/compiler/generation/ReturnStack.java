package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.MessageConsumer;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/// Class maintaining the stack of active functions and their return variables. Used to resolve return
/// statements inside inline functions, which can be nested.
@NullMarked
public class ReturnStack extends AbstractMessageEmitter {
    private final Deque<Return> stack = new ArrayDeque<>();

    public ReturnStack(MessageConsumer messageConsumer) {
        super(messageConsumer);
    }

    public void enterFunction(LogicLabel returnLabel, LogicValue returnValue) {
        stack.push(new Return(returnLabel, returnValue));
    }

    public void exitFunction() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("exitFunction on empty stack");
        }

        stack.pop();
    }

    public LogicLabel getReturnLabel(InputPosition inputPosition) {
        if (stack.isEmpty()) {
            error(inputPosition, "Return statement outside of a function.");
            return LogicLabel.INVALID;
        } else {
            return stack.peek().returnLabel;
        }
    }

    public LogicValue getReturnValue(InputPosition inputPosition) {
        if (stack.isEmpty()) {
            error(inputPosition, "Return statement outside of a function.");
            return LogicVariable.INVALID;
        } else {
            return stack.peek().returnValue;
        }
    }

    private record Return(LogicLabel returnLabel, LogicValue returnValue) {
        private Return {
            Objects.requireNonNull(returnLabel);
            Objects.requireNonNull(returnValue);
        }
    }
}