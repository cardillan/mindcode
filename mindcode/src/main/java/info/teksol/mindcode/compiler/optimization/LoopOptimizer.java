package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicBoolean;
import info.teksol.mindcode.logic.LogicLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static info.teksol.mindcode.compiler.instructions.AstSubcontextType.*;

/**
 * The loop optimizers improves loops with the condition at the beginning by performing these modifications:
 * <ol>
 * <li>Replacing the closing unconditional jump to the loop condition with a jump based on the inverse of the loop
 * condition leading to loop body. This avoid the execution of the unconditional jump. If the loop condition evaluates
 * through additional instructions apart from the jump, these will be copied to the end of the loop too (assuming the
 * code generation goal is 'speed' and the loop condition takes at most three  additional instructions.)</li>
 * <li>If the previous modification was done, the loop condition consists of the jump only and it can be determined
 * that the loop condition holds before the first iteration of the loop, the conditional jump at the beginning
 * of the loop is removed.</li>
 * </ol>
 * <p>
 * If the opening jump has a form of op followed by negation jump, the condition is still replicated at the end
 * of the body as a jump having the op condition. In this case, execution of two instructions per loop is avoided.
 */
public class LoopOptimizer extends BaseOptimizer {
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
    protected boolean optimizeProgram(OptimizationPhase phase, int pass, int iteration) {
        forEachContext(AstContextType.LOOP, BASIC, loop -> processLoop(loop, true, 0));
        return false;
    }

    private List<OptimizationAction> actions;

    @Override
    public List<OptimizationAction> getPossibleOptimizations(int costLimit) {
        actions = new ArrayList<>();
        forEachContext(AstContextType.LOOP, BASIC, loop -> processLoop(loop, false, costLimit));
        return actions;
    }

    private OptimizationResult processLoop(AstContext loop, boolean optimize, int costLimit) {
        List<AstContext> conditions = loop.findSubcontexts(CONDITION);
        if (conditions.size() != 1) return OptimizationResult.INVALID;

        LogicList condition = contextInstructions(conditions.get(0));
        LogicList next = contextInstructions(loop.findSubcontext(FLOW_CONTROL));
        if (condition.isEmpty() || next.isEmpty() || !hasConditionAtFront(loop)) {
            return OptimizationResult.INVALID;
        }

        if (condition.get(0) instanceof LabelInstruction conditionLabel
                && condition.getLast() instanceof JumpInstruction jump
                && jump.isConditional()
                && next.getLast() instanceof LabelInstruction doneLabel
                && jump.getTarget().equals(doneLabel.getLabel())
                && next.getFromEnd(1) instanceof JumpInstruction backJump
                && backJump.isUnconditional()) {

            LogicInstruction loopSetup = instructionBefore(conditionLabel);

            // Remove the opening jump, if the loop is known to be executed at least once
            boolean removeOriginal = evaluateLoopConditionJump(jump, loop) == LogicBoolean.FALSE;

            // Gather condition instructions without the label and jump (possibly empty)
            final LogicList conditionEvaluation;
            final BiFunction<AstContext, LogicLabel, JumpInstruction> newJumpCreator; // Creates the new jump when label is known

            if (condition.getFromEnd(1) instanceof OpInstruction op
                    && op.getOperation().isCondition() && op.getResult().isTemporaryVariable()
                    && jump.getCondition() == Condition.EQUAL && jump.getX().equals(op.getResult())
                    && jump.getY().equals(LogicBoolean.FALSE)) {

                // This is an inverse jump
                conditionEvaluation = condition.subList(1, condition.size() - 2); // Remove the op as well
                newJumpCreator = (astContext, target) -> createJump(astContext, target,
                        op.getOperation().toCondition(), op.getX(), op.getY());
            } else {
                if (!jump.getCondition().hasInverse()) {
                    // Inverting the condition wouldn't save execution time
                    return OptimizationResult.INVALID;
                }
                conditionEvaluation = condition.subList(1, condition.size() - 1);
                newJumpCreator = (astContext, target) -> createJump(astContext, target,
                        jump.getCondition().inverse(), jump.getX(), jump.getY());
            }

            if (optimize) {
                // Perform the optimization now
                // Can we duplicate the additional condition instructions? If not, nothing to do
                if (removeOriginal || canDuplicate(conditionEvaluation, costLimit)) {
                    duplicateCondition(loop, jump, backJump, conditionEvaluation, removeOriginal, newJumpCreator);
                    count++;
                    return OptimizationResult.REALIZED;
                } else {
                    return OptimizationResult.OVER_LIMIT;
                }
            } else {
                int cost = duplicationCost(conditionEvaluation);
                if (cost <= costLimit) {
                    actions.add(new DuplicateConditionAction(conditions.get(0), cost,
                            backJump.getAstContext().weight(), 2));
                }
                return OptimizationResult.REALIZED;
            }
        }

        return OptimizationResult.INVALID;
    }

    private boolean hasConditionAtFront(AstContext loop) {
        List<AstContext> children = loop.children();
        return (children.size() >= 2) && (children.get(0).subcontextType() == CONDITION
                || children.get(0).subcontextType() == INIT && children.get(1).subcontextType() == CONDITION);
    }

    private int duplicationCost(LogicList conditionEvaluation) {
        // Use real instruction size for the test
        return conditionEvaluation.stream().mapToInt(LogicInstruction::getRealSize).sum();
    }

    private boolean canDuplicate(LogicList conditionEvaluation, int costLimit) {
        return duplicationCost(conditionEvaluation) <= costLimit;
    }

    private void duplicateCondition(AstContext loop, JumpInstruction jump, JumpInstruction backJump,
            LogicList conditionEvaluation, boolean removeOriginal,
            BiFunction<AstContext, LogicLabel, JumpInstruction> newJumpCreator) {
        LogicLabel bodyLabel = obtainContextLabel(loop.findSubcontext(BODY));
        LogicList copy = conditionEvaluation.duplicate();

        // Replace the last jump first, then insert the rest of the code *before* it
        int index = instructionIndex(backJump);
        replaceInstruction(index, newJumpCreator.apply(copy.getAstContext(), bodyLabel));
        insertInstructions(index, copy);

        if (removeOriginal) {
            removeInstruction(jump);
        }
    }

    @Override
    public OptimizationResult applyOptimizationInternal(OptimizationAction optimization, int costLimit) {
        return switch (optimization) {
            case DuplicateConditionAction a -> duplicateCondition(a, costLimit);
            default                         -> OptimizationResult.INVALID;
        };
    }

    private OptimizationResult duplicateCondition(DuplicateConditionAction optimization, int costLimit) {
        return processLoop(optimization.astContext().parent(), true, costLimit);
    }

    private class DuplicateConditionAction extends AbstractOptimizationAction {
        public DuplicateConditionAction(AstContext astContext, int cost, double benefit, int codeMultiplication) {
            super(astContext, cost, benefit, codeMultiplication);
        }

        @Override
        public String toString() {
            return getName() + ": replicate condition at line " + astContext.parent().node().startToken().getLine();
        }
    }
}
