package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.emulator.MindustryVariable;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.InstructionCounter;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstForEachLoopStatement;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.compiler.astcontext.AstContextType.EACH;
import static info.teksol.mc.mindcode.compiler.astcontext.AstContextType.LOOP;
import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;
import static info.teksol.mc.util.CollectionUtils.resultIn;
import static java.util.Objects.requireNonNull;

/// Unrolls loops
@NullMarked
class LoopUnroller extends BaseOptimizer {
    public LoopUnroller(OptimizationContext optimizationContext) {
        super(Optimization.LOOP_UNROLLING, optimizationContext);
    }

    private int invocations = 0;
    private int count = 0;

    @Override
    public void generateFinalMessages() {
        iterations = invocations;
        super.generateFinalMessages();
        if (count > 0) {
            emitMessage(MessageLevel.INFO, "%6d loops unrolled by %s.", count, getName());
        }
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        return false;
    }

    @Override
    public List<OptimizationAction> getPossibleOptimizations(int costLimit) {
        invocations++;
        return forEachContext(c -> c.matches(LOOP, EACH) && c.matches(BASIC),
                loop -> findPossibleUnrolling(loop, costLimit));
    }

    private static final Map<AstSubcontextType, String> SYMBOL_MAP = Map.of(
            INIT, "i",
            CONDITION, "c",
            BODY, "b",
            UPDATE, "u",
            FLOW_CONTROL, "f",
            ITR_LEADING, "l",
            ITR_TRAILING, "t"
    );

    private boolean hasSupportedStructure(AstContext loop) {
        String structure = loop.children().stream().map(c -> SYMBOL_MAP.get(c.subcontextType())).collect(Collectors.joining());
        return structure.contains("b") && structure.matches("i?c?b?u?c?f");
    }

    private boolean hasSupportedIterationStructure(AstContext loop) {
        String structure = loop.children().stream().map(c -> SYMBOL_MAP.get(c.subcontextType())).collect(Collectors.joining());
        return structure.matches("l(tl)*bft");
    }

    private boolean hasEntryCondition(AstContext loop) {
        List<AstContext> children = loop.children();
        return (children.size() >= 2) && (children.get(0).subcontextType() == CONDITION
                || children.get(0).subcontextType() == INIT && children.get(1).subcontextType() == CONDITION);
    }

    private boolean hasExitCondition(AstContext loop) {
        for (int i = loop.children().size() - 1; i >= 0; i--) {
            switch (loop.child(i).subcontextType()) {
                case CONDITION: return true;
                case BODY, UPDATE: return false;
            }
        }
        err();
        return false;
    }

    // Remove the last jump of condition context if present (it might have been removed by optimizations).
    private LogicList removeFinalJump(LogicList condition) {
        if (condition.getExistingAstContext().subcontextType() != CONDITION) {
            throw new MindcodeInternalError("Invalid loop structure.");
        }
        return condition.getLast() instanceof JumpInstruction ? condition.subList(0, condition.size() - 1) : condition;
    }

    // Creates a list of LogicLists containing instructions that are part of the loop initialization
    private List<LogicList> initInstructionContexts(AstContext loop) {
        List<LogicList> result = new ArrayList<>();
        LogicList init = contextInstructions(loop.findSubcontext(INIT));
        if (!init.isEmpty()) {
            result.add(init);
        }

        List<AstContext> conditions = loop.findSubcontexts(CONDITION);
        if (conditions.size() == 2) {
            LogicList condition = removeFinalJump(contextInstructions(conditions.getFirst()));
            if (!condition.isEmpty()) {
                result.add(condition);
            }
        }

        return result;
    }

    // Creates a list of LogicLists containing instructions that are part of the loop iterations
    private List<LogicList> iterationInstructionContexts(AstContext loop) {
        List<LogicList> result = loop.children().stream()
                .filter(resultIn(AstContext::subcontextType, CONDITION, BODY, UPDATE))
                .map(this::contextInstructions)
                .collect(Collectors.toCollection(ArrayList::new));

        if (result.size() > 1 && result.getLast().getExistingAstContext().subcontextType() == CONDITION) {
            // If there are two condition contexts, only keep the second one
            if (result.getFirst().getExistingAstContext().subcontextType() == CONDITION) {
                result.removeFirst();
            }
            result.set(result.size() - 1, removeFinalJump(result.getLast()));
        } else {
            result.set(0, removeFinalJump(result.getFirst()));
        }

        return result;
    }

    private @Nullable OptimizationAction findPossibleUnrolling(AstContext loop, int costLimit) {
        if (loop.findSubcontext(ITR_LEADING) != null) {
            return findPossibleIterationUnrolling(loop, costLimit);
        }

        if (!hasSupportedStructure(loop)) {
            return null;
        }

        boolean entryCondition = hasEntryCondition(loop);
        if (entryCondition) {
            LogicList condition = contextInstructions(loop.findSubcontext(CONDITION));
            if (condition.getLast() instanceof JumpInstruction jump && evaluateLoopConditionJump(jump, loop) != LogicBoolean.FALSE) {
                // The loop is not known to execute at least once
                return null;
            }
        }

        AstContext init = loop.findSubcontext(INIT);
        List<LogicList> iterationContexts = iterationInstructionContexts(loop);
        LogicList condition = contextInstructions(loop.findLastSubcontext(CONDITION));

        // Last jump in condition should contain loop control variable
        if (condition.getLast() instanceof JumpInstruction jump) {
            LogicVariable controlVariable = findLoopControl(loop, init, jump);
            var variables = optimizationContext.getLoopVariables(loop);
            var initialValue = variables == null ? null : variables.findVariableValue(controlVariable);
            if (initialValue != null && initialValue.getConstantValue() instanceof LogicLiteral initLiteral && initLiteral.isNumericLiteral()) {
                assert controlVariable != null;

                // List of instructions that will be duplicated
                List<LogicInstruction> loopIxs = iterationContexts.stream().flatMap(LogicList::stream).toList();
                List<LogicInstruction> controlIxs = loopIxs.stream().filter(ix -> ix.usesAsOutput(controlVariable)).toList();

                // Real size of one unrolled iteration. We ignore loop control updates (jump is already removed)
                // Internal array access with control variable as an index is considered.
                // Loop control updates will only be removed by Data Flow Optimization later on.
                int size = InstructionCounter.localSize(loopIxs, ix -> getReplacementSize(ix, controlVariable))
                        + unhoistingSize(loop)
                        - InstructionCounter.localSize(controlIxs.stream());
                int originalSize = InstructionCounter.localSize(contextStream(loop));

                int loopLimit = size <= 0 ? costLimit : costLimit / size;
                int loops = findLoopCount(loop, jump, controlVariable, initLiteral, controlIxs, loopLimit);
                if (loops > 0 && loops <= loopLimit) {
                    // Compute benefit: unrolling avoids all control variable updates and the condition jump
                    double weight = controlIxs.stream().mapToDouble(ix -> ix.getAstContext().totalWeight()).sum() + jump.getAstContext().totalWeight();
                    return new UnrollLoopAction(loop, loops * size - originalSize, loops * weight, loops, controlVariable);
                }
            }
        }

        return null;
    }

    private int unhoistingSize(AstContext loop) {
        // Find all hoisted SETADDRs
        Map<LogicLabel, LogicInstruction> hoistedMap = instructionStream().filter(LogicInstruction::isHoisted)
                .collect(Collectors.toMap(LogicInstruction::getHoistId, ix -> ix));

        List<LogicInstruction> hoisted = contextStream(loop)
                .filter(ix -> !ix.isHoisted())
                .map(ix -> hoistedMap.get(ix.getHoistId()))
                .filter(Objects::nonNull)
                .toList();

        return InstructionCounter.localSize(hoisted);
    }

    private int getReplacementSize(LogicInstruction instruction, LogicVariable controlVariable) {
        return instruction instanceof ArrayAccessInstruction ix && ix.getIndex().equals(controlVariable)
                ? 1
                : instruction.getRealSize(null);
    }

    private int findLoopCount(AstContext loop, JumpInstruction jump, LogicVariable loopControl,
            LogicLiteral initLiteral, List<LogicInstruction> controlIxs, int loopLimit) {

        int result = findLoopCountBasic(loop, jump, loopControl, initLiteral, controlIxs);
        return (result <= 0 && advanced())
                ? findLoopCountAdvanced(loop, jump, loopControl, initLiteral, controlIxs, loopLimit)
                : result;
    }

    private int findLoopCountBasic(AstContext loop, JumpInstruction jump, LogicVariable loopControl,
            LogicLiteral initLiteral, List<LogicInstruction> controlIxs) {

        if (controlIxs.stream().allMatch(ix -> ix instanceof OpInstruction op
                && (op.getOperation() == Operation.ADD || op.getOperation() == Operation.SUB)
                && op.getX().equals(loopControl) && op.getY().isNumericLiteral())) {

            boolean weakInequality;
            switch (jump.getCondition()) {
                case LESS_THAN, GREATER_THAN:       weakInequality = false; break;
                case LESS_THAN_EQ, GREATER_THAN_EQ: weakInequality = true; break;
                default:                            return -1;  // Nothing else is supported
            }

            LogicLiteral endValue = findArgumentValue(jump, loopControl);
            if (endValue == null) {
                return -1;
            }

            double start = initLiteral.getDoubleValue();
            double end = endValue.getDoubleValue();
            double step = controlIxs.stream()
                    .map(OpInstruction.class::cast)
                    .mapToDouble(ix -> (ix.getOperation() == Operation.ADD ? 1d : -1d) * ix.getY().getDoubleValue())
                    .sum();

            if (isInt(start) && isInt(end) && isInt(step)) {
                int intDiff = (int) end - (int) start;
                int intStep = (int) step;
                if (step == 0 || (step < 0 ^ intDiff < 0)) {
                    // Possibly empty or infinite loop
                    return -1;
                }

                // When there's an exit condition, it is used to evaluate the loop:
                //     in this case, weak inequality means one additional iteration
                // When there isn't an exit condition, the entry condition is used to evaluate the loop:
                //     when the entry condition is true, the loop body is NOT executed (it jumps around the loop)
                //     in this case, strong inequality means one additional iteration
                boolean additionalLoop = weakInequality ^ !hasExitCondition(loop);

                // Number of steps to reach end
                return (Math.abs(intDiff) + Math.abs(intStep) - 1) / Math.abs(intStep) + ((additionalLoop && (intDiff % intStep == 0)) ? 1 : 0);
            }
        }
        return -1;
    }

    private @Nullable LogicLiteral findArgumentValue(JumpInstruction jump, LogicVariable loopControl) {
        LogicValue argument = jump.getX().equals(loopControl) ? jump.getY() : jump.getX();
        LogicValue resolved = optimizationContext.resolveValue(getVariableStates(jump), argument);
        return resolved instanceof LogicLiteral literal && literal.isNumericLiteral() ? literal : null;
    }

    private boolean isInt(double value) {
        return value == (int) value;
    }

    private int findLoopCountAdvanced(AstContext loop, JumpInstruction jump, LogicVariable loopControl,
            LogicLiteral initLiteral, List<LogicInstruction> controlIxs, int loopLimit) {
        MindustryVariable controlVariable = MindustryVariable.createVar(loopControl.toMlog());
        controlVariable.setDoubleValue(initLiteral.getDoubleValue());
        LogicBoolean terminatingValue = LogicBoolean.get(!hasExitCondition(loop));

        for (int loops = 1; loops <= loopLimit; loops++) {
            LogicLiteral result = null;
            // Update control variable
            for (LogicInstruction ix : controlIxs) {
                OpInstruction op = (OpInstruction) ix;
                result = op.getX().equals(loopControl)
                        ? evaluate(op.sourcePosition(), op.getOperation(), controlVariable, op.getY())
                        : evaluate(op.sourcePosition(), op.getOperation(), op.getX(), controlVariable);
                assert result != null;
                controlVariable.setDoubleValue(result.getDoubleValue());
            }

            requireNonNull(result);

            // TODO Awfully ineffective. Evaluate condition without duplicating and evaluating jump
            JumpInstruction test = replaceAllArgs(jump, loopControl, result);
            if (evaluateJumpInstruction(test) == terminatingValue) {
                return loops;
            }
        }

        return -1;
    }

    // Finds the control variable of this loop
    private @Nullable LogicVariable findLoopControl(AstContext loop, @Nullable AstContext init, JumpInstruction jump) {
        if (jump.isUnconditional()) {
            return null;    // There isn't any loop control variable
        }

        LogicVariable result = null;
        for (LogicValue operand : jump.getOperands()) {
            if (operand instanceof LogicVariable variable && !variable.isGlobalVariable()) {
                // All modifications of this operand outside the init context
                List<LogicInstruction> controlIxs = getControlVariableUpdates(loop, init, variable);
                if (!controlIxs.isEmpty()) {
                    if (controlIxs.stream().allMatch(ix -> ix instanceof OpInstruction op
                            && loop.executesOnce(op)
                            && op.getResult().equals(variable) && op.hasSecondOperand()
                            && (op.getX().equals(variable) && op.getY().isNumericLiteral()
                            || op.getY().equals(variable) && op.getX().isNumericLiteral())
                    )) {
                        if (result != null) {
                            // Both operands are modified inside loop: cannot unroll
                            return null;
                        }
                        result = variable;
                    } else {
                        // This variable is modified in an incompatible way: cannot unroll
                        return null;
                    }
                }
            }
        }
        return result;
    }

    private @NonNull ArrayList<LogicInstruction> getControlVariableUpdates(AstContext loop, @Nullable AstContext init, LogicVariable variable) {
        return contextStream(loop)
                .filter(ix -> !ix.getAstContext().belongsTo(init))
                .filter(ix -> ix.usesAsOutput(variable))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private int computeSavedInstructions(AstContext loop, int iterations) {
        if (loop.node() instanceof AstForEachLoopStatement forEachLoop) {
            // Assignments potentially saved
            int assignments = forEachLoop.getIteratorGroups().stream()
                    .flatMap(s -> s.getIterators().stream())
                    .mapToInt(it -> it.hasOutModifier() ? 2 : 1).sum();

            // Instructions saved per loop:
            // Set address
            // Set iterator(s)
            // Jump to body (except the last iteration)
            // Go to next iteration
            // Read out iterator(s)
            return iterations * (assignments + 3) - 1;
        } else {
            throw new MindcodeInternalError("Expected AstForEachLoopStatement, got " + loop);
        }
    }

    private @Nullable OptimizationAction findPossibleIterationUnrolling(AstContext loop, int costLimit) {
        if (!hasSupportedIterationStructure(loop)) {
            return null;
        }

        List<AstContext> leading = loop.findSubcontexts(ITR_LEADING);
        LogicList body = contextInstructions(loop.findSubcontext(BODY));

        int loops = leading.size();
        int savings = computeSavedInstructions(loop, loops) - unhoistingSize(loop);
        int size = body.realSize();

        // Optimization cost could actually get negative
        int cost = Math.max(size * loops - savings, 0);
        return cost > costLimit ? null : new UnrollListIterationLoopAction(loop, cost, loop.totalWeight() * savings);
    }

    private void unhoistLoop(AstContext loop) {
        // Find all hoisted SETADDRs
        Map<LogicLabel, LogicInstruction> hoistedMap = instructionStream().filter(LogicInstruction::isHoisted)
                .collect(Collectors.toMap(LogicInstruction::getHoistId, ix -> ix));

        try (OptimizationContext.LogicIterator iterator = createIteratorAtContext(loop)) {
            while (iterator.hasNext()) {
                LogicInstruction instruction = iterator.next();
                if (!instruction.belongsTo(loop)) break;
                if (instruction.isHoisted()) continue;

                LogicInstruction hoisted = hoistedMap.remove(instruction.getHoistId());
                if (hoisted != null) {
                    removeInstruction(hoisted);
                    LogicInstruction copy = hoisted.withContext(instruction.getAstContext()).resetHoistId();
                    insertInstruction(iterator.previousIndex(), copy);
                }
            }
        }
    }

    private OptimizationResult unrollLoop(AstContext loop, LogicVariable loopControlVariable, int loops, int costLimit) {
        unhoistLoop(loop);
        List<LogicList> initContexts = initInstructionContexts(loop);
        List<LogicList> iterationContexts = iterationInstructionContexts(loop);

        // Create a new, non-loop context for unrolled instructions
        AstContext newContext = loop.existingParent().createChild(loop.getProfile(), loop.existingNode(), AstContextType.BODY);
        int insertionPoint = firstInstructionIndex(loop);
        for (LogicList list : initContexts) {
            insertInstructions(insertionPoint, list.duplicateToContext(newContext));
            insertionPoint += list.size();
        }

        // Put in copies of the body
        for (int i = 0; i < loops; i++) {
            for (LogicList list : iterationContexts) {
                insertInstructions(insertionPoint, list.duplicateToContext(newContext));
                insertionPoint += list.size();
            }
        }

        unrollFlowControlContext(loop, newContext, insertionPoint);
        return OptimizationResult.REALIZED;
    }

    private Set<LogicVariable> getOutputIteratorVariables(AstContext loop) {
        Set<LogicVariable> leadingVariables = new HashSet<>();
        Set<LogicVariable> trailingVariables = new HashSet<>();
        contextStream(loop)
                .filter(ix -> ix.getAstContext().matches(ITR_LEADING, ITR_TRAILING))
                .forEach(ix -> {
                    if (ix.getAstContext().matches(ITR_LEADING)) {
                        ix.outputArgumentsStream().filter(LogicArgument::isUserVariable).forEach(e -> leadingVariables.add((LogicVariable) e));
                    } else {
                        ix.inputArgumentsStream().filter(LogicArgument::isUserVariable).forEach(e -> trailingVariables.add((LogicVariable) e));
                    }
                });
        trailingVariables.retainAll(leadingVariables);
        return trailingVariables;
    }

    private OptimizationResult unrollListLoop(AstContext loop, int costLimit) {
        unhoistLoop(loop);
        List<AstContext> leading = loop.findSubcontexts(ITR_LEADING);
        List<AstContext> trailing = loop.findSubcontexts(ITR_TRAILING);
        if (leading.size() != trailing.size()) {
            throw new MindcodeInternalError("Invalid loop structure.");
        }

        Set<LogicVariable> outputIterators = getOutputIteratorVariables(loop);
        LogicList body = contextInstructions(loop.findSubcontext(BODY));

        // Create a new, non-loop context for unrolled instructions
        AstContext newContext = loop.existingParent().createChild(loop.getProfile(), loop.existingNode(), AstContextType.BODY);
        int insertionPoint = firstInstructionIndex(loop);
        for (int i = 0; i < leading.size(); i++) {
            LogicList leadingCtx = removeLeadingIteratorInstructions(contextInstructions(leading.get(i)));

            insertInstructions(insertionPoint, leadingCtx.duplicateToContext(newContext));
            insertionPoint += leadingCtx.size();

            insertInstructions(insertionPoint, body.duplicateToContext(newContext));
            insertionPoint += body.size();

            LogicList trailingCtx = contextInstructions(trailing.get(i));
            if (!trailingCtx.isEmpty()) {
                // Skips labels/multilabels, copies the rest
                LogicList copy = trailingCtx.transformToContext(newContext,
                        ix -> ix instanceof LabelInstruction || ix instanceof MultiLabelInstruction || ix instanceof ReturnInstruction
                                ? null : ix);
                insertInstructions(insertionPoint, copy);
                insertionPoint += copy.size();
            }
        }

        // Get a copy of the context before removing it
        LogicList labels = contextInstructions(trailing.getLast());

        // Remove all loop instructions
        removeMatchingInstructions(ix -> ix.belongsTo(loop));

        // Put back labels in the last trailing subcontext (contains break label)
        for (LogicInstruction ix : labels) {
            if (ix instanceof LabelInstruction) {
                insertInstruction(insertionPoint++, ix.withContext(newContext));
            }
        }

        count++;

        return OptimizationResult.REALIZED;
    }

    private void err() {
        throw new MindcodeInternalError("Invalid loop structure.");
    }

    private LogicList removeLeadingIteratorInstructions(LogicList list) {
        if (getProfile().isSymbolicLabels()) {
            if (list.getLast() instanceof JumpInstruction jump) {
                if (!jump.isUnconditional() || !(list.getFromEnd(1) instanceof OpInstruction op)
                        || op.getOperation() != Operation.ADD || !op.getX().equals(LogicBuiltIn.COUNTER)) err();
                return list.subList(0, list.size() - 2);
            } else {
                // Last iteration
                if (!(list.getLast() instanceof SetInstruction set) || set.getValue() != LogicNull.NULL) err();
                return list.subList(0, list.size() - 1);
            }
        } else {
            int end = list.getLast() instanceof JumpInstruction ? 1 : 0;
            if (!(list.getFromEnd(end) instanceof SetAddressInstruction)) err();
            return list.subList(0, list.size() - end - 1);
        }
    }

    private void unrollFlowControlContext(AstContext loop, AstContext newContext, int insertionPoint) {
        // Save it before the original loop is erased
        LogicList flowControl = contextInstructions(loop.findSubcontext(FLOW_CONTROL));

        // Remove all loop instructions
        removeMatchingInstructions(ix -> ix.belongsTo(loop));

        // Put back labels in the flowControl (contains break label)
        for (LogicInstruction ix : flowControl) {
            if (ix instanceof LabelInstruction) {
                insertInstruction(insertionPoint++, ix.withContext(newContext));
            }
        }

        count++;
    }

    private class UnrollLoopAction extends AbstractOptimizationAction {
        private final LogicVariable controlVariable;
        private final int loops;

        public UnrollLoopAction(AstContext astContext, int cost, double benefit, int loops, LogicVariable controlVariable) {
            super(astContext, cost, benefit);
            this.loops = loops;
            this.controlVariable = controlVariable;
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            return applyOptimization(() -> unrollLoop(astContext, controlVariable, loops, costLimit), toString());
        }

        @Override
        public String toString() {
            assert astContext.node() != null;
            return "Unroll loop at " + astContext.node().sourcePosition().formatForLog();
        }
    }

    private class UnrollListIterationLoopAction extends AbstractOptimizationAction {
        public UnrollListIterationLoopAction(AstContext astContext, int cost, double benefit) {
            super(astContext, cost, benefit);
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            return applyOptimization(() -> unrollListLoop(astContext(), costLimit), toString());
        }

        @Override
        public String toString() {
            assert astContext.node() != null;
            return "Unroll iteration loop at " + astContext.node().sourcePosition().formatForLog();
        }
    }
}
