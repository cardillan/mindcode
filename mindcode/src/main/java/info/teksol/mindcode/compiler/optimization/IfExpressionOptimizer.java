package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicBoolean;
import info.teksol.mindcode.logic.LogicVariable;

import java.util.List;

import static info.teksol.mindcode.compiler.generator.AstSubcontextType.*;

class IfExpressionOptimizer extends BaseOptimizer {

    public IfExpressionOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.IF_EXPRESSION_OPTIMIZATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        forEachContext(AstContextType.IF, BASIC, returningNull(this::optimizeIfExpression));
        return false;
    }

    private void optimizeIfExpression(AstContext ifExpression) {
        LogicList condition = contextInstructions(ifExpression.findSubcontext(CONDITION));
        if (!hasSubcontexts(ifExpression, CONDITION, BODY, FLOW_CONTROL, BODY, FLOW_CONTROL)
                || !(condition.getLast() instanceof JumpInstruction jump && jump.isConditional())
                || !hasExpectedStructure(ifExpression)) {
            return;
        }

        // See the expected subcontext structure above
        LogicList trueBranch = contextInstructions(ifExpression.child(1));
        LogicList falseBranch = contextInstructions(ifExpression.child(3));

        LogicInstruction lastTrue = getLastRealInstruction(trueBranch);
        LogicInstruction lastFalse = getLastRealInstruction(falseBranch);
        JumpInstruction invertedJump = negateCompoundCondition(condition);
        boolean swappable = true;

        // Can we rearrange branches?
        // Expensive tests last
        if (lastTrue instanceof LogicResultInstruction resTrue && lastFalse instanceof LogicResultInstruction resFalse
                && resTrue.getResult().equals(resFalse.getResult())
                && isContained(trueBranch.toList()) && isContained(falseBranch.toList())) {

            LogicVariable resVar = resTrue.getResult();

            // Replace the temporary variable with the actual target
            // Only if the temporary variable is not reused anywhere
            if (resVar.isTemporaryVariable()
                    && instructionAfter(ifExpression) instanceof SetInstruction finalSet
                    && finalSet.getValue().equals(resVar)
                    && instructionCount(ix -> ix.inputArgumentsStream().anyMatch(resVar::equals)) == 1) {
                resTrue = replaceAndGet(resTrue, resTrue.withResult(finalSet.getResult()));
                resFalse = replaceAndGet(resFalse, resFalse.withResult(finalSet.getResult()));
                removeInstruction(finalSet);

                trueBranch = contextInstructions(ifExpression.child(1));
                falseBranch = contextInstructions(ifExpression.child(3));
            }

            LogicVariable updatedResVar = resTrue.getResult();

            // Do not perform the optimization if the condition depends on the resulting variable
            if (condition.stream().noneMatch(ix -> ix.inputArgumentsStream().anyMatch(updatedResVar::equals))) {
                if (invertedJump != null && trueBranch.realSize() == 1 && !isVolatile(resTrue)) {
                    moveTrueBranchUsingJump(condition, trueBranch, falseBranch, invertedJump, true);
                    swappable = false;
                } else if (falseBranch.realSize() == 1 && !isVolatile(resFalse)) {
                    moveFalseBranch(condition, trueBranch, falseBranch, jump);
                    swappable = false;
                } else if (invertedJump == null && trueBranch.realSize() == 1 && jump.getCondition().hasInverse()
                        && !isVolatile(resTrue)) {
                    moveTrueBranchUsingJump(condition, trueBranch, falseBranch, jump.invert(), false);
                    swappable = false;
                }
            }
        }

        if (swappable && invertedJump != null) {
            swapBranches(condition, trueBranch, falseBranch, invertedJump);
        }
    }

    private LogicInstruction getLastRealInstruction(LogicList instructions) {
        for (int i = 0; i < instructions.size(); i ++) {
            if (instructions.getFromEnd(i).getRealSize() > 0) {
                return instructions.getFromEnd(i);
            }
        }

        return null;
    }

    private LogicResultInstruction replaceAndGet(LogicResultInstruction original, LogicResultInstruction replaced) {
        replaceInstruction(original, replaced);
        return replaced;
    }

    private boolean hasExpectedStructure(AstContext ifExpression) {
        List<AstContext> transitions = ifExpression.findSubcontexts(FLOW_CONTROL);
        LogicList first = contextInstructions(transitions.get(0));
        LogicList second = contextInstructions(transitions.get(1));
        return first.size() == 2 && first.getFirst() instanceof JumpInstruction jump && jump.isUnconditional() &&
                second.size() == 1 && second.getFirst() instanceof LabelInstruction;
    }

    private JumpInstruction negateCompoundCondition(LogicList condition) {
        if (condition.getFromEnd(0) instanceof JumpInstruction jump
                && condition.getFromEnd(1) instanceof OpInstruction op
                && op.getOperation().isCondition() && op.getResult().isTemporaryVariable()
                && jump.getCondition() == Condition.EQUAL && jump.getX().equals(op.getResult())
                && jump.getY().equals(LogicBoolean.FALSE)
                && instructionCount(ix -> ix.inputArgumentsStream().anyMatch(op.getResult()::equals)) == 1) {

            return createJump(jump.getAstContext(), jump.getTarget(), op.getOperation().toCondition(), op.getX(), op.getY());
        } else {
            return null;
        }
    }

    private void moveTrueBranchUsingJump(LogicList condition, LogicList trueBranch, LogicList falseBranch, JumpInstruction invertedJump,
            boolean compoundCondition) {
        // Move body
        int bodyIndex = instructionIndex(trueBranch.getFirst());
        trueBranch.forEach(ix -> removeInstruction(bodyIndex)); // Removes the body
        removeInstruction(bodyIndex);   // Removes the jump
        insertBefore(condition.getFirst(), trueBranch);

        // Get final label (blind cast since the if structure was verified)
        LabelInstruction labelInstruction = (LabelInstruction) instructionAfter(falseBranch.getLast());

        // Update condition
        int conditionIndex = instructionIndex(condition.getLast());
        replaceInstruction(conditionIndex, invertedJump.withTarget(labelInstruction.getLabel()));

        // If it was a compound condition, remove it
        // (Note: dead code eliminator in an additional pass would remove it too)
        if (compoundCondition) {
            removeInstruction(conditionIndex - 1);
        }
    }

    private void moveFalseBranch(LogicList condition, LogicList trueBranch, LogicList falseBranch, JumpInstruction jump) {
        // Get final label (blind cast since the if structure was verified)
        LabelInstruction labelInstruction = (LabelInstruction) instructionAfter(falseBranch.getLast());

        // Move body
        int bodyIndex = instructionIndex(falseBranch.getFirst());
        falseBranch.forEach(ix -> removeInstruction(bodyIndex)); // Removes the body
        removeInstruction(bodyIndex - 1);     // Removes the previous jump
        insertBefore(condition.getFirst(), falseBranch);

        replaceInstruction(jump, jump.withTarget(labelInstruction.getLabel()));
    }


    private void swapBranches(LogicList condition, LogicList trueBranch, LogicList falseBranch, JumpInstruction invertedJump) {
        if (isContinuous(trueBranch) && isContinuous(falseBranch)) {
            LogicInstruction trueAnchor = instructionAfter(trueBranch.getLast());
            LogicInstruction falseAnchor = instructionAfter(falseBranch.getLast());

            removeBody(trueBranch);
            removeBody(falseBranch);

            insertInstructions(instructionIndex(trueAnchor), falseBranch);
            insertInstructions(instructionIndex(falseAnchor), trueBranch);

            // Negate compound condition
            int conditionIndex = instructionIndex(condition.getLast());
            replaceInstruction(conditionIndex, invertedJump);
            removeInstruction(conditionIndex - 1);
        }
    }

    private boolean isContinuous(LogicList body) {
        int index = instructionIndex(body.getFirst());
        for (LogicInstruction ix : body) {
            if (ix != instructionAt( index++)) {
                return false;
            }
        }
        return true;
    }

    private void removeBody(LogicList body) {
        int index = instructionIndex(body.getFirst());
        for (int i = 0; i < body.size(); i++) {
            removeInstruction(index);
        }
    }
}
