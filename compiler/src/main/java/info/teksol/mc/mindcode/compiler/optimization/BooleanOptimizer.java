package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.GenerationGoal;
import info.teksol.mc.util.CollectionUtils;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.compiler.astcontext.AstContextType.IF;
import static info.teksol.mc.mindcode.compiler.astcontext.AstContextType.OPERATOR;
import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;
import static info.teksol.mc.mindcode.compiler.optimization.AbstractConditionalOptimizer.JumpTarget.*;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

@NullMarked
class BooleanOptimizer extends AbstractConditionalOptimizer {
    private final boolean hasSelect;

    public BooleanOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.BOOLEAN_OPTIMIZATION, optimizationContext);
        hasSelect = getProcessor().isSupported(Opcode.SELECT);
    }

    private int fullConditions = 0;
    private int fullExpressions = 0;
    private int shortSelectSequence = 0;
    private int lastJumps = 0;
    private int fullSelectSequence = 0;

    @Override
    public void generateFinalMessages() {
        super.generateFinalMessages();
        outputActions("       %d short-circuited expressions turned to full evaluations.", fullExpressions);
        outputActions("       %d short-circuited conditions turned to full evaluations.", fullConditions);
        outputActions("       %d short-circuited expressions optimized using selects.", shortSelectSequence);
        outputActions("       %d final jumps of a short-circuited expression optimized.", lastJumps);
        outputActions("       %d fully-evaluated expressions optimized using selects.", fullSelectSequence);
    }

    private void outputActions(@PrintFormat String format, int count) {
        if (count > 0) emitMessage(MessageLevel.INFO, format, count);
    }


    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        List<AstContext> contexts = contexts(ctx -> ctx.matches(IF, BASIC));
        List<Boolean> result = contexts.stream().map(this::applySelectOptimization).toList();
        return result.stream().anyMatch(Boolean::booleanValue);
    }

    private boolean applySelectOptimizationDebug(AstContext ifExpression) {
        String before = optimizationContext.getProgramTextFullAst();
        boolean result = applySelectOptimization(ifExpression);
        if (result) {
            System.out.println("Before:\n" + before);
            System.out.println("\n\nAfter:\n" + optimizationContext.getProgramTextFullAst());
            System.out.println("\n\n");
        }
        return result;
    }

    // Note: this optimization doesn't resolve negated strictEqual operation. There will be a separate optimization for that.
    private boolean applySelectOptimization(AstContext ifExpression) {
        if (!hasSubcontexts(ifExpression, CONDITION, BODY, FLOW_CONTROL, BODY, FLOW_CONTROL)
                && !hasSubcontexts(ifExpression, CONDITION, BODY, FLOW_CONTROL, BODY)) return false;

        AstContext conditionContext = ifExpression.child(0);
        if (optimizationContext.isShortCircuitCondition(conditionContext)) {
            // Help the optimizer a bit by removing the unreachable code within the short-circuited context
            while (removeUnreachableCode(conditionContext.children().getFirst()));
        }

        LogicList condition = contextInstructions(conditionContext);
        ShortCircuitAnalysis analysis = analyzeShortCircuit(condition, true);
        if (analysis == null) {
            return false;
        }

        LogicList trueBranch = contextInstructions(ifExpression.child(1));
        LogicList falseBranch = contextInstructions(ifExpression.child(3));

        BranchContent trueContent = analyzeBranchContent(ifExpression, trueBranch);
        BranchContent falseContent = analyzeBranchContent(ifExpression, falseBranch);
        int numOperations = trueContent.numOperations() + falseContent.numOperations();

        // Branches aren't compatible with select optimization
        if (trueContent.invalid() || falseContent.invalid()) {
            return false;
        }

        // Make sure there's no dependency cycle in the combined contents of both branches
        Map<LogicVariable, Set<LogicVariable>> dependencies = new HashMap<>(trueContent.dependencies);
        dependencies.putAll(falseContent.dependencies);
        if (hasDependencyCycle(dependencies)) return false;

        SequencedSet<LogicVariable> results = new LinkedHashSet<>(trueContent.results);
        results.addAll(falseContent.results);
        if (results.isEmpty()) return false;

        // There's a single branch only, and it has operations: the optimization wouldn't decrease the code size and would make
        // the execution slower.
        if ((trueContent.isEmpty() || falseContent.isEmpty()) && numOperations > 0) {
            return false;
        }

        LogicList flowControl = contextInstructions(ifExpression.child(2));
        boolean hasExpectedStructure = flowControl.getFirst() instanceof JumpInstruction jump2 && jump2.isUnconditional()
                || (falseContent.isEmpty() && flowControl.getFirst() instanceof EmptyInstruction);
        if (!hasExpectedStructure) return false;

        int diff1 = results.size() - trueContent.results.size();
        int diff2 = results.size() - falseContent.results.size();

        // The total jump steps executing both alternatives
        boolean optimizeForSize = ifExpression.getLocalProfile().getGoal() == GenerationGoal.SIZE;

        // The entire if expression is a simple ternary operation
        boolean ternaryOp = results.size() == 1 && trueContent.hasSingleAssignment() && falseContent.hasSingleAssignment();

        // The expression produces a boolean value
        boolean booleanOp = ternaryOp && mapsToBoolean(trueContent, falseContent);

        ConditionJumpHandling jumpHandling = analyzeJump(analysis.lastJump(), trueContent, falseContent);

        // Without select we can only encode expressions producing booleans
        if (!hasSelect && !booleanOp) return false;

        if (analysis.shortCircuit()) {
            LogicVariable result = results.getFirst();

            // We'll try to switch to full evaluation here
            if (analysis.jumps().size() == 3 && booleanOp && analysis.shortedOpCount() == 0 && analysis.plainOperands()) {
                if (evaluateFully(ifExpression, analysis, condition, result)) {
                    fullExpressions++;
                    return true;
                }
            }

            // Optimize only linear conditions with more than one jump
            if (analysis.operation() != null) {
                if (!analysis.sideEffects()) {
                    // Full evaluation is possible and compatible with the goal
                    // Since full evaluation stores the condition value in a variable anyway, no need for jump handling

                    int maxShortedOpCount = optimizeForSize ? results.size() - diff1 - diff2 : 0;
                    int maxSpeedJumpCount = booleanOp && analysis.shortedOpCount() == 0 && analysis.plainOperands() ? 3 : 2;
                    boolean isGoalCompatible = optimizeForSize ? numOperations <= 5
                            : analysis.jumpCount() <= maxSpeedJumpCount && diff1 + diff2 + numOperations == 0;
                    if (analysis.onlyEquals() && analysis.shortedOpCount() <= maxShortedOpCount && isGoalCompatible) {
                        // We convert to full evaluation assuming the select conversion will happen later.
                        // We're being conservative and require the select overhead to be 0.
                        convertToFullEvaluation(conditionContext, condition, analysis);
                        fullConditions++;
                        return true;
                    } else if (hasSelect && ternaryOp) {
                        int maxJumpCount = optimizeForSize ? 5 : maxSpeedJumpCount;
                        if (analysis.shortedOpCount() <= maxShortedOpCount && analysis.jumpCount() <= maxJumpCount) {
                            // Only one variable to be set: a select sequence is always shorter and faster
                            convertToSelectSequence(ifExpression, condition, analysis, trueContent, falseContent, result);
                            shortSelectSequence++;
                            return true;
                        }
                    }
                }

                // Here we try to replace the very last jump in a condition with a 'select'
                // Always shorter and faster.
                // Since it is just one select, no need for jump handling
                if (ternaryOp && hasSelect || booleanOp) {
                    if (convertLastJump(analysis, condition, trueBranch, falseBranch, trueContent, falseContent, result)) {
                        lastJumps++;
                        return true;
                    }
                }
            }

            // Couldn't do anything
            return false;
        } else if (analysis.normal()) {
            // Here the entire condition has only one jump
            // Subtracting one from the select steps: we're replacing the condition jump with a select sequence
            int jumpHandlingCost = jumpHandling == ConditionJumpHandling.NONE ? 0 : 1;
            double selectSteps = results.size() + numOperations + jumpHandlingCost - 1;
            double originalSteps = (trueContent.size() + falseContent.size()) / 2.0
                    + (!trueContent.isEmpty() && !falseContent.isEmpty() ? 0.5 : 0);

            if (optimizeForSize ? numOperations <= 5 : selectSteps < originalSteps) {
                if (optimizeFullEvaluation(ifExpression, condition, trueContent, falseContent, results, analysis.lastJump(), jumpHandling)) {
                    fullSelectSequence++;
                    return true;
                }
            }
        }

        return false;
    }

    private interface FullBooleanOptimizer {
        boolean optimize(AstContext ifExpression, LogicList condition, ShortCircuitAnalysis analysis, LogicVariable result);
    }

    private boolean evaluateFully(AstContext ifExpression, ShortCircuitAnalysis analysis, LogicList condition, LogicVariable result) {
        return tryEvaluate(ifExpression, analysis, condition, result, FALSE, FALSE, this::evaluateAndAnd) ||
                tryEvaluate(ifExpression, analysis, condition, result, FALSE, TRUE, this::evaluateAndOrFlipped) ||
                tryEvaluate(ifExpression, analysis, condition, result, TRUE, FALSE, this::evaluateOrAndFlipped) ||
                tryEvaluate(ifExpression, analysis, condition, result, TRUE, TRUE, this::evaluateOrOr) ||
                tryEvaluate(ifExpression, analysis, condition, result, NEXT, FALSE, this::evaluateOrAnd) ||
                tryEvaluate(ifExpression, analysis, condition, result, NEXT, TRUE, this::evaluateAndOr);
    }

    private boolean tryEvaluate(AstContext ifExpression, ShortCircuitAnalysis analysis, LogicList condition,
            LogicVariable result, JumpTarget t0, JumpTarget t1, FullBooleanOptimizer optimizer) {
        List<Jump> jumps = analysis.jumps();
        return jumps.size() == 3
                && jumps.get(0).target() == t0
                && jumps.get(1).target() == t1
                && jumps.get(2).target() == FALSE
                && optimizer.optimize(ifExpression, condition, analysis, result);
    }

    private void replaceExpression(AstContext ifExpression, LogicList condition, OpInstruction op1, OpInstruction op2) {
        AstContext targetContext = requireNonNull(ifExpression.parent()).createChild(ifExpression.existingNode(), OPERATOR);
        LogicList instructions = createLogicList(targetContext);
        LogicList duplicated = condition.duplicateToContext(targetContext, false);

        for (LogicInstruction ix : duplicated) {
            if (ix instanceof JumpInstruction) {
                break;
            } else {
                instructions.addKeepingContext(ix);
            }
        }

        instructions.addToContext(op1);
        instructions.addToContext(op2);

        insertInstructions(firstInstructionIndex(ifExpression), instructions);
        removeMatchingInstructions(ix -> ix.belongsTo(ifExpression));
    }

    private boolean evaluateAndAnd(AstContext astContext, LogicList condition, ShortCircuitAnalysis analysis, LogicVariable result) {
        Jump a = analysis.jumps().getFirst();
        Jump b = analysis.jumps().get(1);
        Jump c = analysis.jumps().get(2);

        if (a.comparesToFalse() && b.comparesToFalse() && c.comparesToFalse()) {
            LogicVariable tmp = instructionProcessor.nextTemp();
            replaceExpression(astContext, condition,
                    createOp(astContext, Operation.LOGICAL_AND, tmp, a.variable(), b.variable()),
                    createOp(astContext, Operation.LOGICAL_AND, result, tmp, c.variable())
            );
            return true;
        }
        return false;
    }

    private boolean evaluateAndOrFlipped(AstContext astContext, LogicList condition, ShortCircuitAnalysis analysis, LogicVariable result) {
        Jump a = analysis.jumps().getFirst();
        Jump b = analysis.jumps().get(1);
        Jump c = analysis.jumps().get(2);

        if (a.comparesToFalse() && b.comparesToTrue() && c.comparesToFalse()) {
            LogicVariable tmp = instructionProcessor.nextTemp();
            replaceExpression(astContext, condition,
                    createOp(astContext, Operation.LOGICAL_OR, tmp, b.variable(), c.variable()),
                    createOp(astContext, Operation.LOGICAL_AND, result, tmp, a.variable())
            );
            return true;
        }
        return false;
    }

    private boolean evaluateOrAndFlipped(AstContext astContext, LogicList condition, ShortCircuitAnalysis analysis, LogicVariable result) {
        Jump a = analysis.jumps().getFirst();
        Jump b = analysis.jumps().get(1);
        Jump c = analysis.jumps().get(2);

        if (a.comparesToTrue() && b.comparesToFalse() && c.comparesToFalse()) {
            LogicVariable tmp = instructionProcessor.nextTemp();
            replaceExpression(astContext, condition,
                    createOp(astContext, Operation.LOGICAL_AND, tmp, b.variable(), c.variable()),
                    createOp(astContext, Operation.LOGICAL_OR, result, tmp, a.variable())
            );
            return true;
        }
        return false;
    }

    private boolean evaluateOrOr(AstContext astContext, LogicList condition, ShortCircuitAnalysis analysis, LogicVariable result) {
        Jump a = analysis.jumps().getFirst();
        Jump b = analysis.jumps().get(1);
        Jump c = analysis.jumps().get(2);

        if (a.comparesToTrue() && b.comparesToTrue() && c.comparesToFalse()) {
            LogicVariable tmp = instructionProcessor.nextTemp();
            replaceExpression(astContext, condition,
                    createOp(astContext, Operation.LOGICAL_OR, tmp, a.variable(), b.variable()),
                    createOp(astContext, Operation.LOGICAL_OR, result, tmp, c.variable())
            );
            return true;
        }
        return false;
    }

    private boolean evaluateOrAnd(AstContext astContext, LogicList condition, ShortCircuitAnalysis analysis, LogicVariable result) {
        Jump a = analysis.jumps().getFirst();
        Jump b = analysis.jumps().get(1);
        Jump c = analysis.jumps().get(2);

        if (a.comparesToTrue() && b.comparesToFalse() && c.comparesToFalse()) {
            LogicVariable tmp = instructionProcessor.nextTemp();
            replaceExpression(astContext, condition,
                    createOp(astContext, Operation.LOGICAL_OR, tmp, a.variable(), b.variable()),
                    createOp(astContext, Operation.LOGICAL_AND, result, tmp, c.variable())
            );
            return true;
        }
        return false;
    }

    private boolean evaluateAndOr(AstContext astContext, LogicList condition, ShortCircuitAnalysis analysis, LogicVariable result) {
        Jump a = analysis.jumps().getFirst();
        Jump b = analysis.jumps().get(1);
        Jump c = analysis.jumps().get(2);

        if (a.comparesToFalse() && b.comparesToTrue() && c.comparesToFalse()) {
            LogicVariable tmp = instructionProcessor.nextTemp();
            replaceExpression(astContext, condition,
                    createOp(astContext, Operation.LOGICAL_AND, tmp, a.variable(), b.variable()),
                    createOp(astContext, Operation.LOGICAL_OR, result, tmp, c.variable())
            );
            return true;
        }
        return false;
    }

    private boolean mapsToBoolean(BranchContent trueContent, BranchContent falseContent) {
        LogicBoolean trueValue = trueContent.getAsBoolean();
        LogicBoolean falseValue = falseContent.getAsBoolean();
        return trueValue == LogicBoolean.TRUE && falseValue == LogicBoolean.FALSE
                || trueValue == LogicBoolean.FALSE && falseValue == LogicBoolean.TRUE;
    }

    private void convertToSelectSequence(AstContext ifExpression, LogicList condition, ShortCircuitAnalysis analysis,
            BranchContent trueContent, BranchContent falseContent, LogicVariable result) {
        AstContext targetContext = requireNonNull(ifExpression.parent()).createChild(ifExpression.existingNode(), OPERATOR);
        LogicList instructions = createLogicList(targetContext);
        LogicList duplicated = condition.duplicateToContext(targetContext, false);

        LogicValue trueValue = requireNonNull(trueContent.assignments.get(result));
        LogicValue falseValue = requireNonNull(falseContent.assignments.get(result));
        LogicValue previous = null;
        LogicLabel trueLabel =  duplicated.getLast() instanceof LabelInstruction lastLabel ? lastLabel.getLabel() : null;

        int jumps = analysis.jumpCount();
        for (LogicInstruction ix : duplicated) {
            if (ix instanceof JumpInstruction jump) {
                jumps--;
                LogicVariable next = jumps > 0 ? instructionProcessor.nextTemp() : result;
                if (analysis.operation() == Operation.LOGICAL_AND) {
                    createSelect(instructions, true, next, jump.getCondition(), jump.getX(), jump.getY(),
                            falseValue, requireNonNullElse(previous, trueValue));
                } else if (jump.getTarget().equals(trueLabel)) {
                    createSelect(instructions, false, next, jump.getCondition(), jump.getX(), jump.getY(),
                            trueValue, requireNonNullElse(previous, falseValue));
                } else {
                    // The last jump of OR
                    createSelect(instructions, true, next, jump.getCondition(), jump.getX(), jump.getY(),
                            requireNonNullElse(previous, falseValue), trueValue);
                }
                previous = next;
            } else {
                instructions.addKeepingContext(ix);
            }
        }

        insertInstructions(firstInstructionIndex(ifExpression), instructions);
        removeMatchingInstructions(ix -> ix.belongsTo(ifExpression));
    }

    private void createSelect(LogicList instructions, boolean swap, LogicVariable result, Condition condition, LogicValue x, LogicValue y,
            LogicValue valueIfTrue, LogicValue valueIfFalse) {
        if (condition == Condition.STRICT_NOT_EQUAL || swap && condition != Condition.STRICT_EQUAL) {
            instructions.createSelect(result, condition.inverse(getGlobalProfile()), x, y, valueIfFalse, valueIfTrue);
        } else {
            instructions.createSelect(result, condition, x, y, valueIfTrue, valueIfFalse);
        }
    }

    private void convertToFullEvaluation(AstContext conditionContext, LogicList condition, ShortCircuitAnalysis analysis) {
        AstContext targetContext = conditionContext.createChild(conditionContext.existingNode(), OPERATOR);
        LogicList instructions = createLogicList(targetContext);
        LogicList duplicated = condition.duplicateToContext(targetContext, false);

        List<LogicValue> values = new ArrayList<>();
        LogicLabel falseLabel = null;
        for (LogicInstruction ix : duplicated) {
            if (ix instanceof JumpInstruction jump) {
                // We know we can ignore unconditional jumps
                if (jump.isConditional()) {
                    values.add(jump.getX().equals(LogicBoolean.FALSE) ? jump.getY() : jump.getX());
                }

                // The last jump necessarily targets the false branch
                falseLabel = jump.getTarget();
            } else {
                instructions.addKeepingContext(ix);
            }
        }

        LogicValue last = values.removeFirst();
        while (!values.isEmpty()) {
            LogicVariable temp = instructionProcessor.nextTemp();
            instructions.createOp(analysis.operation(), temp, last, values.removeFirst());
            last = temp;
        }

        assert falseLabel != null;
        instructions.createJump(falseLabel, Condition.EQUAL, last, LogicBoolean.FALSE);

        int insertIndex = firstInstructionIndex(conditionContext);
        removeMatchingInstructions(ix -> ix.belongsTo(conditionContext));
        insertInstructions(insertIndex, instructions);
    }

    private boolean convertLastJump(ShortCircuitAnalysis analysis,
            LogicList condition, LogicList trueBranch, LogicList falseBranch,
            BranchContent trueContent, BranchContent falseContent, LogicVariable result) {

        AstContext conditionContext = condition.getExistingAstContext();
        if (!optimizationContext.isShortCircuitCondition(conditionContext)) {
            throw new MindcodeInternalError("Expected short circuit condition");
        }

        final LogicList targetBranch = analysis.operation() == Operation.LOGICAL_AND ? trueBranch : falseBranch;
        AstContext targetContext = targetBranch.getExistingAstContext();
        LogicList instructions = createLogicList(targetContext);

        int index = findJumpFromEnd(condition, 2);

        // We need to make the new final jump target the false branch
        JumpInstruction finalJump = (JumpInstruction) condition.getExisting(index);
        if (!finalJump.getTarget().equals(analysis.lastJump().getTarget())) {
            replaceInstruction(finalJump, createJump(finalJump.getAstContext(),
                    analysis.lastJump().getTarget(), finalJump.getCondition().inverse(getGlobalProfile()), finalJump.getX(), finalJump.getY()));
        }

        LogicList transformed = condition.duplicateToContext(targetContext, false);
        int size = 0;
        for (int i = index + 1; i < transformed.size(); i++) {
            size++;
            LogicInstruction ix = transformed.getExisting(i);
            if (ix instanceof JumpInstruction jumpIx) {
                boolean swapValues = jumpIx.getCondition() != Condition.STRICT_NOT_EQUAL;
                Condition newCondition = swapValues ? jumpIx.getCondition() : Condition.STRICT_EQUAL;
                LogicValue trueValue = trueContent.assignments.getOrDefault(result, result);
                LogicValue falseValue = falseContent.assignments.getOrDefault(result, result);
                LogicValue t = swapValues ? falseValue : trueValue;
                LogicValue f = swapValues ? trueValue : falseValue;
                if (t.isOne() && f.isZero() || t.isZero() && f.isOne() && newCondition.hasInverse(false)) {
                    Condition adjusted = t.isZero() ? newCondition.inverse(false) : newCondition;
                    instructions.createOp(adjusted.toOperation(), result, jumpIx.getX(), jumpIx.getY());
                } else if (hasSelect) {
                    instructions.createSelect(result, newCondition, jumpIx.getX(), jumpIx.getY(), t, f);
                } else {
                    return false;
                }
                break;
            }
            instructions.addToContext(ix);
        }

        // Remove the unused part of the condition
        int start = instructionIndex(condition.getExisting(index + 1));
        while (size-- > 0) removeInstruction(start);

        // Replace the target branch with the new content
        int branchIndex = instructionIndex(targetBranch.getExisting(0));
        for (int i = 0; i < targetBranch.size(); i++) removeInstruction(branchIndex);

        insertInstructions(branchIndex, instructions);
        return true;
    }

    private int findJumpFromEnd(LogicList condition, int index) {
        int current = condition.size();
        while (--current >= 0) {
            if (condition.get(current) instanceof JumpInstruction && --index <= 0) {
                return current;
            }
        }
        throw new MindcodeInternalError("Expected two jumps or more");
    }

    private boolean optimizeFullEvaluation(AstContext ifExpression, LogicList condition, BranchContent trueContent,
            BranchContent falseContent, SequencedSet<LogicVariable> results, JumpInstruction jump, ConditionJumpHandling jumpHandling) {
        AstContext targetContext = requireNonNull(ifExpression.parent()).createChild(ifExpression.existingNode(), OPERATOR);
        int index = findJumpFromEnd(condition, 1);
        LogicList instructions = condition.subList(0, index).duplicateToContext(targetContext, false);
        if (condition.getLast() instanceof LabelInstruction label) {
            instructions.addToContext(label);
        }

        JumpInstruction invertedJump = negateCompoundCondition(condition);
        if (invertedJump != null && analyzeJump(invertedJump, trueContent, falseContent) == ConditionJumpHandling.NONE) {
            instructions.removeLast();  // This is the op instruction
            return replaceIfStatement(ifExpression, instructions, trueContent, falseContent, results,
                    invertedJump.getCondition(), invertedJump.getX(), invertedJump.getY(),
                    false);
        } else {
            if (jumpHandling == ConditionJumpHandling.FULL) {
                LogicVariable tmp = instructionProcessor.nextTemp();
                instructions.createOp(jump.getCondition().toOperation(), tmp, jump.getX(), jump.getY());
                jump = createJump(jump.getAstContext(), jump.getTarget(), Condition.NOT_EQUAL, tmp, LogicBoolean.FALSE);
            }

            if (jump.getCondition().hasInverse(getGlobalProfile())) {
                // The compiler inverts the if condition because the jump leads to the false branch
                // Here we invert it back so that true and false values remain the same
                return replaceIfStatement(ifExpression, instructions, trueContent, falseContent, results,
                        jump.getCondition().inverse(getGlobalProfile()), jump.getX(), jump.getY(),
                        false);
            } else {
                // The compiler inverts the if condition because the jump leads to the false branch
                // Here we cannot invert it back, so the true and false values are swapped
                return replaceIfStatement(ifExpression, instructions, trueContent, falseContent, results,
                        jump.getCondition(), jump.getX(), jump.getY(),
                        true);
            }
        }
    }

    private boolean replaceIfStatement(AstContext ifExpression, LogicList instructions, BranchContent trueContent, BranchContent falseContent,
            Set<LogicVariable> results, Condition condition, LogicValue x, LogicValue y, boolean swapValues) {
        int falseIndex = 0;
        Set<LogicVariable> resolved = new HashSet<>();
        Set<LogicVariable> falseWrites = new HashSet<>();

        for (LogicResultInstruction instruction : trueContent.instructions) {
            LogicVariable result = instruction.getResult();

            Set<LogicVariable> dependencies = new HashSet<>(trueContent.dependencies.getOrDefault(result, Set.of()));
            dependencies.retainAll(falseContent.dependencies.keySet());
            if (falseContent.results.contains(result)) dependencies.add(result);

            while (!falseWrites.containsAll(dependencies)) {
                if (falseIndex >= falseContent.instructions.size()) {
                    throw new MindcodeInternalError("Error syncing false branch");
                }
                LogicResultInstruction instruction2 = falseContent.instructions.get(falseIndex++);
                encapsulateResult(instructions, instruction2, results, falseContent);
                if (!result.equals(instruction2.getResult())) {
                    addInstruction(instructions, instruction2, results, resolved, trueContent, falseContent, condition, x, y, swapValues);
                }
                falseWrites.add(instruction2.getResult());
            }

            encapsulateResult(instructions, instruction, results, trueContent);
            addInstruction(instructions, instruction, results, resolved, trueContent, falseContent, condition, x, y, swapValues);
        }

        while (falseIndex < falseContent.instructions.size()) {
            LogicResultInstruction instruction = falseContent.instructions.get(falseIndex++);
            addInstruction(instructions, instruction, results, resolved, trueContent, falseContent, condition, x, y, swapValues);
        }

        if (hasSelect || instructions.stream().noneMatch(ix -> ix.getOpcode() == Opcode.SELECT)) {
            int insertionIndex = firstInstructionIndex(ifExpression);
            removeMatchingInstructions(ix -> ix.belongsTo(ifExpression));
            insertInstructions(insertionIndex, instructions);
            return true;
        } else {
            return false;
        }
    }

    private void encapsulateResult(LogicList instructions, LogicResultInstruction instruction, Set<LogicVariable> results,
            BranchContent content) {
        LogicVariable result = instruction.getResult();
        if (results.contains(result) && !(instruction instanceof SetInstruction)) {
            LogicVariable tmp = instructionProcessor.nextTemp();
            instructions.addToContext(instruction.withResult(tmp));
            content.assignments.put(result, tmp);
        }
    }

    private void addInstruction(LogicList instructions, LogicResultInstruction instruction, Set<LogicVariable> results,
            Set<LogicVariable> resolved, BranchContent trueContent, BranchContent falseContent,
            Condition condition, LogicValue x, LogicValue y, boolean swapValues) {
        LogicVariable result = instruction.getResult();
        if (resolved.contains(result)) return;

        if (results.contains(result)) {
            LogicValue trueValue = trueContent.assignments.getOrDefault(result, result);
            LogicValue falseValue = falseContent.assignments.getOrDefault(result, result);
            LogicValue t = swapValues ? falseValue : trueValue;
            LogicValue f = swapValues ? trueValue : falseValue;

            if (t.isOne() && f.isZero() || t.isZero() && f.isOne() && condition.hasInverse(false)) {
                Condition adjusted = t.isZero() ? condition.inverse(false) : condition;
                instructions.createOp(adjusted.toOperation(), result, x, y);
            } else {
                instructions.createSelect(result, condition, x, y, t, f);
            }
            resolved.add(result);
        } else {
            instructions.addToContext(instruction);
        }
    }

    private BranchContent analyzeBranchContent(AstContext ifExpression, LogicList branch) {
        Set<LogicVariable> reads = new HashSet<>();
        Map<LogicVariable, LogicValue> assignments = new HashMap<>();
        Map<LogicVariable, Set<LogicVariable>> dependencies = new LinkedHashMap<>();
        List<LogicResultInstruction> instructions = new ArrayList<>();

        for (LogicInstruction instruction : branch) {
            if (instruction instanceof LogicResultInstruction ix) {
                if (ix instanceof SetInstruction set) {
                    assignments.put(set.getResult(), set.getValue());
                }

                instructions.add(ix);
                List<LogicVariable> vars = ix.getAllReads().filter(LogicVariable.class::isInstance).map(LogicVariable.class::cast).toList();
                reads.addAll(vars);
                if (reads.contains(ix.getResult())) {
                    // Modifying a variable that was already read or self-referential update --> nonlinear code
                    return INVALID_CONTENT;
                }

                Set<LogicVariable> indirect = new HashSet<>(vars);
                vars.forEach(v -> indirect.addAll(dependencies.getOrDefault(v, Set.of())));
                if (dependencies.put(ix.getResult(), indirect) != null) {
                    // We can't handle variable redefinition in a branch
                    return INVALID_CONTENT;
                }
            } else if (instruction.isReal()) {
                // Any other real instruction cannot be handled
                return INVALID_CONTENT;
            }
        }

        List<LogicVariable> results = dependencies.keySet().stream()
                .filter(variable -> variable.isVolatile() || optimizationContext.isUsedOutsideOf(variable, ifExpression))
                .toList();

        return new BranchContent(assignments, dependencies, results, instructions);
    }

    private ConditionJumpHandling analyzeJump(JumpInstruction jump, BranchContent trueContent, BranchContent falseContent) {
        Set<LogicVariable> modifications = new HashSet<>(trueContent.dependencies.keySet());
        modifications.addAll(falseContent.dependencies.keySet());

        LogicVariable x = jump.getX() instanceof LogicVariable v && modifications.contains(v) ? v : null;
        LogicVariable y = jump.getY() instanceof LogicVariable v && modifications.contains(v) ? v : null;

        if (x == null && y == null) return ConditionJumpHandling.NONE;
        if (x != null && y != null) return ConditionJumpHandling.FULL;

        // Just one of them
        LogicVariable variable = x == null ? y : x;

        // We need to move v to the last position in the list; if anything depends on it, it's not possible
        if (trueContent.hasDependents(variable) || falseContent.hasDependents(variable)) return ConditionJumpHandling.FULL;

        // Move it to the end in both branches
        trueContent.moveToEnd(variable);
        falseContent.moveToEnd(variable);
        return ConditionJumpHandling.NONE;
    }

    private enum ConditionJumpHandling {
        /// No handling needed.
        NONE,
        /// The condition value needs to be stored separately, as the statement body/bodies modify variables
        /// used by the condition.
        FULL,
    }

    private static final BranchContent INVALID_CONTENT = new BranchContent();

    private record BranchContent(boolean valid,
                                 Map<LogicVariable, LogicValue> assignments,
                                 Map<LogicVariable, Set<LogicVariable>> dependencies,
                                 List<LogicVariable> results,
                                 List<LogicResultInstruction> instructions) {

        private BranchContent() {
            this(false, Map.of(), Map.of(), List.of(), List.of());
        }

        private BranchContent(Map<LogicVariable, LogicValue> assignments, Map<LogicVariable, Set<LogicVariable>> dependencies,
                List<LogicVariable> results, List<LogicResultInstruction> instructions) {
            this(true, assignments, dependencies, results, instructions);
        }

        int size() {
            return instructions.size();
        }

        boolean invalid() {
            return !valid;
        }

        boolean isEmpty() {
            return instructions().isEmpty();
        }

        int numOperations() {
            return instructions.size() - assignments.size();
        }

        boolean hasSingleAssignment() {
            return assignments.size() == 1 && instructions.size() == 1;
        }

        @Nullable LogicBoolean getAsBoolean() {
            if (assignments.size() == 1) {
                LogicValue value = assignments.values().iterator().next();
                if (value instanceof LogicBoolean booleanValue) return booleanValue;
                if (value.isNumericConstant() && (value.getIntValue() == 0 || value.getIntValue() == 1)) {
                    return LogicBoolean.get(value.getIntValue() == 1);
                }
            }
            return null;
        }

        boolean hasDependents(LogicVariable variable) {
            return dependencies.values().stream().anyMatch(deps -> deps.contains(variable));
        }

        void moveToEnd(LogicVariable variable) {
            int index = CollectionUtils.indexOf(instructions, ix -> ix.getResult().equals(variable));
            if (index >= 0) {
                LogicResultInstruction instruction = instructions.remove(index);
                instructions.add(instruction);
            }
        }

        @Override
        public String toString() {
            Collector<CharSequence, ?, String> newLines = Collectors.joining("\n        ");
            Collector<CharSequence, ?, String> commas = Collectors.joining(", ");
            return """
                    Branch content:
                        assignments:
                            %s
                        dependencies:
                            %s
                        results:
                            %s
                        instructions:
                            %s
                    """.formatted(
                            assignments.entrySet().stream().map(e -> e.getKey().toMlog() + " = " + e.getValue().toMlog()).collect(newLines),
                            dependencies.entrySet().stream().map(e -> e.getKey().toMlog() + ": [" + e.getValue().stream().map(info.teksol.mc.mindcode.logic.arguments.LogicVariable::toMlog)
                                    .collect(commas) + "]").collect(newLines),
                            results.stream().map(info.teksol.mc.mindcode.logic.arguments.LogicVariable::toMlog).collect(commas),
                            instructions.stream().map(Object::toString).collect(newLines));
        }
    }
}
