package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.logic.LogicLabel;
import org.antlr.v4.runtime.Token;

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

    void enterLoop(Token token, String loopLabel, LogicLabel breakLabel, LogicLabel continueLabel) {
        if (loopLabel != null) {
            if (continueMap.containsKey(loopLabel)) {
                throw new MindcodeException(token, "Loop label '%s' already in use.", loopLabel);
            }
            continueMap.put(loopLabel, continueLabel);
            breakMap.put(loopLabel, breakLabel);
        }
        continueStack.push(continueLabel);
        breakStack.push(breakLabel);
    }

    LogicLabel getBreakLabel(Token token, String loopLabel) {
        return getLabel(token, loopLabel, breakStack, breakMap, "break");
    }

    LogicLabel getContinueLabel(Token token, String loopLabel) {
        return getLabel(token, loopLabel, continueStack, continueMap, "continue");
    }

    private LogicLabel getLabel(Token token, String loopLabel, Deque<LogicLabel> stack, Map<String, LogicLabel> map, String statement) {
        if (stack.isEmpty()) {
            throw new MindcodeException(token, "'%s' statement outside of a do/while/for loop.", statement);
        }

        if (loopLabel == null) {
            return stack.peek();
        } else {
            LogicLabel label = map.get(loopLabel);
            if (label == null) {
                throw new MindcodeException(token, "Undefined label '%s'.", loopLabel);
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
