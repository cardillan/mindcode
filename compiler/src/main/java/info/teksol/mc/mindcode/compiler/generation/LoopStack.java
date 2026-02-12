package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.CompilerMessageEmitter;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstBreakStatement;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstContinueStatement;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLabeledStatement;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

/// Class maintaining the stack of active loops and their break and continue labels.
@NullMarked
public class LoopStack extends CompilerMessageEmitter {
    private static final LogicLabel NO_LABEL = LogicLabel.symbolic("");

    public record LoopLabels(String loopLabel, LogicLabel breakLabel, LogicLabel continueLabel,
                             boolean codeBlock, boolean continueAllowed) {

        LogicLabel getLabel(boolean isContinue) {
            return isContinue ? continueLabel : breakLabel;
        }
    }

    private final Deque<LoopLabels> breakStack = new ArrayDeque<>();
    private final Deque<LoopLabels> continueStack = new ArrayDeque<>();
    private final Map<String, LoopLabels> map = new HashMap<>();
    private boolean continueAllowed = true;

    public LoopStack(MessageConsumer messageConsumer) {
        super(messageConsumer);
    }

    public LoopLabels enterBlock(@Nullable AstIdentifier label, LogicLabel breakLabel, String defaultLoopLabel) {
        return enterLoop(label, defaultLoopLabel, breakLabel, NO_LABEL, true);
    }

    public LoopLabels enterLoop(@Nullable AstIdentifier label, String defaultLoopLabel, LogicLabel breakLabel, LogicLabel continueLabel) {
        return enterLoop(label, defaultLoopLabel, breakLabel, continueLabel, false);
    }

    private LoopLabels enterLoop(@Nullable AstIdentifier label, String defaultLoopLabel,
            LogicLabel breakLabel, LogicLabel continueLabel, boolean codeBlock) {
        String loopLabel = label == null ? defaultLoopLabel : label.getName();
        LoopLabels loopLabels = new LoopLabels(loopLabel, breakLabel, continueLabel, codeBlock, continueAllowed);
        if (label != null) {
            if (map.containsKey(loopLabel)) {
                error(label, ERR.LOOP_LABEL_ALREADY_IN_USE, loopLabel);
            } else {
                map.put(loopLabel, loopLabels);
            }
        }
        breakStack.push(loopLabels);
        if (continueLabel != NO_LABEL) {
            continueStack.push(loopLabels);
        }
        return loopLabels;
    }

    public void exitLoop(LoopLabels loopLabels) {
        if (breakStack.isEmpty()) {
            throw new IllegalStateException("exitLoop on empty break stack");
        }

        LoopLabels lastLabels = breakStack.pop();
        if (lastLabels != loopLabels) {
            throw new IllegalStateException("exitLoop: removing non-topmost break label");
        }

        if (loopLabels.continueLabel != NO_LABEL) {
            if (continueStack.isEmpty()) {
                throw new IllegalStateException("exitLoop on empty continue stack");
            }

            LoopLabels l = continueStack.pop();
            if (l != loopLabels) {
                throw new IllegalStateException("exitLoop: removing non-topmost continue label");
            }
        }

        // Do not remove the label from a map if it is still found on the stack
        // This is a graceful recovery from an error where a loop label is used twice.
        if (!loopLabels.loopLabel.isEmpty()) {
            String loopLabel = loopLabels.loopLabel;
            if (breakStack.stream().noneMatch(l -> l.loopLabel().equals(loopLabel))) {
                map.remove(loopLabel);
            }
        }

        continueAllowed = lastLabels.continueAllowed;
    }

    public void allowContinue(boolean allowed) {
        continueAllowed = allowed;
    }

    public LogicLabel getBreakLabel(AstBreakStatement breakNode) {
        return getLabel(breakNode, breakStack, false);
    }

    public LogicLabel getContinueLabel(AstContinueStatement continueNode) {
        if (!continueAllowed) {
            error(continueNode, ERR.CONTINUE_NOT_ALLOWED);
        }
        return getLabel(continueNode, continueStack, true);
    }

    private LogicLabel getLabel(AstLabeledStatement node, Deque<LoopLabels> stack, boolean isContinue) {
        if (stack.isEmpty()) {
            error(node, isContinue ? ERR.CONTINUE_OUTSIDE_LOOP : ERR.BREAK_OUTSIDE_LOOP);
            return LogicLabel.INVALID;
        }

        if (node.getLabel() == null) {
            assert !stack.isEmpty();
            if (isContinue) return stack.peek().continueLabel();

            LoopLabels unlabeledBreak = findUnlabeledBreak(stack);
            if (unlabeledBreak == null) {
                error(node, ERR.BREAK_OUTSIDE_LOOP_NO_LABEL);
                return LogicLabel.INVALID;
            }
            return unlabeledBreak.breakLabel();
        } else {
            LoopLabels labels = map.get(node.getLabel().getName());
            if (labels != null) {
                return labels.getLabel(isContinue);
            } else {
                return findDefaultLabel(node, stack, isContinue);
            }
        }
    }

    private LogicLabel findDefaultLabel(AstLabeledStatement node, Deque<LoopLabels> stack, boolean isContinue) {
        assert node.getLabel() != null;
        String label = node.getLabel().getName();
        List<LoopLabels> labels = stack.stream().filter(l -> label.equals(l.loopLabel())).toList();
        switch (labels.size()) {
            case 0 -> {
                error(node.getLabel(), ERR.UNDEFINED_LABEL, label);
                return LogicLabel.INVALID;
            }
            case 1 -> {
                return labels.getFirst().getLabel(isContinue);
            }
            default -> {
                error(node.getLabel(), ERR.AMBIGUOUS_LABEL, label);
                return LogicLabel.INVALID;
            }
        }
    }

    private @Nullable LoopLabels findUnlabeledBreak(Deque<LoopLabels> stack) {
        for (LoopLabels labels : stack) {
            if (!labels.codeBlock) return labels;
        }
        return null;
    }
}
