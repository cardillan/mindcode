package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicVariable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Class maintaining the stack of active functions and their return variables. Used to resolve return
 * statements inside inline functions, which can be nested.
 */
public class ReturnStack {
    private final Deque<Return> stack = new ArrayDeque<>();

    void enterFunction(LogicLabel returnLabel, LogicVariable returnValue) {
        stack.push(new Return(returnLabel, returnValue));
    }

    void exitFunction() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("exitFunction on empty stack");
        }

        stack.pop();
    }

    LogicLabel getReturnLabel() {
        if (stack.isEmpty()) {
            throw new MindcodeException("return statement outside of a function.");
        }

        return stack.peek().label;
    }

    LogicVariable getReturnValue() {
        if (stack.isEmpty()) {
            throw new MindcodeException("return statement outside of a function.");
        }

        return stack.peek().retval;
    }

    private record Return(LogicLabel label, LogicVariable retval) {
        private Return {
            Objects.requireNonNull(label);
            Objects.requireNonNull(retval);
        }
    }
}
