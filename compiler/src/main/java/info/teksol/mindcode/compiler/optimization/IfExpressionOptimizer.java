package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mindcode.logic.*;

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
        boolean canMoveForward = true;

        // Can we rearrange branches?
        // Both branches set the same variable to some value as the last statement
        // Expensive tests last
        if (lastTrue instanceof LogicResultInstruction resTrue && lastFalse instanceof LogicResultInstruction resFalse
                && resTrue.getResult().equals(resFalse.getResult())
                && isContained(trueBranch.toList()) && isContained(falseBranch.toList())) {

            LogicVariable resVar = resTrue.getResult();
            LogicInstruction instructionAfter;

            // Replace the temporary variable with the actual target
            // Only if the temporary variable is not reused anywhere
            if (resVar.isTemporaryVariable()
                    && isReplaceable(instructionAfter = instructionAfter(ifExpression), resVar)
                    && instructionCount(ix -> ix.usesAsInput(resVar)) == 1) {
                if (instructionAfter instanceof SetInstruction finalSet) {
                    resTrue = replaceInstruction(resTrue, resTrue.withResult(finalSet.getResult()));
                    resFalse = replaceInstruction(resFalse, resFalse.withResult(finalSet.getResult()));
                    invalidateInstruction(finalSet);
                } else if (resTrue instanceof SetInstruction setTrue && resFalse instanceof SetInstruction setFalse) {
                    replaceInstruction(setTrue, replaceAllArgs(instructionAfter, resVar, setTrue.getValue())
                            .withContext(setTrue.getAstContext()));
                    replaceInstruction(setFalse, replaceAllArgs(instructionAfter, resVar, setFalse.getValue())
                            .withContext(setFalse.getAstContext()));
                    invalidateInstruction(instructionAfter);
                    canMoveForward = false;
                }

                trueBranch = contextInstructions(ifExpression.child(1));
                falseBranch = contextInstructions(ifExpression.child(3));
            }

            if (advanced() && canMoveForward) {
                LogicVariable updatedResVar = resTrue.getResult();

                boolean globalSafe = !updatedResVar.isGlobalVariable()
                                     || containsNoCall(condition) && containsNoCall(trueBranch) && containsNoCall(falseBranch);

                if (globalSafe && condition.stream().noneMatch(ix -> ix.usesAsInput(updatedResVar))) {
                    if (invertedJump != null && trueBranch.realSize() == 1 && !isVolatile(resTrue) && isSafe(falseBranch, updatedResVar)) {
                        moveTrueBranchUsingJump(condition, trueBranch, falseBranch, invertedJump, true);
                        swappable = false;
                    } else if (falseBranch.realSize() == 1 && !isVolatile(resFalse) && isSafe(trueBranch, updatedResVar)) {
                        moveFalseBranch(condition, trueBranch, falseBranch, jump);
                        swappable = false;
                    } else if (invertedJump == null && trueBranch.realSize() == 1 && jump.getCondition().hasInverse()
                               && !isVolatile(resTrue) && isSafe(falseBranch, updatedResVar)) {
                        moveTrueBranchUsingJump(condition, trueBranch, falseBranch, jump.invert(), false);
                        swappable = false;
                    }
                }
            }
        }

        if (swappable && invertedJump != null) {
            swapBranches(condition, trueBranch, falseBranch, invertedJump);
        }
    }

    private boolean containsNoCall(LogicList list) {
        return list.stream().noneMatch(ix -> ix.getOpcode() == Opcode.CALL || ix.getOpcode() == Opcode.CALLREC);
    }

    /// Returns true if the other branch in the forward assignment optimization is safe:
    /// - the result variable is used only in the last statement,
    /// - the last statement is not self-modifying.
    ///
    /// If the result variable is a FUNCTION_RETVAL one, the optimization is applied nevertheless.
    /// Assignments to these variables are part of compiler-generated logic, and these are safe
    /// in this optimization. Currently on experimental level.
    private boolean isSafe(LogicList branch, LogicVariable resultVariable) {
        if (experimental() && resultVariable.getType() == ArgumentType.FUNCTION_RETVAL) {
            return true;
        }

        LogicInstruction last = getLastRealInstruction(branch);
        if (!(last instanceof LogicResultInstruction res) || !res.getResult().equals(resultVariable) || res.isUpdating()) {
            // We do allow self-modifying operations on function return values
            // They are the result of applying additional operations to the result
            // of the previous call in recursive functions
            return false;
        }

        for (LogicInstruction ix : branch) {
            if (ix == last) {
                break;
            }
            if (ix.usesAsInput(resultVariable)) {
                return false;
            }
        }

        return true;
    }

    private boolean isReplaceable(LogicInstruction instruction, LogicVariable resVar) {
        return instruction instanceof SetInstruction set && set.getValue().equals(resVar)
               || experimental()
                  && instruction.usesAsInput(resVar)
                  && instruction.getOpcode() != Opcode.JUMP
                  && instruction.getOpcode() != Opcode.SETADDR;
    }

    private LogicInstruction getLastRealInstruction(LogicList instructions) {
        for (int i = 0; i < instructions.size(); i ++) {
            if (instructions.getFromEnd(i).getRealSize() > 0) {
                return instructions.getFromEnd(i);
            }
        }

        return null;
    }

    private LogicResultInstruction replaceInstruction(LogicResultInstruction original, LogicResultInstruction replacement) {
        optimizationContext.replaceInstruction(original, replacement);
        return replacement;
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
                && instructionCount(ix -> ix.usesAsInput(op.getResult())) == 1) {

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
