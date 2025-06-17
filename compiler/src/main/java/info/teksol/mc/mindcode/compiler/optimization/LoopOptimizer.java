package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.arguments.Condition;
import info.teksol.mc.mindcode.logic.arguments.LogicBoolean;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.instructions.JumpInstruction;
import info.teksol.mc.mindcode.logic.instructions.LabelInstruction;
import info.teksol.mc.mindcode.logic.instructions.OpInstruction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;

import static info.teksol.mc.mindcode.compiler.astcontext.AstContextType.EACH;
import static info.teksol.mc.mindcode.compiler.astcontext.AstContextType.LOOP;
import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;
import static java.util.Objects.requireNonNull;

/// The loop optimizers improves loops with the condition at the beginning by performing these modifications:
/// - Replacing the closing unconditional jump to the loop condition with a jump based on the inverse of the loop
///   condition leading to loop body. This avoids the execution of the unconditional jump. If the loop condition evaluates
///   through additional instructions apart from the jump, these will be copied to the end of the loop too (assuming
///   'speed' optimization)
/// - If the previous modification was done, the loop condition consists of the jump only, and it can be determined
///   that the loop condition holds before the first iteration of the loop, the conditional jump at the beginning
///   of the loop is removed.
///
/// If the opening jump has a form of op followed by negation jump, the condition is still replicated at the end
/// of the body as a jump having the op condition. In this case, execution of two instructions per loop is avoided.
@NullMarked
class LoopOptimizer extends BaseOptimizer {
    public LoopOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.LOOP_OPTIMIZATION, optimizationContext);
    }

    private int count = 0;

    @Override
    public void generateFinalMessages() {
        super.generateFinalMessages();
        if (count > 0) {
            emitMessage(MessageLevel.INFO, "%6d loops improved by %s.", count, getName());
        }
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        forEachContext(c -> c.matches(LOOP, EACH) && c.matches(BASIC), loop -> {
            processLoop(loop, true, 0);
            return null;
        });
        return false;
    }

    @Override
    public List<OptimizationAction> getPossibleOptimizations(int costLimit) {
        return forEachContext(c -> c.matches(LOOP, EACH) && c.matches(BASIC),
                loop -> processLoop(loop, false, costLimit));
    }

    private @Nullable OptimizationAction processLoop(AstContext loop, boolean optimize, int costLimit) {
        List<AstContext> conditions = loop.findSubcontexts(CONDITION);
        if (conditions.size() != 1) return null;

        List<AstContext> bodies = loop.findSubcontexts(BODY);
        if (bodies.size() != 1) return null;

        LogicList body = contextInstructions(bodies.getFirst());
        if (body.stream().allMatch(ix -> ix.getRealSize(null) == 0)) return null;

        LogicList condition = contextInstructions(conditions.getFirst());
        LogicList next = contextInstructions(loop.findSubcontext(FLOW_CONTROL));
        if (condition.isEmpty() || next.isEmpty() || !hasConditionAtFront(loop)) {
            return null;
        }

        if (condition.getFirst() instanceof LabelInstruction conditionLabel
                && condition.getLast() instanceof JumpInstruction jump
                && jump.isConditional()
                && next.getLast() instanceof LabelInstruction doneLabel
                && jump.getTarget().equals(doneLabel.getLabel())
                && next.getFromEnd(1) instanceof JumpInstruction backJump
                && backJump.isUnconditional()) {

            // Remove the opening jump, if the loop is known to be executed at least once
            boolean removeOriginal = evaluateLoopConditionJump(jump, loop) == LogicBoolean.FALSE;

            // Keeps condition instructions without the label and jump (possibly empty)
            final LogicList conditionEvaluation;

            // Creates the new jump when label is known
            final BiFunction<AstContext, LogicLabel, JumpInstruction> newJumpCreator;

            if (condition.getFromEnd(1) instanceof OpInstruction op
                    && op.getOperation().isCondition() && op.getResult().isTemporaryVariable()
                    && jump.getCondition() == Condition.EQUAL && jump.getX().equals(op.getResult())
                    && jump.getY().equals(LogicBoolean.FALSE)) {

                // This is an inverse jump
                conditionEvaluation = condition.subList(1, condition.size() - 2); // Remove the op as well
                newJumpCreator = (astContext, target) -> createJump(astContext, target,
                        op.getOperation().toExistingCondition(), op.getX(), op.getY());
            } else {
                if (!jump.getCondition().hasInverse()) {
                    // Inverting the condition wouldn't save execution time
                    return null;
                }
                conditionEvaluation = condition.subList(1, condition.size() - 1);
                newJumpCreator = (astContext, target) -> createJump(astContext, target,
                        jump.getCondition().inverse(), jump.getX(), jump.getY());
            }

            int cost = conditionEvaluation.realSize();
            if (optimize) {
                // Perform the optimization now
                if (removeOriginal || cost <= costLimit) {
                    duplicateCondition(loop, jump, backJump, conditionEvaluation, removeOriginal, newJumpCreator);
                }
            } else if (cost <= costLimit) {
                return new DuplicateConditionAction(loop, cost, backJump.getAstContext().totalWeight(),
                        jump, backJump, conditionEvaluation, removeOriginal, newJumpCreator);
            }
        }

        return null;
    }

    private boolean hasConditionAtFront(AstContext loop) {
        List<AstContext> children = loop.children();
        return (children.size() >= 2) && (children.get(0).subcontextType() == CONDITION
                || children.get(0).subcontextType() == INIT && children.get(1).subcontextType() == CONDITION);
    }

    private OptimizationResult duplicateCondition(AstContext loop, JumpInstruction jump, JumpInstruction backJump,
            LogicList conditionEvaluation, boolean removeOriginal,
            BiFunction<AstContext, LogicLabel, JumpInstruction> newJumpCreator) {
        LogicLabel bodyLabel = obtainContextLabel(requireNonNull(loop.findSubcontext(BODY)));
        LogicList copy = conditionEvaluation.duplicate(true);

        // Replace the last jump first, then insert the rest of the code *before* it
        int index = instructionIndex(backJump);
        replaceInstruction(index, newJumpCreator.apply(requireNonNull(copy.getAstContext()), bodyLabel));
        insertInstructions(index, copy);

        if (removeOriginal) {
            removeInstruction(jump);
        }

        count++;
        return OptimizationResult.REALIZED;
    }

    private class DuplicateConditionAction extends AbstractOptimizationAction {
        private final JumpInstruction jump;
        private final JumpInstruction backJump;
        private final LogicList conditionEvaluation;
        private final boolean removeOriginal;
        private final BiFunction<AstContext, LogicLabel, JumpInstruction> newJumpCreator;

        public DuplicateConditionAction(AstContext astContext, int cost, double benefit, JumpInstruction jump,
                JumpInstruction backJump, LogicList conditionEvaluation, boolean removeOriginal, 
                BiFunction<AstContext, LogicLabel, JumpInstruction> newJumpCreator) {
            super(astContext, cost, benefit);
            this.jump = jump;
            this.backJump = backJump;
            this.conditionEvaluation = conditionEvaluation;
            this.removeOriginal = removeOriginal;
            this.newJumpCreator = newJumpCreator;
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            return applyOptimization(() -> duplicateCondition(astContext, jump, backJump,
                    conditionEvaluation, removeOriginal, newJumpCreator), toString());
        }

        @Override
        public String toString() {
            assert astContext.node() != null;
            return "Replicate loop condition at " + astContext.node().sourcePosition().formatForLog();
        }
    }
}
