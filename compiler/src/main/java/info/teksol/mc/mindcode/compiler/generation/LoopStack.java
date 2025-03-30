package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstBreakStatement;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstContinueStatement;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLabeledStatement;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/// Class maintaining the stack of active loops and their break and continue labels.
@NullMarked
public class LoopStack extends AbstractMessageEmitter {
    public record LoopLabels(String loopLabel, LogicLabel breakLabel, LogicLabel continueLabel, boolean continueAllowed) {
    }

    private final Deque<LoopLabels> stack = new ArrayDeque<>();
    private final Map<String, LoopLabels> map = new HashMap<>();
    private boolean continueAllowed = true;

    public LoopStack(MessageConsumer messageConsumer) {
        super(messageConsumer);
    }

    public LoopLabels enterLoop(@Nullable AstIdentifier label, LogicLabel breakLabel, LogicLabel continueLabel) {
        String loopLabel = label == null ? "" : label.getName();
        LoopLabels loopLabels = new LoopLabels(loopLabel, breakLabel, continueLabel, continueAllowed);
        if (label != null) {
            if (map.containsKey(loopLabel)) {
                error(label, ERR.LOOP_LABEL_ALREADY_IN_USE, loopLabel);
            } else {
                map.put(loopLabel, loopLabels);
            }
        }
        stack.push(loopLabels);
        return loopLabels;
    }

    public void exitLoop(LoopLabels loopLabels) {
        if (stack.isEmpty()) {
            throw new IllegalStateException("exitLoop on empty stack");
        }

        LoopLabels lastLabels = stack.pop();
        if (lastLabels != loopLabels) {
            throw new IllegalStateException("exitLoop: removing non-topmost label");
        }

        // Do not remove the label from map if it is still found on the stack
        // This is a graceful recovery from an error where loop label is used twice.
        if (!loopLabels.loopLabel.isEmpty()) {
            String loopLabel = loopLabels.loopLabel;
            if (stack.stream().noneMatch(l -> l.loopLabel().equals(loopLabel))) {
                map.remove(loopLabel);
            }
        }

        continueAllowed = lastLabels.continueAllowed;
    }

    public void allowContinue(boolean allowed) {
        continueAllowed = allowed;
    }

    public LogicLabel getBreakLabel(AstBreakStatement breakNode) {
        return getLabel(breakNode, LoopLabels::breakLabel, "break");
    }

    public LogicLabel getContinueLabel(AstContinueStatement continueNode) {
        if (!continueAllowed) {
            error(continueNode, ERR.CONTINUE_NOT_ALLOWED);
        }
        return getLabel(continueNode, LoopLabels::continueLabel, "continue");
    }

    private LogicLabel getLabel(AstLabeledStatement loopNode, Function<LoopLabels, LogicLabel> extractor, String statement) {
        final String loopLabel = loopNode.getLabel() == null ? "" : loopNode.getLabel().getName();

        if (stack.isEmpty()) {
            error(loopNode, ERR.BREAK_CONTINUE_OUTSIDE_LOOP, statement);
            return LogicLabel.INVALID;
        }

        if (loopLabel.isEmpty()) {
            return extractor.apply(stack.peek());
        } else {
            LoopLabels labels = map.get(loopLabel);
            if (labels == null) {
                error(loopNode.getLabel(), ERR.UNDEFINED_LOOP_LABEL, loopLabel);
                return LogicLabel.INVALID;
            }
            return extractor.apply(labels);
        }
    }

}
