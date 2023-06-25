package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.processor.DoubleVariable;
import info.teksol.util.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static info.teksol.mindcode.compiler.instructions.AstSubcontextType.*;

/**
 * Unrolls loops
 */
public class LoopUnroller extends BaseOptimizer {
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
    protected boolean optimizeProgram(OptimizationPhase phase, int pass, int iteration) {
        return false;
    }

    @Override
    public List<OptimizationAction> getPossibleOptimizations(int costLimit) {
        invocations++;
        List<OptimizationAction> actions = new ArrayList<>();
        forEachContext(AstContextType.LOOP, BASIC, loop -> {
            UnrollLoopAction possibleUnrolling = findPossibleUnrolling(loop, costLimit);
            if (possibleUnrolling != null) {
                actions.add(possibleUnrolling);
            }
        });
        return actions;
    }

    private static final Map<AstSubcontextType, String> SYMBOL_MAP = Map.of(
            INIT, "i",
            CONDITION, "c",
            BODY, "b",
            UPDATE, "u",
            FLOW_CONTROL, "f"
    );

    private boolean hasSupportedStructure(AstContext loop) {
        String structure = loop.children().stream().map(c -> SYMBOL_MAP.get(c.subcontextType())).collect(Collectors.joining());
        return structure.contains("b") && structure.matches("i?c?b?u?c?f");
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
                .filter(CollectionUtils.in(AstContext::subcontextType, CONDITION, BODY, UPDATE))
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

    private UnrollLoopAction findPossibleUnrolling(AstContext loop, int costLimit) {
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
            LogicVariable loopControlVar = findLoopControl(loop, init, jump);
            var variables = optimizationContext.getLoopVariables(loop);
            var initialValue = variables == null ? null : variables.findVariableValue(loopControlVar);
            if (initialValue != null && initialValue.getConstantValue() instanceof LogicLiteral initLiteral && initLiteral.isNumericLiteral()) {
                // List of instructions that will be duplicated
                List<LogicInstruction> loopIxs = iterationContexts.stream().flatMap(LogicList::stream).toList();
                List<LogicInstruction> controlIxs = loopIxs.stream().filter(ix -> ix.outputArgumentsStream().anyMatch(a -> a.equals(loopControlVar))).toList();

                // Real size of one unrolled iteration. We ignore loop control updates (jump is already removed)
                // Loop control updates will only be removed by Data Flow Optimization later on.
                int size = loopIxs.stream().mapToInt(LogicInstruction::getRealSize).sum()
                        - controlIxs.stream().mapToInt(LogicInstruction::getRealSize).sum();

                int loopLimit = size <= 0 ? costLimit : costLimit / size;
                int loops = findLoopCount(loop, jump, loopControlVar, initLiteral, controlIxs, loopLimit);
                if (loops > 0 && loops <= loopLimit) {
                    // Compute benefit: unrolling avoids all control variable updates and the condition jump
                    double weight = controlIxs.stream().mapToDouble(ix -> ix.getAstContext().weight()).sum() + jump.getAstContext().weight();
                    return new UnrollLoopAction(loop, loops * size, loops * weight, loops - 1, loopControlVar);
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
        LogicVariable result = null;
        for (LogicValue operand : jump.getOperands()) {
            if (operand instanceof LogicVariable variable && !variable.isGlobalVariable()) {
                // All modifications of this operand outside the init context
                List<LogicInstruction> controlIxs = getControlVariableUpdates(loop, init, variable);
                if (!controlIxs.isEmpty()) {
                    if (controlIxs.stream().allMatch(ix -> ix instanceof OpInstruction op
                            && withinLoopContext(loop, op)
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

    // Determines whether the instruction lies directly within the loop context (not in another control-flow subcontext)
    private boolean withinLoopContext(AstContext loop, OpInstruction op) {
        for (AstContext ctx = op.getAstContext(); ctx != null; ctx = ctx.parent()) {
            if (ctx.contextType().flowControl) {
                return ctx == loop || ctx.parent() == loop;
            }
        }
        return false;
    }

    @Override
    public OptimizationResult applyOptimizationInternal(OptimizationAction optimization, int costLimit) {
        return switch (optimization) {
            case UnrollLoopAction a         -> unrollLoop(a, costLimit);
            default                         -> OptimizationResult.INVALID;
        };
    }

    private OptimizationResult unrollLoop(UnrollLoopAction optimization, int costLimit) {
        AstContext loop = optimization.astContext();
        List<LogicList> initContexts = initInstructionContexts(loop);
        List<LogicList> iterationContexts = iterationInstructionContexts(loop);
        int loops = optimization.codeMultiplication() + 1;

        // Create a new, non-loop context for unrolled instructions
        AstContext newContext = loop.parent().createChild(loop.node(), AstContextType.BODY);
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

        optimizationContext.addUnrolledVariable(optimization.getLoopControl());
        count++;
        return OptimizationResult.REALIZED;
    }

    private class UnrollLoopAction extends AbstractOptimizationAction {
        private final LogicVariable loopControl;

        public UnrollLoopAction(AstContext astContext, int cost, double benefit, int codeMultiplication, LogicVariable loopControl) {
            super(astContext, cost, benefit, codeMultiplication);
            this.loopControl = loopControl;
        }

        public LogicVariable getLoopControl() {
            return loopControl;
        }

        @Override
        public String toString() {
            return getName() + ": unroll loop at line " + astContext.node().startToken().getLine();
        }
    }
}
