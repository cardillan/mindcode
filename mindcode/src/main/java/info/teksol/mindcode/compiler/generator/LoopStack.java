package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.logic.LogicLabel;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Class maintaining the stack of active loops and their break and continue labels.
 */
public class LoopStack {
    private final Deque<LogicLabel> breakStack = new ArrayDeque<>();
    private final Deque<LogicLabel> continueStack = new ArrayDeque<>();
    private final Map<String, LogicLabel> breakMap = new HashMap<>();
    private final Map<String, LogicLabel> continueMap = new HashMap<>();

    void enterLoop(String loopLabel, LogicLabel breakLabel, LogicLabel continueLabel) {
        if (loopLabel != null) {
            if (continueMap.containsKey(loopLabel)) {
                throw new MindcodeException("Loop label '" + loopLabel + "' already in use.");
            }
            continueMap.put(loopLabel, continueLabel);
            breakMap.put(loopLabel, breakLabel);
        }
        continueStack.push(continueLabel);
        breakStack.push(breakLabel);
    }

    LogicLabel getBreakLabel(String loopLabel) {
        return getLabel(loopLabel, breakStack, breakMap, "break");
    }

    LogicLabel getContinueLabel(String loopLabel) {
        return getLabel(loopLabel, continueStack, continueMap, "continue");
    }

    private LogicLabel getLabel(String loopLabel, Deque<LogicLabel> stack, Map<String, LogicLabel> map, String statement) {
        if (stack.isEmpty()) {
            throw new MindcodeException(statement + " statement outside of a do/while/for loop.");
        }

        if (loopLabel == null) {
            return stack.peek();
        } else {
            LogicLabel label = map.get(loopLabel);
            if (label == null) {
                throw new MindcodeException("Undefined label '" + loopLabel + "'.");
            }
            return label;
        }
    }

    void exitLoop(String loopLabel) {
        if (breakStack.isEmpty()) {
            throw new IllegalStateException("exitLoop on empty stack");
        }

        LogicLabel breakLabel = breakStack.pop();
        LogicLabel continueLabel = continueStack.pop();

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
