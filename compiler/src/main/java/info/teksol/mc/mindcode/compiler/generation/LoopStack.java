package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/// Class maintaining the stack of active loops and their break and continue labels.
@NullMarked
public class LoopStack extends AbstractMessageEmitter {
    private record LoopLabels(LogicLabel breakLabel, LogicLabel continueLabel) {
    }

    private final Deque<LoopLabels> stack = new ArrayDeque<>();
    private final Deque<LogicLabel> breakStack = new ArrayDeque<>();
    private final Deque<LogicLabel> continueStack = new ArrayDeque<>();
    private final Map<String, LoopLabels> map = new HashMap<>();
    private final Map<String, LogicLabel> breakMap = new HashMap<>();
    private final Map<String, LogicLabel> continueMap = new HashMap<>();

    public LoopStack(MessageConsumer messageConsumer) {
        super(messageConsumer);
    }

    public void enterLoop(SourcePosition position, String loopLabel, LogicLabel breakLabel, LogicLabel continueLabel) {
        LoopLabels loopLabels = new LoopLabels(breakLabel, continueLabel);
        if (!loopLabel.isEmpty()) {
            if (map.containsKey(loopLabel)) {
                error(position, "Loop label '%s' already in use.", loopLabel);
            } else {
                map.put(loopLabel, loopLabels);
            }
        }
        stack.push(loopLabels);
    }

    public LogicLabel getBreakLabel(SourcePosition position, String loopLabel) {
        return getLabel(position, loopLabel, LoopLabels::breakLabel, "break");
    }

    public LogicLabel getContinueLabel(SourcePosition position, String loopLabel) {
        return getLabel(position, loopLabel, LoopLabels::continueLabel, "continue");
    }

    private LogicLabel getLabel(SourcePosition position, String loopLabel,
            Function<LoopLabels, LogicLabel> extractor, String statement) {
        if (stack.isEmpty()) {
            error(position, "'%s' statement outside of a do/while/for loop.", statement);
            return LogicLabel.INVALID;
        }

        if (loopLabel.isEmpty()) {
            return extractor.apply(stack.peek());
        } else {
            LoopLabels labels = map.get(loopLabel);
            if (labels == null) {
                error(position, "Undefined label '%s'.", loopLabel);
                return LogicLabel.INVALID;
            }
            return extractor.apply(labels);
        }
    }

    public void exitLoop(String loopLabel) {
        if (stack.isEmpty()) {
            throw new IllegalStateException("exitLoop on empty stack");
        }

        LoopLabels labels = stack.pop();

        if (!loopLabel.isEmpty() && !labels.equals(map.remove(loopLabel))) {
            throw new IllegalStateException("exitLoop: removing non-topmost label " + loopLabel);
        }
    }
}
