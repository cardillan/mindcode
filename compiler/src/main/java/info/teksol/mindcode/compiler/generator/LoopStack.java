package info.teksol.mindcode.compiler.generator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Class maintaining the stack of active loops and their break and continue labels.
 */
public class LoopStack {
    private final Deque<String> breakStack = new ArrayDeque<>();
    private final Deque<String> continueStack = new ArrayDeque<>();
    private final Map<String, String> breakMap = new HashMap<>();
    private final Map<String, String> continueMap = new HashMap<>();

    void enterLoop(String loopLabel, String breakLabel, String continueLabel) {
        if (loopLabel != null) {
            if (continueMap.containsKey(loopLabel)) {
                throw new GenerationException("Loop label " + loopLabel + " already in use");
            }
            continueMap.put(loopLabel, continueLabel);
            breakMap.put(loopLabel, breakLabel);
        }
        continueStack.push(continueLabel);
        breakStack.push(breakLabel);
    }

    String getBreakLabel(String loopLabel) {
        return getLabel(loopLabel, breakStack, breakMap, "break");
    }

    String getContinueLabel(String loopLabel) {
        return getLabel(loopLabel, continueStack, continueMap, "continue");
    }

    private String getLabel(String loopLabel, Deque<String> stack, Map<String, String> map, String statement) {
        if (stack.isEmpty()) {
            throw new GenerationException(statement + " statement outside of a do/while/for loop.");
        }

        if (loopLabel == null) {
            return stack.peek();
        } else {
            String label = map.get(loopLabel);
            if (label == null) {
                throw new GenerationException("Undefined label " + loopLabel);
            }
            return label;
        }
    }

    void exitLoop(String loopLabel) {
        if (breakStack.isEmpty()) {
            throw new IllegalStateException("exitLoop on empty stack");
        }

        String breakLabel = breakStack.pop();
        String continueLabel = continueStack.pop();

        if (loopLabel != null) {
            if (!breakLabel.equals(breakMap.remove(loopLabel))) {
                throw new IllegalStateException("exitLoop: removing non-topmost label " + loopLabel);
            }
            if (!continueLabel.equals(continueMap.remove(loopLabel))) {
                throw new IllegalStateException("exitLoop: removing non-topmost label " + loopLabel);
            }
        }
    }
}
