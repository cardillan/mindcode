package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.instructions.AstContext;
import info.teksol.mindcode.compiler.instructions.AstContextType;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.LabelInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.OpInstruction;
import info.teksol.mindcode.compiler.instructions.SetInstruction;
import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicBoolean;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.processor.ExpressionEvaluator;

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

    public LoopOptimizer(InstructionProcessor instructionProcessor) {
        super(instructionProcessor);
    }

    private int count;

    @Override
    protected boolean optimizeProgram() {
        count = 0;
        forEachContext(AstContextType.LOOP, BASIC, this::optimizeLoop);
        if (count > 0) {
            emitMessage(MessageLevel.INFO, "%6d loops improved by %s.", count, getClass().getSimpleName());
        }
        return false;
    }

    private void optimizeLoop(AstContext loop) {
        List<AstContext> conditions = loop.findSubcontexts(CONDITION);
        if (conditions.size() != 1) return;     // Either malformed, or already optimized.

        LogicList condition = contextInstructions(conditions.get(0));
        LogicList next = contextInstructions(loop.findSubcontext(FLOW_CONTROL));
        if (condition.isEmpty() || next.isEmpty() || !hasConditionAtFront(loop)) {
            return;
        }

        if (condition.get(0) instanceof LabelInstruction conditionLabel
                && condition.getLast() instanceof JumpInstruction jump
                && jump.isConditional()
                && next.getLast() instanceof LabelInstruction doneLabel
                && jump.getTarget().equals(doneLabel.getLabel())
                && next.getFromEnd(1) instanceof JumpInstruction backJump
                && backJump.isUnconditional()) {

            LogicInstruction loopSetup = instructionBefore(conditionLabel);

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
                    return;
                }
                conditionEvaluation = condition.subList(1, condition.size() - 1);
                newJumpCreator = (astContext, target) -> createJump(astContext, target,
                        jump.getCondition().inverse(), jump.getX(), jump.getY());
            }

            // Can we duplicate the additional condition instructions? If not, nothing to do
            if (canDuplicate(conditionEvaluation)) {
                LogicLabel bodyLabel = obtainContextLabel(loop.findSubcontext(BODY));
                LogicList copy = conditionEvaluation.duplicate();

                // Replace the last jump first, then insert the rest of the code *before* it
                int index = instructionIndex(backJump);
                replaceInstruction(index, newJumpCreator.apply(copy.getAstContext(),bodyLabel));
                insertInstructions(index, copy);

                // Remove the opening jump, if the loop is known to be executed at least once
                // and the optimization level is aggressive.
                // Preconditions:
                // 1. The condition consists of the condition label and the jump only (i.e. not inverted).
                // 2. The loop control variable is initialized by a set immediately preceding
                //    the loop
                // 3. The jump instruction compares the loop control variable to a constant
                // 4. The jump evaluates to false (i.e. doesn't skip over the loop) for the initial
                //    value of the loop control variable
                //
                // This only handles the simplest cases.
                // TODO use general compile-time evaluation to find the possible states of the condition
                //      and either remove the jump, or the entire loop accordingly.
                if (aggressive() && condition.size() == 2
                        && loopSetup instanceof SetInstruction set && set.getValue().isNumericLiteral()
                        && jump.getArgs().contains(set.getResult())) {
                    // Replace the loop control variable with its initial value and evaluate
                    LogicInstruction test = replaceAllArgs(jump, set.getResult(), set.getValue());
                    if (alwaysNegative((JumpInstruction) test)) {
                        removeInstruction(jump);
                    }
                }

                count++;
            }
        }
    }

    private boolean hasConditionAtFront(AstContext loop) {
        List<AstContext> children = loop.children();
        return (children.size() >= 2) && (children.get(0).subcontextType() == CONDITION
                || children.get(0).subcontextType() == INIT && children.get(1).subcontextType() == CONDITION);
    }

    private boolean canDuplicate(LogicList conditionEvaluation) {
        // Use real instruction size for the test
        int size = conditionEvaluation.stream().mapToInt(LogicInstruction::getRealSize).sum();
        return size == 0 || goal == GenerationGoal.SPEED && size <= 3;
    }

    private boolean alwaysNegative(JumpInstruction jump) {
        if (jump.getX().isNumericLiteral() && jump.getY().isNumericLiteral()) {
            double a = jump.getX().getDoubleValue();
            double b = jump.getY().getDoubleValue();
            boolean value = switch (jump.getCondition()) {
                case ALWAYS             -> true;
                case EQUAL,STRICT_EQUAL -> ExpressionEvaluator.equals(a, b);
                case NOT_EQUAL          -> !ExpressionEvaluator.equals(a, b);
                case GREATER_THAN       -> a > b;
                case GREATER_THAN_EQ    -> a >= b;
                case LESS_THAN          -> a < b;
                case LESS_THAN_EQ       -> a <= b;
            };
            return !value;
        }
        return false;
    }
}
