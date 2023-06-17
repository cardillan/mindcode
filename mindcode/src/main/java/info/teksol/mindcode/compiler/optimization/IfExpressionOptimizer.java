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
        forEachContext(AstContextType.IF, BASIC, this::optimizeIfExpression);
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

        LogicInstruction lastTrue = trueBranch.getLast();
        LogicInstruction lastFalse = falseBranch.getLast();
        JumpInstruction invertedJump = negateCompoundCondition(condition);
        boolean swappable = true;

        // Can we rearrange branches?
        // Expensive tests last
        if (lastTrue instanceof LogicResultInstruction res1 && lastFalse instanceof LogicResultInstruction res2
                && res1.getResult().equals(res2.getResult())
                && isContained(trueBranch.toList()) && isContained(falseBranch.toList())) {

            // Do not perform the optimization in recursive functions
            // It might lead to more instructions being evaluated by moving assignment to the function return variable
            // in front of the condition.
            // TODO Activate this if an optimization to replace unconditional jump to return instruction with the
            //      return itself is ever implemented.
            if (true || res1.getAstContext().functionPrefix() == null
                    || !getCallGraph().getFunctionByPrefix(res1.getAstContext().functionPrefix()).isRecursive()) {
                if (invertedJump != null && trueBranch.size() == 1) {
                    moveTrueBranchUsingJump(condition, trueBranch, falseBranch, invertedJump, true);
                    swappable = false;
                } else if (falseBranch.size() == 1) {
                    moveFalseBranch(condition, trueBranch, falseBranch, jump);
                    swappable = false;
                } else if (invertedJump == null && trueBranch.size() == 1 && jump.getCondition().hasInverse()) {
                    moveTrueBranchUsingJump(condition, trueBranch, falseBranch, jump.invert(), false);
                    swappable = false;
                }
            }

            // Can we propagate writes?
            if (res1.getResult().isTemporaryVariable()
                    && instructionAfter(ifExpression) instanceof SetInstruction finalSet
                    && finalSet.getValue().equals(res1.getResult())) {
                replaceInstruction(res1, res1.withResult(finalSet.getResult()));
                replaceInstruction(res2, res2.withResult(finalSet.getResult()));
                removeInstruction(finalSet);
            }
        }

        if (swappable && invertedJump != null) {
            swapBranches(condition, trueBranch, falseBranch, invertedJump);
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
        if (condition.getFromEnd(0) instanceof JumpInstruction jump
                && condition.getFromEnd(1) instanceof OpInstruction op
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
