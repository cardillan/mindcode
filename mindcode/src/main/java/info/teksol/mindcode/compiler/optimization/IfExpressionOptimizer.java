package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.AstContext;
import info.teksol.mindcode.compiler.instructions.AstContextType;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.LabelInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.LogicResultInstruction;
import info.teksol.mindcode.compiler.instructions.OpInstruction;
import info.teksol.mindcode.compiler.instructions.SetInstruction;
import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicBoolean;

import java.util.List;

import static info.teksol.mindcode.compiler.instructions.AstSubcontextType.*;

public class IfExpressionOptimizer extends BaseOptimizer {

    public IfExpressionOptimizer(InstructionProcessor instructionProcessor) {
        super(instructionProcessor);
    }

    @Override
    protected boolean optimizeProgram() {
        forEachContext(c -> c.contextType() == AstContextType.IF && c.subcontextType() == BASIC,
                this::optimizeIfExpression);
        return false;
    }

    private void optimizeIfExpression(AstContext ifExpression) {
        LogicList condition = contextInstructions(ifExpression.findSubcontext(CONDITION));
        if (!hasSubcontexts(ifExpression, CONDITION, BODY, FLOW_CONTROL, BODY, FLOW_CONTROL)
                || !(condition.getLast() instanceof JumpInstruction jump && jump.isConditional())
                || !hasExpectedStructure(ifExpression)) {
            return;
        }

        // See the subcontext structure above
        LogicList trueBranch = contextInstructions(ifExpression.children().get(1));
        LogicList falseBranch = contextInstructions(ifExpression.children().get(3));
        LogicList lastInstruction = contextInstructions(ifExpression.children().get(4));

        LogicInstruction lastTrue = trueBranch.getLast();
        LogicInstruction lastFalse = falseBranch.getLast();
        JumpInstruction negatedJump = negateCompoundCondition(condition);
        boolean trySwap = true;

        // Can we rearrange branches?
        // Expensive tests last
        if (lastTrue instanceof LogicResultInstruction res1 && lastFalse instanceof LogicResultInstruction res2
                && res1.getResult().equals(res2.getResult())
                && isContained(trueBranch.toList()) && isContained(falseBranch.toList())) {

            if (negatedJump != null && trueBranch.size() == 1) {
                moveTrueBranchUsingJump(condition, trueBranch, falseBranch, negatedJump, true);
                trySwap = false;
            } else if (falseBranch.size() == 1) {
                moveFalseBranch(condition, trueBranch, falseBranch, jump);
                trySwap = false;
            } else if (negatedJump == null && trueBranch.size() == 1 && jump.getCondition().hasInverse()) {
                moveTrueBranchUsingJump(condition, trueBranch, falseBranch, jump.invert(), false);
                trySwap = false;
            }

            // Can we propagate writes?
            if (res1.getResult().isTemporaryVariable()
                    && instructionAfter(lastInstruction.getLast()) instanceof SetInstruction finalSet
                    && finalSet.getValue().equals(res1.getResult())) {
                replaceInstruction(res1, res1.withResult(finalSet.getResult()));
                replaceInstruction(res2, res2.withResult(finalSet.getResult()));
                removeInstruction(finalSet);
            }
        }

        if (trySwap && negatedJump != null) {
            swapBranches(condition, trueBranch, falseBranch, negatedJump);
        }
    }

    private boolean hasExpectedStructure(AstContext ifExpression) {
        List<AstContext> transitions = ifExpression.findSubcontexts(FLOW_CONTROL);
        LogicList first = contextInstructions(transitions.get(0));
        LogicList second = contextInstructions(transitions.get(1));
        return first.size() == 2 && first.getFirst() instanceof JumpInstruction jump && jump.isUnconditional() &&
                second.size() == 1 && second.getFirst() instanceof LabelInstruction;
    }

    private JumpInstruction negateCompoundCondition(LogicList condition) {
        if (condition.fromEnd(0) instanceof JumpInstruction jump
                && condition.fromEnd(1) instanceof OpInstruction op
                && op.getOperation().isCondition() && op.getResult().isTemporaryVariable()
                && jump.getCondition() == Condition.EQUAL && jump.getX().equals(op.getResult())
                && jump.getY().equals(LogicBoolean.FALSE)) {

            return createJump(jump.getAstContext(), jump.getTarget(), op.getOperation().toCondition(), op.getX(), op.getY());
        } else {
            return null;
        }
    }

    private void moveTrueBranchUsingJump(LogicList condition, LogicList trueBranch, LogicList falseBranch, JumpInstruction invertedJump,
            boolean compoundCondition) {
        // Move body
        int bodyIndex = instructionIndex(trueBranch.getFirst());
        removeInstruction(bodyIndex);   // Removes the body
        removeInstruction(bodyIndex);   // Removes the jump
        insertBefore(condition.getFirst(), trueBranch.getFirst());

        // Get final label (blind cast since the if structure was verified)
        LabelInstruction labelInstruction = (LabelInstruction) instructionAfter(falseBranch.getLast());

        // Update condition
        int conditionIndex = instructionIndex(condition.getLast());
        replaceInstruction(conditionIndex, invertedJump.withTarget(labelInstruction.getLabel()));

        // If it was a compound instruction, remove it
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
        removeInstruction(bodyIndex);               // Removes the body
        removeInstruction(bodyIndex - 1);     // Removes the previous jump
        insertBefore(condition.getFirst(), falseBranch.getFirst());

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
