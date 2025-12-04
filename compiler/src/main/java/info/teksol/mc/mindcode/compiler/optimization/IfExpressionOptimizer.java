package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.arguments.ArgumentType;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static info.teksol.mc.mindcode.compiler.astcontext.AstContextType.IF;
import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;
import static java.util.Objects.requireNonNull;

@NullMarked
class IfExpressionOptimizer extends AbstractConditionalOptimizer {
    public IfExpressionOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.IF_EXPRESSION_OPTIMIZATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        forEachContext(IF, BASIC, returningNull(this::optimizeIfExpression));
        return false;
    }

    private boolean isNestedIfExpression(LogicList falseBranch) {
        AstContext astContext = falseBranch.getAstContext();
        if (astContext == null || !astContext.matches(IF, BODY)) {
            throw new MindcodeInternalError("Invalid context structure");
        }

        return astContext.children().size() == 1
               && astContext.children().getFirst().matches(IF, BASIC);
    }

    private boolean isLinearSubcontext(@Nullable AstContext context, AstContext subcontext) {
        while (subcontext != null) {
            if (subcontext == context) return true;
            if (!subcontext.isLinear()) return false;
            subcontext = subcontext.parent();
        }

        throw new MindcodeInternalError("Invalid context structure");
    }

    /// Determines whether the IF context has a supported structure:
    /// 1. If the conditions are chained (using nesting), there's a final else branch
    /// 2. The last real instruction in each branch is not nested in another control structure
    private boolean isSupportedIfElseExpression(AstContext ifExpression) {
        if (!hasSubcontexts(ifExpression, CONDITION, BODY, FLOW_CONTROL, BODY, FLOW_CONTROL)
            || !hasExpectedStructure(ifExpression))
            return false;

        // Check true branch
        LogicList trueBranch = contextInstructions(ifExpression.child(1));
        LogicInstruction trueInstruction = getLastRealInstruction(trueBranch);
        if (trueInstruction == null || !isLinearSubcontext(trueBranch.getAstContext(), trueInstruction.getAstContext())) {
            return false;
        }

        LogicList falseBranch = contextInstructions(ifExpression.child(3));
        if (isNestedIfExpression(falseBranch)) {
            return isSupportedIfElseExpression(ifExpression.child(3).children().getFirst());
        } else {
            LogicInstruction falseInstruction = getLastRealInstruction(trueBranch);
            return falseInstruction != null && isLinearSubcontext(trueBranch.getAstContext(), falseInstruction.getAstContext());
        }
    }

    private void optimizeIfExpression(AstContext ifExpression) {
        if (!isSupportedIfElseExpression(ifExpression)) {
            return;
        }

        LogicList condition = contextInstructions(ifExpression.child(0));
        ShortCircuitAnalysis analysis = analyzeShortCircuit(condition);
        if (analysis == null) {
            return;
        }

        // See the expected subcontext structure above
        JumpInstruction jump = analysis.lastJump();
        LogicList trueBranch = contextInstructions(ifExpression.child(1));
        LogicList falseBranch = contextInstructions(ifExpression.child(3));

        LogicInstruction lastTrue = getLastRealInstruction(trueBranch);
        LogicInstruction lastFalse = getLastRealInstruction(falseBranch);
        JumpInstruction invertedJump = negateCompoundCondition(condition);
        boolean swappable = true;

        // Can we rearrange branches?
        // Both branches set the same variable to some value as the last statement
        // Expensive tests last
        if (lastTrue instanceof LogicResultInstruction resTrue && lastFalse instanceof LogicResultInstruction resFalse
                && resTrue.getResult().equals(resFalse.getResult())
                && !resTrue.isUpdating() && !resFalse.isUpdating() && !resTrue.getResult().isVolatile()
                && isContained(trueBranch.toList()) && isContained(falseBranch.toList())) {

            LogicVariable resVar = resTrue.getResult();
            List<LogicInstruction> references = getVariableReferences(resVar);
            LogicInstruction instructionAfter = requireNonNull(instructionAfter(ifExpression));
            boolean canMoveForward = resVar.isTemporaryVariable()
                    ? references.stream().filter(LogicResultInstruction.class::isInstance)
                            .map(LogicResultInstruction.class::cast).noneMatch(ix -> ix.getResult().isVolatile())
                    : !resVar.isVolatile();

            // Replace the temporary variable with the actual target
            // Only if the temporary variable is not reused anywhere
            if (resVar.isTemporaryVariable()
                    && references.stream().filter(ix -> ix.usesAsInput(resVar)).toList().equals(List.of(instructionAfter))
                    && isReplaceable(instructionAfter, resVar)) {
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

            if (canMoveForward && (!analysis.shortCircuit() || analysis.operation() != null)) {
                LogicVariable updatedResVar = resTrue.getResult();

                boolean globalSafe = !updatedResVar.isGlobalVariable()
                        || containsNoCall(condition) && containsNoCall(trueBranch) && containsNoCall(falseBranch);

                if (globalSafe && condition.stream().noneMatch(ix -> ix.usesAsInput(updatedResVar))) {
                    boolean falseSafe = isSafe(falseBranch, updatedResVar);
                    if (invertedJump != null && trueBranch.realSize() == 1 && !isVolatile(resTrue)
                            && falseSafe && analysis.jumpCount() == 1) {
                        moveTrueBranchUsingJump(condition, trueBranch, falseBranch, jump, invertedJump, true);
                        swappable = false;
                    } else if (falseBranch.realSize() == 1 && !isVolatile(resFalse) && isSafe(trueBranch, updatedResVar)
                            && analysis.canPrecomputeFalseBranch()) {
                        moveFalseBranch(condition, trueBranch, falseBranch, jump);
                        swappable = false;
                    } else if (invertedJump == null && trueBranch.realSize() == 1 && jump.getCondition().hasInverse(getGlobalProfile())
                               && !isVolatile(resTrue) && falseSafe && analysis.canPrecomputeTrueBranch()) {
                        moveTrueBranchUsingJump(condition, trueBranch, falseBranch, jump, jump.invert(getGlobalProfile()), false);
                        swappable = false;
                    }
                }
            }
        }

        if (swappable && invertedJump != null) {
            swapBranches(analysis, condition, trueBranch, falseBranch, invertedJump);
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
    /// in this optimization.
    private boolean isSafe(LogicList branch, LogicVariable resultVariable) {
        if (resultVariable.getType() == ArgumentType.FUNCTION_RETVAL) {
            // Note we do allow even self-modifying operations on function return values.
            // They are the result of applying additional operations to the result
            // of the previous call in recursive functions
            return true;
        }

        LogicInstruction last = getLastRealInstruction(branch);
        if (!(last instanceof LogicResultInstruction res) || !res.getResult().equals(resultVariable) || res.isUpdating()) {
            // Self-modifying instructions aren't safe for optimization
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
                || instruction.usesAsInput(resVar)
                && instruction.getOpcode() != Opcode.JUMP
                && instruction.getOpcode() != Opcode.SETADDR;
    }

    private @Nullable LogicInstruction getLastRealInstruction(LogicList instructions) {
        for (int i = 0; i < instructions.size(); i ++) {
            if (requireNonNull(instructions.getFromEnd(i)).isReal()) {
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

    private void moveTrueBranchUsingJump(LogicList condition, LogicList trueBranch, LogicList falseBranch,
            JumpInstruction jump, JumpInstruction invertedJump, boolean compoundCondition) {
        // Move body
        int bodyIndex = instructionIndex(requireNonNull(trueBranch.getFirst()));
        trueBranch.forEach(ix -> removeInstruction(bodyIndex)); // Removes the body
        removeInstruction(bodyIndex);   // Removes the jump
        insertBefore(requireNonNull(condition.getFirst()), trueBranch);

        // Get the final label (blind cast since the `if` structure was verified)
        LabelInstruction labelInstruction = (LabelInstruction) instructionAfter(requireNonNull(falseBranch.getLast()));
        LogicLabel newLabel = requireNonNull(labelInstruction).getLabel();

        // Update condition
        int conditionIndex = instructionIndex(jump);

        // Update all jumps in condition (for short-circuiting)
        condition.stream()
                .filter(JumpInstruction.class::isInstance)
                .map(JumpInstruction.class::cast)
                .forEach(ix -> replaceInstruction(ix,
                        (ix == jump ? invertedJump : ix).withTarget(newLabel)));

        // If it was a compound condition, remove it
        // (Note: dead code eliminator in an additional pass would remove it too)
        if (compoundCondition) {
            removeInstruction(conditionIndex - 1);
        }
    }

    private void moveFalseBranch(LogicList condition, LogicList trueBranch, LogicList falseBranch, JumpInstruction jump) {
        // Get the final label (blind cast since the `if` structure was verified)
        LabelInstruction labelInstruction = (LabelInstruction) instructionAfter(requireNonNull(falseBranch.getLast()));

        // Move body
        int bodyIndex = instructionIndex(requireNonNull(falseBranch.getFirst()));
        falseBranch.forEach(ix -> removeInstruction(bodyIndex)); // Removes the body
        removeInstruction(bodyIndex - 1);     // Removes the previous jump
        insertBefore(requireNonNull(condition.getFirst()), falseBranch);

        // New label
        LogicLabel oldLabel = jump.getTarget();
        LogicLabel newLabel = requireNonNull(labelInstruction).getLabel();

        // Update all jumps in condition (for short-circuiting)
        condition.stream()
                .filter(JumpInstruction.class::isInstance)
                .map(JumpInstruction.class::cast)
                .filter(j -> j.getTarget().equals(oldLabel))
                .forEach(j -> replaceInstruction(j, j.withTarget(newLabel)));
    }

    private void swapBranches(ShortCircuitAnalysis analysis, LogicList condition,
            LogicList trueBranch, LogicList falseBranch, JumpInstruction invertedJump) {
        if (isContinuous(trueBranch) && isContinuous(falseBranch)) {
            LogicInstruction trueAnchor = instructionAfter(requireNonNull(trueBranch.getLast()));
            LogicInstruction falseAnchor = instructionAfter(requireNonNull(falseBranch.getLast()));

            removeBody(trueBranch);
            removeBody(falseBranch);

            insertInstructions(instructionIndex(requireNonNull(trueAnchor)), falseBranch);
            insertInstructions(instructionIndex(requireNonNull(falseAnchor)), trueBranch);

            // Negate compound condition
            if (analysis.shortCircuit()) {
                JumpInstruction originalJump = (JumpInstruction) requireNonNull(condition.getRealFromEnd(0));
                LabelInstruction trueLabel = (LabelInstruction) requireNonNull(condition.getLast());
                LabelInstruction falseLabel = getLabelInstruction(originalJump.getTarget());

                // Swap labels
                int trueLabelIndex = instructionIndex(trueLabel);
                int falseLabelIndex = instructionIndex(falseLabel);
                invalidateInstruction(trueLabelIndex);
                invalidateInstruction(falseLabelIndex);
                replaceInstruction(trueLabelIndex, trueLabel.withLabel(falseLabel.getLabel()));
                replaceInstruction(falseLabelIndex, falseLabel.withLabel(trueLabel.getLabel()));
                replaceInstruction(originalJump, invertedJump.withTarget(trueLabel.getLabel()));
            } else {
                int conditionIndex = instructionIndex(requireNonNull(condition.getLast()));
                replaceInstruction(conditionIndex, invertedJump);
                removeInstruction(conditionIndex - 1);
            }
        }
    }

    private boolean isContinuous(LogicList body) {
        int index = instructionIndex(requireNonNull(body.getFirst()));
        for (LogicInstruction ix : body) {
            if (ix != instructionAt( index++)) {
                return false;
            }
        }
        return true;
    }

    private void removeBody(LogicList body) {
        int index = instructionIndex(requireNonNull(body.getFirst()));
        for (int i = 0; i < body.size(); i++) {
            removeInstruction(index);
        }
    }
}
