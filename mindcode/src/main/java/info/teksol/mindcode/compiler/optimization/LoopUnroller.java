package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.processor.DoubleVariable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static info.teksol.mindcode.compiler.generator.AstSubcontextType.*;
import static info.teksol.util.CollectionUtils.resultIn;

/**
 * Unrolls loops
 */
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
        return forEachContext(AstContextType.LOOP, BASIC,
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

    private boolean hasConditionAtFront(AstContext loop) {
        List<AstContext> children = loop.children();
        return (children.size() >= 2) && (children.get(0).subcontextType() == CONDITION
                || children.get(0).subcontextType() == INIT && children.get(1).subcontextType() == CONDITION);
    }

    // Remove the last jump of condition context if present (it might have been removed by optimizations).
    private LogicList removeFinalJump(LogicList condition) {
        if (condition.getAstContext().subcontextType() != CONDITION) {
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
            LogicList condition = removeFinalJump(contextInstructions(conditions.get(0)));
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

        if (result.size() > 1 && result.get(result.size() - 1).getAstContext().subcontextType() == CONDITION) {
            // If there are two condition contexts, only keep the second one
            if (result.get(0).getAstContext().subcontextType() == CONDITION) {
                result.remove(0);
            }
            result.set(result.size() - 1, removeFinalJump(result.get(result.size() - 1)));
        } else {
            result.set(0, removeFinalJump(result.get(0)));
        }

        return result;
    }

    private OptimizationAction findPossibleUnrolling(AstContext loop, int costLimit) {
        if (loop.findSubcontext(ITR_LEADING) != null) {
            return findPossibleIterationUnrolling(loop, costLimit);
        }

        if (!hasSupportedStructure(loop)) {
            return null;
        }

        if (hasConditionAtFront(loop)) {
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
                // List of instructions that will be duplicated
                List<LogicInstruction> loopIxs = iterationContexts.stream().flatMap(LogicList::stream).toList();
                List<LogicInstruction> controlIxs = loopIxs.stream().filter(ix -> ix.outputArgumentsStream().anyMatch(a -> a.equals(controlVariable))).toList();

                // Real size of one unrolled iteration. We ignore loop control updates (jump is already removed)
                // Loop control updates will only be removed by Data Flow Optimization later on.
                int size = loopIxs.stream().mapToInt(LogicInstruction::getRealSize).sum()
                        - controlIxs.stream().mapToInt(LogicInstruction::getRealSize).sum();
                int originalSize = contextStream(loop).mapToInt(LogicInstruction::getRealSize).sum();

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

    private int findLoopCount(AstContext loop, JumpInstruction jump, LogicVariable loopControl,
            LogicLiteral initLiteral, List<LogicInstruction> controlIxs, int loopLimit) {

        int result = findLoopCountBasic(loop, jump, loopControl, initLiteral, controlIxs);
        return (result <= 0 && aggressive())
                ? findLoopCountAggressive(loop, jump, loopControl, initLiteral, controlIxs, loopLimit)
                : result;
    }

    private int findLoopCountBasic(AstContext loop, JumpInstruction jump, LogicVariable loopControl,
            LogicLiteral initLiteral, List<LogicInstruction> controlIxs) {

        if (controlIxs.stream().allMatch(ix -> ix instanceof OpInstruction op
                && (op.getOperation() == Operation.ADD || op.getOperation() == Operation.SUB)
                && op.getX().equals(loopControl) && op.getY().isNumericLiteral())) {

            boolean orEqual;
            switch (jump.getCondition()) {
                case LESS_THAN, GREATER_THAN:       orEqual = false; break;
                case LESS_THAN_EQ, GREATER_THAN_EQ: orEqual = true; break;
                default:                            return -1;  // Nothing else is supported
            }

            boolean invert = !jump.getX().equals(loopControl);

            LogicLiteral endValue = findArgumentValue(jump, loopControl);
            if (endValue == null) {
                return -1;
            }

            double start = initLiteral.getDoubleValue();
            double end = endValue.getDoubleValue();
            double step = controlIxs.stream()
                    .map(OpInstruction.class::cast)
                    .mapToDouble(ix -> (ix.getOperation() == Operation.ADD ? 1d : -1d) * ((LogicLiteral) ix.getY()).getDoubleValue())
                    .sum();

            if (isInt(start) && isInt(end) &&isInt(step)) {
                int intDiff = (int) end - (int) start;
                int intStep = (int) step;
                if (step == 0 || (step < 0 ^ intDiff < 0)) {
                    // Possibly empty or infinite loop
                    return -1;
                }

                // Number of steps to reach end
                return (Math.abs(intDiff) + Math.abs(intStep) - 1) / Math.abs(intStep) + ((orEqual && (intDiff % intStep == 0)) ? 1 : 0);
            }
        }
        return -1;
    }

    private LogicLiteral findArgumentValue(JumpInstruction jump, LogicVariable loopControl) {
        LogicValue argument = jump.getX().equals(loopControl) ? jump.getY() : jump.getX();
        LogicValue resolved = optimizationContext.resolveValue(getVariableStates(jump), argument);
        return resolved instanceof LogicLiteral literal && literal.isNumericLiteral() ? literal : null;
    }

    private boolean isInt(double value) {
        return value == (int) value;
    }

    private int findLoopCountAggressive(AstContext loop, JumpInstruction jump, LogicVariable loopControl,
            LogicLiteral initLiteral, List<LogicInstruction> controlIxs, int loopLimit) {
        DoubleVariable controlVariable = DoubleVariable.newDoubleValue(false, loopControl.toMlog(),
                initLiteral.getDoubleValue());
        LogicBoolean terminatingValue = LogicBoolean.get(!isTrailingCondition(loop));

        for (int loops = 1; loops <= loopLimit; loops++) {
            LogicLiteral result = null;
            // Update control variable
            for (LogicInstruction ix : controlIxs) {
                OpInstruction op = (OpInstruction) ix;
                result = evaluate(op.getOperation(), controlVariable, (LogicLiteral) op.getY());
                controlVariable.setDoubleValue(result.getDoubleValue());
            }

            // TODO Awfully ineffective. Evaluate condition without duplicating and evaluating jump
            JumpInstruction test = replaceAllArgs(jump, loopControl, result);
            if (evaluateJumpInstruction(test) == terminatingValue) {
                return loops;
            }
        }

        return -1;
    }


    private boolean isTrailingCondition(AstContext loop) {
        for (int i = loop.children().size() - 1; i >= 0; i--) {
            switch (loop.child(i).subcontextType()) {
                case CONDITION: return true;
                case BODY, UPDATE: return false;
            }
        }
        throw new MindcodeInternalError("Invalid loop structure.");
    }

    // Finds the control variable of this loop
    private LogicVariable findLoopControl(AstContext loop, AstContext init, JumpInstruction jump) {
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
                            && op.getResult().equals(variable) && op.getX().equals(variable)
                            && op.hasSecondOperand() && op.getY().isNumericLiteral())) {
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

    @NotNull
    private ArrayList<LogicInstruction> getControlVariableUpdates(AstContext loop, AstContext init, LogicVariable variable) {
        return contextStream(loop)
                .filter(ix -> !ix.getAstContext().belongsTo(init))
                .filter(ix -> ix.outputArgumentsStream().anyMatch(a -> a.equals(variable)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private OptimizationAction findPossibleIterationUnrolling(AstContext loop, int costLimit) {
        if (!hasSupportedIterationStructure(loop)) {
            return null;
        }

        List<AstContext> leading = loop.findSubcontexts(ITR_LEADING);
        LogicList body = contextInstructions(loop.findSubcontext(BODY));

        int loops = leading.size();
        // Instructions saved per loop:
        // Set address
        // Jump to body (except the last iteration)
        // Go to next iteration
        int savings = (3 * loops - 1);

        int size = body.stream().mapToInt(LogicInstruction::getRealSize).sum();

        // Optimization cost could actually get negative
        int cost = Math.max(size * loops - savings, 0);
        return cost > costLimit ? null : new UnrollListIterationLoopAction(loop, cost, loop.totalWeight() * savings);
    }

    private OptimizationResult unrollLoop(AstContext loop, LogicVariable loopControlVariable, int loops, int costLimit) {
        optimizationContext.addUnrolledVariable(loopControlVariable);
        List<LogicList> initContexts = initInstructionContexts(loop);
        List<LogicList> iterationContexts = iterationInstructionContexts(loop);

        // Create a new, non-loop context for unrolled instructions
        AstContext newContext = loop.parent().createChild(loop.getProfile(), loop.node(), AstContextType.BODY);
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

    private OptimizationResult unrollListLoop(AstContext loop, int costLimit) {
        List<AstContext> leading = loop.findSubcontexts(ITR_LEADING);
        List<AstContext> trailing = loop.findSubcontexts(ITR_TRAILING);
        if (leading.size() != trailing.size()) {
            throw new MindcodeInternalError("Invalid loop structure.");
        }

        LogicList body = contextInstructions(loop.findSubcontext(BODY));

        // Create a new, non-loop context for unrolled instructions
        AstContext newContext = loop.parent().createChild(loop.getProfile(), loop.node(), AstContextType.BODY);
        int insertionPoint = firstInstructionIndex(loop);
        for (int i = 0; i < leading.size(); i++) {
            LogicList leadingCtx = removeLeadingIteratorInstructions(contextInstructions(leading.get(i)));
            insertInstructions(insertionPoint, leadingCtx.duplicateToContext(newContext));
            insertionPoint += leadingCtx.size();

            insertInstructions(insertionPoint, body.duplicateToContext(newContext));
            insertionPoint += body.size();

            LogicList trailingCtx = contextInstructions(trailing.get(i));
            if (!trailingCtx.isEmpty()) {
                LogicList copy = trailingCtx.duplicateToContext(newContext,
                        ix -> !(ix instanceof LabelInstruction) && !(ix instanceof GotoLabelInstruction) && !(ix instanceof GotoInstruction));
                insertInstructions(insertionPoint, copy);
                insertionPoint += copy.size();
            }
        }

        // Get a copy of the context before removing it
        LogicList labels = contextInstructions(trailing.get(trailing.size() - 1));

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

    private LogicList removeLeadingIteratorInstructions(LogicList list) {
        int begin = list.getFirst() instanceof SetAddressInstruction ? 1 : 0;
        int end = list.getLast() instanceof JumpInstruction ? 1 : 0;
        return list.subList(begin, list.size() - end);
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
            return getName() + ": unroll loop at line " + astContext.node().startToken().getLine();
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
            return getName() + ": unroll iteration loop at line " + astContext.node().startToken().getLine();
        }
    }
}
