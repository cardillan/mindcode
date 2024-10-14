package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicVariable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Class maintaining the stack of active functions and their return variables. Used to resolve return
 * statements inside inline functions, which can be nested.
 */
public class ReturnStack extends MessageEmitter {
    private final Deque<Return> stack = new ArrayDeque<>();

    public ReturnStack(Consumer<MindcodeMessage> messageConsumer) {
        super(messageConsumer);
    }

    void enterFunction(LogicLabel returnLabel, LogicVariable returnValue) {
        stack.push(new Return(returnLabel, returnValue));
    }

    void exitFunction() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("exitFunction on empty stack");
        }

        stack.pop();
    }

    LogicLabel getReturnLabel(InputPosition inputPosition) {
        if (stack.isEmpty()) {
            error(inputPosition, "Return statement outside of a function.");
            return LogicLabel.INVALID;
        } else {
            return stack.peek().label;
        }
    }

    LogicVariable getReturnValue(InputPosition inputPosition) {
        if (stack.isEmpty()) {
            error(inputPosition, "Return statement outside of a function.");
            return LogicVariable.special("invalid");
        } else {
            return stack.peek().retval;
        }
    }

    private record Return(LogicLabel label, LogicVariable retval) {
        private Return {
            Objects.requireNonNull(label);
            Objects.requireNonNull(retval);
        }
    }
}
