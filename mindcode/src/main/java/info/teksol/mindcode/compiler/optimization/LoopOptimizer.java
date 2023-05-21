package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.MessageLevel;
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

import java.util.ArrayList;
import java.util.List;

import static info.teksol.mindcode.logic.ArgumentType.TMP_VARIABLE;

/**
 * The loop optimizers improves loops with the condition at the beginning by performing these modifications:
 * <ol>
 * <li>Replacing the closing unconditional jump to the loop condition with a jump based on the inverse of the loop
 * condition leading to loop body. This saves the unconditional jump. If the loop condition evaluates through
 * additional instructions apart from the jump, these will be copied to the end of the loop too (assuming the
 * code generation goal is 'speed' and the loop condition takes at most three  additional instructions.)</li>
 * <li>If the previous modification was done and it can be determined that the loop condition holds before the first
 * iteration of the loop: removing the conditional jump at the beginning of the loop.</li>
 * </ol>
 *
 * If the opening jump has a form of op followed by negation jump, the condition is still replicated at the end
 * of the body as a jump based on the op condition. In this case, execution of two instructions per loop is avoided.
 */
public class LoopOptimizer extends GlobalOptimizer {

    public LoopOptimizer(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected boolean optimizeProgram() {
        int count = 0;

        LogicIterator iterator = createIterator();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof JumpInstruction closing && closing.isUnconditional()) {
                // We're locating loops using the backjump instruction. If such instruction leads to
                // a linear sequence of instructions terminated by a conditional jump to a location directly
                // after the backjump, we found a loop with condition at the beginning.

                // Obtain the potential loop setup instruction right before the label
                // There might be additional labels, but we don't care - only the simplest case is handled
                LogicIterator tmpIterator = createIteratorAtLabel(closing.getTarget());
                LogicInstruction loopSetup = tmpIterator.hasPrevious() ? tmpIterator.previous() : null;
                tmpIterator.close();

                try (LogicIterator openingIterator = createIteratorAtLabelledInstruction(closing.getTarget())) {
                    // Loop through the instructions at the (possible) beginning of the loop
                    // Gather instructions serving to evaluate the loop condition
                    List<LogicInstruction> condEval = new ArrayList<>();

                    while (openingIterator.hasNext() && openingIterator.nextIndex() < iterator.nextIndex()) {
                        LogicInstruction openingIx = openingIterator.next();
                        if (openingIx instanceof JumpInstruction jump) {
                            if (jump.getCondition().hasInverse() && targetsLoopExit(jump, iterator) && canDuplicate(condEval)) {
                                LogicLabel bodyLabel = getOpeningLabel(openingIterator);
                                insertInstructions(iterator, condEval);
                                iterator.set(createJump(bodyLabel, jump.getCondition().inverse(),
                                        jump.getFirstOperand(), jump.getSecondOperand()));

                                // Remove the opening jump, if the loop is known to be executed at least once
                                // and the optimization level is aggressive.
                                // Preconditions:
                                // 1. The condition consists of the jump only.
                                // 2. The loop control variable is initialized by a set immediately preceding
                                //    the loop
                                // 3. The jump instruction compares the loop control variable to a constant
                                // 4. The jump evaluates to false (i.e. doesn't skip over the loop) for the initial
                                //    value of the loop control variable
                                //
                                // TODO remove the loop altogether if the loop is known to be never executed, i.e.
                                //      if the jump evaluates to true for the initial value of the loop control variable
                                if (level == OptimizationLevel.AGGRESSIVE && condEval.isEmpty() &&
                                        loopSetup instanceof SetInstruction set && set.getValue().isNumericLiteral() &&
                                        jump.getArgs().contains(set.getTarget())) {
                                    // Cheap trick to replace the loop control variable with its initial value
                                    LogicInstruction test = replaceAllArgs(jump, set.getTarget(), set.getValue());
                                    if (alwaysNegative((JumpInstruction)test)) {
                                        removeInstruction(jump);
                                    }
                                }

                                count++;
                            }
                            // If we couldn't handle this jump, stop processing anyway
                            break;
                        } else if (openingIx instanceof OpInstruction op && openingIterator.peek(0) instanceof JumpInstruction jump
                                && op.getOperation().isCondition() && op.getResult().getType() == TMP_VARIABLE
                                && jump.getCondition() == Condition.EQUAL && jump.getFirstOperand().equals(op.getResult())
                                && jump.getSecondOperand().equals(LogicBoolean.FALSE)
                                && targetsLoopExit(jump, iterator) && canDuplicate(condEval)) {
                            // This is a (probably) non-invertible op and a jump
                            // We make a jump from the op as the loop closing jump
                            openingIterator.next(); // Read the jump instruction
                            LogicLabel bodyLabel = getOpeningLabel(openingIterator);
                            insertInstructions(iterator, condEval);
                            iterator.set(createJump(bodyLabel, op.getOperation().toCondition(),
                                    op.getFirstOperand(), op.getSecondOperand()));
                            count++;
                            break;
                        } else  {
                            condEval.add(openingIx);
                        }
                    }
                }
            }
        }
        iterator.close();

        if (count > 0) {
            emitMessage(MessageLevel.INFO, "%6d loop jumps improved by %s.", count, getClass().getSimpleName());
        }

        return false;
    }

    private boolean alwaysNegative(JumpInstruction jump) {
        if (jump.getFirstOperand().isNumericLiteral() && jump.getSecondOperand().isNumericLiteral()) {
            double a = jump.getFirstOperand().getDoubleValue();
            double b = jump.getSecondOperand().getDoubleValue();
            boolean value = switch (jump.getCondition()) {
                case ALWAYS                 -> true;
                case EQUAL, STRICT_EQUAL    -> ExpressionEvaluator.equals(a, b);
                case NOT_EQUAL              -> !ExpressionEvaluator.equals(a, b);
                case GREATER_THAN           -> a > b;
                case GREATER_THAN_EQ        -> a >= b;
                case LESS_THAN              -> a < b;
                case LESS_THAN_EQ           -> a <= b;
            };
            return !value;
        }
        return false;
    }

    private boolean canDuplicate(List<LogicInstruction> condEval) {
        // Use real instruction size for the test
        return condEval.isEmpty() ||
                isLocalized(condEval) &&
                        goal == GenerationGoal.SPEED &&
                        condEval.stream().mapToInt(LogicInstruction::getRealSize).sum() <= 3;
    }

    private void insertInstructions(LogicIterator iterator, List<LogicInstruction> instructions) {
        if (!instructions.isEmpty()) {
            try (LogicIterator adder = iterator.copy()) {
                adder.previous();
                instructions.forEach(adder::add);
            }
        }
    }

    private boolean targetsLoopExit(JumpInstruction jump, LogicIterator iterator) {
        try (LogicIterator followingIterator = createIteratorAtLabel(jump.getTarget())) {
            return (iterator.nextIndex() <= followingIterator.nextIndex()
                    && iterator.between(followingIterator).allMatch(ix -> ix instanceof LabelInstruction));
        }
    }

    // Obtains or creates a label at the beginning of the loop body
    private LogicLabel getOpeningLabel(LogicIterator openingIterator) {
        if (openingIterator.next() instanceof LabelInstruction label) {
            return label.getLabel();
        } else {
            LogicLabel label = instructionProcessor.nextLabel();
            openingIterator.previous(); // Undo next() in the if condition
            openingIterator.add(createLabel(label));
            return label;
        }
    }
}
