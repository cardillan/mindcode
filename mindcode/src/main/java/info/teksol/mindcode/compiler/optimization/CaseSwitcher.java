package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.instructions.AstContext;
import info.teksol.mindcode.compiler.instructions.AstContextType;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mindcode.logic.*;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

import static info.teksol.mindcode.compiler.instructions.AstSubcontextType.*;

/**
 * Inlines functions
 */
public class CaseSwitcher extends BaseOptimizer {
    // Activates generating range limiting instructions for case switching.
    // Only set to false for the purposes of benchmarking.
    private static final boolean RANGE_LIMITING = true;

    public CaseSwitcher(OptimizationContext optimizationContext) {
        super(Optimization.CASE_SWITCHING, optimizationContext);
    }

    private int invocations = 0;
    private int count = 0;

    @Override
    public void generateFinalMessages() {
        iterations = invocations;
        super.generateFinalMessages();
        if (count > 0) {
            emitMessage(MessageLevel.INFO, "%6d case expressions converted to switched jumps %s.", count, getName());
        }
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase, int pass, int iteration) {
        return false;
    }

    @Override
    public List<OptimizationAction> getPossibleOptimizations(int costLimit) {
        invocations++;
        return forEachContext(AstContextType.CASE, BASIC,
                context -> findPossibleCaseSwitches(context, costLimit));
    }


    private OptimizationAction findPossibleCaseSwitches(AstContext context, int costLimit) {
        LogicVariable variable = null;
        NavigableMap<Integer, LogicLabel> targets = new TreeMap<>();

        if (context.findSubcontext(CONDITION) == null) {
            return null;
        }

        int removed = 0;

        try (LogicIterator iterator = createIteratorAtContext(context)) {
            while (iterator.hasNext()) {
                LogicInstruction ix = iterator.next();
                if (ix.getAstContext().parent() == context && ix.getAstContext().matches(CONDITION)) {
                    if (ix instanceof JumpInstruction jump) {
                        removed++;

                        // NOT_EQUAL might have been created by jump over jump optimization
                        if ((jump.getCondition() == Condition.EQUAL || jump.getCondition() == Condition.NOT_EQUAL)
                                && jump.getX() instanceof LogicVariable var && (variable == null || var.equals(variable))
                                && jump.getY() instanceof LogicNumber lit && lit.isInteger()
                                && getLabelInstruction(jump.getTarget()).getAstContext().parent() == context) {
                            variable = var;
                            LogicLabel target;
                            if (jump.getCondition() == Condition.EQUAL) {
                                target = jump.getTarget();
                            } else {
                                AstContext condition = ix.getAstContext();
                                while (iterator.hasNext() && iterator.peek(0).belongsTo(condition)) {
                                    iterator.next();
                                }
                                AstContext bodyContext = context.nextChild(ix.getAstContext());
                                if (iterator.peek(0).belongsTo(bodyContext)) {
                                    target = obtainContextLabel(bodyContext);
                                } else {
                                    return null;
                                }
                            }

                            if (target == null || targets.put(lit.getIntValue(), target) != null) {
                                return null;
                            }
                        } else if (!jump.isUnconditional()) {
                            // Unconditional jump is a jump to next when branch
                            return null;
                        }
                    } else {
                        // Something different from a jump in condition --> unsupported structure
                        return null;
                    }
                }
            }
        }

        // Cost calculation:
        // Adding fixed three instructions: op min, op max, goto offset
        // Adding one jump for each value between min and max (the jump table)
        // Saving one jump per each jump instruction
        int min = targets.firstEntry().getKey();
        int max = targets.lastEntry().getKey();
        int cost = max - min + 3 - removed;

        // New sequence of executed instructions will be: op min / op max / goto offset / jump
        // We save half of the switch jumps on average
        double benefit = (targets.size() / 2.0 - 4) * context.totalWeight();

        return benefit <= 0 || cost > costLimit ? null
                : new ConvertCaseExpressionAction(context, cost, benefit, variable, targets);
    }

    private OptimizationResult convertCaseExpression(ConvertCaseExpressionAction action, int costLimit) {
        AstContext context = action.astContext;
        NavigableMap<Integer, LogicLabel> targets = action.targets;

        int index = firstInstructionIndex(context.findSubcontext(CONDITION));
        AstContext elseContext = context.findSubcontext(ELSE);
        AstContext finalContext = elseContext != null ? elseContext : context.lastChild();
        LogicLabel finalLabel = obtainContextLabel(finalContext);
        AstContext newContext = context.createSubcontext(FLOW_CONTROL, 1.0);
        LogicVariable jumpValue = instructionProcessor.nextTemp();
        int min = targets.firstEntry().getKey() - 1;
        int max = targets.lastEntry().getKey() + 1;

        LogicLabel marker = instructionProcessor.nextLabel();
        List<LogicLabel> labels = IntStream.rangeClosed(min, max).mapToObj(i -> instructionProcessor.nextLabel()).toList();

        if (RANGE_LIMITING) {
            insertInstruction(index++, createOp(newContext, Operation.MIN, jumpValue, action.variable, LogicNumber.get(max)));
            insertInstruction(index++, createOp(newContext, Operation.MAX, jumpValue, jumpValue, LogicNumber.get(min)));
            insertInstruction(index++, createGotoOffset(newContext, labels.get(0), jumpValue, LogicNumber.get(min), marker));
        } else {
            insertInstruction(index++, createGotoOffset(newContext, labels.get(0), action.variable, LogicNumber.get(min), marker));
        }
        for (int i = 0; i < labels.size(); i++) {
            insertInstruction(index++, createGotoLabel(newContext, labels.get(i), marker));
            insertInstruction(index++, createJumpUnconditional(newContext, targets.getOrDefault(min + i, finalLabel)));
        }

        // Remove all conditions
        for (AstContext condition : context.findSubcontexts(CONDITION)) {
            removeMatchingInstructions(ix -> ix.belongsTo(condition));
        }

        count++;
        return OptimizationResult.REALIZED;
    }

    private class ConvertCaseExpressionAction extends AbstractOptimizationAction {
        private final LogicVariable variable;
        private final NavigableMap<Integer, LogicLabel> targets;

        public ConvertCaseExpressionAction(AstContext astContext, int cost, double benefit,
                LogicVariable variable, NavigableMap<Integer, LogicLabel> targets) {
            super(astContext, cost, benefit);
            this.variable = variable;
            this.targets = targets;
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            return applyOptimization(() -> convertCaseExpression(this, costLimit), toString());
        }

        @Override
        public String toString() {
            return getName() + ": convert case at line " + astContext.node().startToken().getLine();
        }
    }
}
