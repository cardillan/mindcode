package info.teksol.mindcode.mindustry.generator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Class maintaining the stack of active functions and the names of their return variables. Used to resolve return
 * statements inside inline functions, which can be nested.
 */
public class ReturnStack {
    private final Deque<Return> stack = new ArrayDeque<>();

    void enterFunction(String returnLabel, String returnValue) {
        stack.push(new Return(returnLabel, returnValue));
    }

    void exitFunction() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("exitFunction on empty stack");
        }

        stack.pop();
    }

    String getReturnLabel() {
        if (stack.isEmpty()) {
            throw new GenerationException("return  statement outside of a function.");
        }

        return stack.peek().label;
    }

    String getReturnValue() {
        if (stack.isEmpty()) {
            throw new GenerationException("return  statement outside of a function.");
        }

        return stack.peek().retval;
    }

    private static final class Return {
        private final String label;
        private final String retval;

        public Return(String label, String retval) {
            this.label = Objects.requireNonNull(label);
            this.retval = Objects.requireNonNull(retval);
        }
    }
}
