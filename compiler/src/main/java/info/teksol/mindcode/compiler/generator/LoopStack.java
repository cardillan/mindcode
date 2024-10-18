package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.logic.LogicLabel;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Class maintaining the stack of active loops and their break and continue labels.
 */
public class LoopStack extends AbstractMessageEmitter {
    private final Deque<LogicLabel> breakStack = new ArrayDeque<>();
    private final Deque<LogicLabel> continueStack = new ArrayDeque<>();
    private final Map<String, LogicLabel> breakMap = new HashMap<>();
    private final Map<String, LogicLabel> continueMap = new HashMap<>();

    public LoopStack(Consumer<MindcodeMessage> messageConsumer) {
        super(messageConsumer);
    }

    void enterLoop(InputPosition position, String loopLabel, LogicLabel breakLabel, LogicLabel continueLabel) {
        if (loopLabel != null) {
            if (continueMap.containsKey(loopLabel)) {
                error(position, "Loop label '%s' already in use.", loopLabel);
            } else {
                continueMap.put(loopLabel, continueLabel);
                breakMap.put(loopLabel, breakLabel);
            }
        }
        continueStack.push(continueLabel);
        breakStack.push(breakLabel);
    }

    LogicLabel getBreakLabel(InputPosition position, String loopLabel) {
        return getLabel(position, loopLabel, breakStack, breakMap, "break");
    }

    LogicLabel getContinueLabel(InputPosition position, String loopLabel) {
        return getLabel(position, loopLabel, continueStack, continueMap, "continue");
    }

    private LogicLabel getLabel(InputPosition position, String loopLabel, Deque<LogicLabel> stack, Map<String, LogicLabel> map, String statement) {
        if (stack.isEmpty()) {
            error(position, "'%s' statement outside of a do/while/for loop.", statement);
            return LogicLabel.INVALID;
        }

        if (loopLabel == null) {
            return stack.peek();
        } else {
            LogicLabel label = map.get(loopLabel);
            if (label == null) {
                error(position, "Undefined label '%s'.", loopLabel);
                return LogicLabel.INVALID;
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
