package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static info.teksol.mc.mindcode.compiler.astcontext.AstContextType.IF;
import static info.teksol.mc.mindcode.compiler.astcontext.AstContextType.OPERATOR;
import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;
import static java.util.Objects.requireNonNull;

@NullMarked
class IfExpressionOptimizer extends BaseOptimizer {
    public IfExpressionOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.IF_EXPRESSION_OPTIMIZATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        if (experimentalGlobal() && instructionProcessor.isSupported(Opcode.SELECT)) {
            List<AstContext> contexts = contexts(ctx -> ctx.matches(IF, BASIC));
            List<Boolean> result = contexts.stream().map(this::applySelectOptimization).toList();
            if (result.stream().anyMatch(Boolean::booleanValue)) return true;
        }

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

    private record BranchContent(Map<LogicVariable, LogicValue> assignments,
                                 List<SelectInstruction> selects,
                                 List<LogicResultInstruction> operations) {
        boolean isEmpty() {
            return assignments().isEmpty() && selects().isEmpty();
        }
    }

    private @Nullable BranchContent analyzeBranchContent(LogicList branch) {
        Map<LogicVariable, LogicValue> assignments = new HashMap<>();
        List<LogicResultInstruction> operations = new ArrayList<>();
        List<SelectInstruction> selects = new ArrayList<>();

        for (LogicInstruction ix : branch) {
            if (ix.isReal()) {
                switch (ix) {
                    case SelectInstruction s -> {
                        selects.add(s);
                    }
                    case SetInstruction s -> {
                        if (!selects.isEmpty() && s.getValue().isTemporaryVariable() && selects.getLast().getResult().equals(s.getValue())) {
                            // Handles the select-then-set construct also created by this optimization
                            // Allows converting nested ifs into selects in consecutive runs
                            selects.add(selects.removeLast().withResult(s.getResult()));
                        } else {
                            assignments.put(s.getResult(), s.getValue());
                        }
                    }
                    case BaseResultInstruction s -> {
                        if (s.isSelectOperation()) {
                            operations.add(s);
                        } else {
                            LogicVariable result = s.getResult();
                            LogicVariable temp = instructionProcessor.nextTemp();
                            operations.add(s.withResult(temp));
                            assignments.put(result, temp);
                        }
                    }
                    default -> {
                        return null;
                    }
                }
            }
        }

        // We need to modify the last select
        if (!selects.isEmpty()) {
            SelectInstruction lastSelect = selects.removeLast();
            LogicVariable temp = instructionProcessor.nextTemp();
            selects.add(lastSelect.withResult(temp));
            assignments.put(lastSelect.getResult(), temp);
        }

        return new BranchContent(assignments, selects, operations);
    }

    // Note: this optimization doesn't resolve negated strictEqual operation. There will be a separate optimization for that.
    private Boolean applySelectOptimization(AstContext ifExpression) {
        if (!hasSubcontexts(ifExpression, CONDITION, BODY, FLOW_CONTROL, BODY, FLOW_CONTROL)) return false;

        LogicList condition = contextInstructions(ifExpression.child(0));
        if (!(condition.getLast() instanceof JumpInstruction jump && jump.isConditional())) {
            return false;
        }

        LogicList trueBranch = contextInstructions(ifExpression.child(1));
        LogicList falseBranch = contextInstructions(ifExpression.child(3));

        BranchContent trueContent = analyzeBranchContent(trueBranch);
        BranchContent falseContent = analyzeBranchContent(falseBranch);

        // Branches aren't compatible with select optimization
        // We can have at most one operation in the entire expression
        if (trueContent == null || falseContent == null
            || trueContent.operations.size() + falseContent.operations.size() > 1) return false;

        Set<LogicVariable> results = new LinkedHashSet<>(trueContent.assignments.keySet());
        results.addAll(falseContent.assignments.keySet());

        int diff1 = results.size() - trueContent.assignments.size();
        int diff2 = results.size() - falseContent.assignments.size();

        // The total jump overhead executing both alternatives
        int jumpOverhead = trueContent.isEmpty() || falseContent.isEmpty() ? 2 : 3;

        // Select overhead?
        //noinspection SuspiciousMethodCalls
        int selectOverhead = diff1 + diff2 +
                (results.contains(jump.getX()) && results.contains(jump.getY()) ? 2 : 0) +
                (trueContent.operations.isEmpty() && falseContent.operations.isEmpty() ? 0 : 2);

        // Unless the goal is size, don't optimize expressions that have too high an overhead
        if (ifExpression.getLocalProfile().getGoal() == GenerationGoal.SIZE || selectOverhead >= jumpOverhead) return false;

        LogicList flowControl = contextInstructions(ifExpression.child(2));
        if (flowControl.getFirst() instanceof JumpInstruction jump2 && jump2.isUnconditional()
                || (falseContent.isEmpty() && flowControl.getFirst() instanceof EmptyInstruction)) {
            AstContext targetContext = Objects.requireNonNull(ifExpression.parent()).createChild(ifExpression.existingNode(), OPERATOR);
            LogicList instructions = condition.subList(0, condition.size() - 1).duplicateToContext(targetContext, false);

            JumpInstruction invertedJump = negateCompoundCondition(condition);
            if (invertedJump != null) {
                instructions.removeLast();  // This is the op instruction
                replaceIfStatement(ifExpression, instructions, trueContent, falseContent, results,
                        invertedJump.getCondition(), invertedJump.getX(), invertedJump.getY(),
                        false);
            } else if (jump.getCondition().hasInverse()) {
                // The compiler inverts the if condition because the jump leads to the false branch
                // Here we invert it back, so true and false values remain the same
                replaceIfStatement(ifExpression, instructions, trueContent, falseContent, results,
                        jump.getCondition().inverse(), jump.getX(), jump.getY(),
                        false);
            } else {
                // The compiler inverts the if condition because the jump leads to the false branch
                // Here we cannot invert it back, so the true and false values are swapped
                replaceIfStatement(ifExpression, instructions, trueContent, falseContent, results,
                        jump.getCondition(), jump.getX(), jump.getY(),
                        true);
            }
            return true;
        }

        return false;
    }

    private void replaceIfStatement(AstContext ifExpression, LogicList instructions, BranchContent trueContent, BranchContent falseContent,
            Set<LogicVariable> results, Condition condition, LogicValue x, LogicValue y, boolean swapValues) {
        trueContent.operations.stream().map(LogicInstruction::setSelectOperation).forEach(instructions::addToContext);
        instructions.addToContext(trueContent.selects);
        falseContent.operations.stream().map(LogicInstruction::setSelectOperation).forEach(instructions::addToContext);
        instructions.addToContext(falseContent.selects);

        // If the condition depends on one of the variables, that variable needs to be assigned last
        LogicVariable resultX = x instanceof LogicVariable xx && results.remove(xx) ? xx : null;
        LogicVariable resultY = y instanceof LogicVariable yy && results.remove(yy) ? yy : null;

        ArrayList<LogicVariable> variables = new ArrayList<>(results);
        if (resultX != null && resultY != null) {
            LogicVariable t = instructionProcessor.nextTemp();
            instructions.createSet(t, x);
            x = t;
        }
        if (resultX != null) variables.add(resultX);
        if (resultY != null) variables.add(resultY);

        for (LogicVariable result : variables) {
            LogicValue trueValue = trueContent.assignments.getOrDefault(result, result);
            LogicValue falseValue = falseContent.assignments.getOrDefault(result, result);
            instructions.createSelect(result, condition, x, y,
                    swapValues ? falseValue : trueValue,
                    swapValues ? trueValue : falseValue);
        }

        insertInstructions(firstInstructionIndex(ifExpression), instructions);
        removeMatchingInstructions(ix -> ix.belongsTo(ifExpression));
    }

    private void optimizeIfExpression(AstContext ifExpression) {
        if (!isSupportedIfElseExpression(ifExpression)) {
            return;
        }

        LogicList condition = contextInstructions(ifExpression.child(0));
        if (!(condition.getLast() instanceof JumpInstruction jump && jump.isConditional())) {
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
                    && isReplaceable(requireNonNull(instructionAfter = instructionAfter(ifExpression)), resVar)
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

            if (canMoveForward) {
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

    private @Nullable JumpInstruction negateCompoundCondition(LogicList condition) {
        if (condition.getFromEnd(0) instanceof JumpInstruction jump
            && condition.getFromEnd(1) instanceof OpInstruction op
            && op.getOperation().isCondition() && op.getResult().isTemporaryVariable()
            && jump.getCondition() == Condition.EQUAL && jump.getX().equals(op.getResult())
            && jump.getY().equals(LogicBoolean.FALSE)
            && instructionCount(ix -> ix.usesAsInput(op.getResult())) == 1) {

            return createJump(jump.getAstContext(), jump.getTarget(), op.getOperation().toExistingCondition(), op.getX(), op.getY());
        } else {
            return null;
        }
    }

    private void moveTrueBranchUsingJump(LogicList condition, LogicList trueBranch, LogicList falseBranch, JumpInstruction invertedJump,
            boolean compoundCondition) {
        // Move body
        int bodyIndex = instructionIndex(requireNonNull(trueBranch.getFirst()));
        trueBranch.forEach(ix -> removeInstruction(bodyIndex)); // Removes the body
        removeInstruction(bodyIndex);   // Removes the jump
        insertBefore(requireNonNull(condition.getFirst()), trueBranch);

        // Get final label (blind cast since the `if` structure was verified)
        LabelInstruction labelInstruction = (LabelInstruction) instructionAfter(requireNonNull(falseBranch.getLast()));

        // Update condition
        int conditionIndex = instructionIndex(requireNonNull(condition.getLast()));
        replaceInstruction(conditionIndex, invertedJump.withTarget(requireNonNull(labelInstruction).getLabel()));

        // If it was a compound condition, remove it
        // (Note: dead code eliminator in an additional pass would remove it too)
        if (compoundCondition) {
            removeInstruction(conditionIndex - 1);
        }
    }

    private void moveFalseBranch(LogicList condition, LogicList trueBranch, LogicList falseBranch, JumpInstruction jump) {
        // Get final label (blind cast since the `if` structure was verified)
        LabelInstruction labelInstruction = (LabelInstruction) instructionAfter(requireNonNull(falseBranch.getLast()));

        // Move body
        int bodyIndex = instructionIndex(requireNonNull(falseBranch.getFirst()));
        falseBranch.forEach(ix -> removeInstruction(bodyIndex)); // Removes the body
        removeInstruction(bodyIndex - 1);     // Removes the previous jump
        insertBefore(requireNonNull(condition.getFirst()), falseBranch);

        replaceInstruction(jump, jump.withTarget(requireNonNull(labelInstruction).getLabel()));
    }


    private void swapBranches(LogicList condition, LogicList trueBranch, LogicList falseBranch, JumpInstruction invertedJump) {
        if (isContinuous(trueBranch) && isContinuous(falseBranch)) {
            LogicInstruction trueAnchor = instructionAfter(requireNonNull(trueBranch.getLast()));
            LogicInstruction falseAnchor = instructionAfter(requireNonNull(falseBranch.getLast()));

            removeBody(trueBranch);
            removeBody(falseBranch);

            insertInstructions(instructionIndex(requireNonNull(trueAnchor)), falseBranch);
            insertInstructions(instructionIndex(requireNonNull(falseAnchor)), trueBranch);

            // Negate compound condition
            int conditionIndex = instructionIndex(requireNonNull(condition.getLast()));
            replaceInstruction(conditionIndex, invertedJump);
            removeInstruction(conditionIndex - 1);
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
