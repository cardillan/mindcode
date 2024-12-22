package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.v3.MessageConsumer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Class maintaining the stack of active loops and their break and continue labels.
 */
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

    public void enterLoop(InputPosition position, @Nullable String loopLabel, LogicLabel breakLabel, LogicLabel continueLabel) {
        // TODO Make loopLabel not-null, use empty string for missing labels
        LoopLabels loopLabels = new LoopLabels(breakLabel, continueLabel);
        if (loopLabel != null && !loopLabel.isEmpty()) {
            if (map.containsKey(loopLabel)) {
                error(position, "Loop label '%s' already in use.", loopLabel);
            } else {
                map.put(loopLabel, loopLabels);
            }
        }
        stack.push(loopLabels);
    }

    public LogicLabel getBreakLabel(InputPosition position, @Nullable String loopLabel) {
        return getLabel(position, loopLabel, LoopLabels::breakLabel, "break");
    }

    public LogicLabel getContinueLabel(InputPosition position, @Nullable String loopLabel) {
        return getLabel(position, loopLabel, LoopLabels::continueLabel, "continue");
    }

    private LogicLabel getLabel(InputPosition position, @Nullable String loopLabel,
            Function<LoopLabels, LogicLabel> extractor, String statement) {
        if (stack.isEmpty()) {
            error(position, "'%s' statement outside of a do/while/for loop.", statement);
            return LogicLabel.INVALID;
        }

        if (loopLabel == null || loopLabel.isEmpty()) {
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

    public void exitLoop(@Nullable String loopLabel) {
        if (stack.isEmpty()) {
            throw new IllegalStateException("exitLoop on empty stack");
        }

        LoopLabels labels = stack.pop();

        if (loopLabel != null && !loopLabel.isEmpty() && !labels.equals(map.remove(loopLabel))) {
            throw new IllegalStateException("exitLoop: removing non-topmost label " + loopLabel);
        }
    }
}
