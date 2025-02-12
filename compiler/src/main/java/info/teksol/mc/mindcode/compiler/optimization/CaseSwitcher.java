package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.logic.arguments.Condition;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.JumpInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.IntStream;

import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;

@NullMarked
class CaseSwitcher extends BaseOptimizer {
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
    protected boolean optimizeProgram(OptimizationPhase phase) {
        return false;
    }

    @Override
    public List<OptimizationAction> getPossibleOptimizations(int costLimit) {
        invocations++;
        return forEachContext(AstContextType.CASE, BASIC,
                context -> findPossibleCaseSwitches(context, costLimit));
    }


    private @Nullable OptimizationAction findPossibleCaseSwitches(AstContext context, int costLimit) {
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
                                if (bodyContext != null && iterator.peek(0).belongsTo(bodyContext)) {
                                    target = obtainContextLabel(bodyContext);
                                } else {
                                    return null;
                                }
                            }

                            if (targets.put(lit.getIntValue(), target) != null) {
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

        // Degenerate case expressions: no branches
        if (targets.isEmpty()) return null;

        // Cost calculation:
        // Adding three fixed instructions: jump lessThan, jump greaterThan, goto offset
        // Adding one jump for each value between min and max inclusive (the jump table)
        // Saving one jump per each jump instruction
        int min = targets.firstEntry().getKey();
        int max = targets.lastEntry().getKey();
        int cost = 3 + (max - min + 1) - removed;

        // New sequence of executed instructions will be: jump lessThan / jump greaterThan / goto offset / jump to branch
        // We save half of the switch jumps on average
        double benefit = (removed / 2.0 - 4) * context.totalWeight();

        return benefit <= 0 || cost > costLimit ? null
                : new ConvertCaseExpressionAction(context, cost, benefit, variable, targets);
    }

    private OptimizationResult convertCaseExpression(ConvertCaseExpressionAction action, int costLimit) {
        AstContext context = action.astContext;
        NavigableMap<Integer, LogicLabel> targets = action.targets;

        int index = firstInstructionIndex(Objects.requireNonNull(context.findSubcontext(CONDITION)));
        AstContext elseContext = context.findSubcontext(ELSE);
        AstContext finalContext = elseContext != null ? elseContext : Objects.requireNonNull(context.lastChild());
        LogicLabel finalLabel = obtainContextLabel(finalContext);
        AstContext newContext = context.createSubcontext(FLOW_CONTROL, 1.0);
        LogicVariable jumpValue = instructionProcessor.nextTemp();
        int min = targets.firstEntry().getKey();
        int max = targets.lastEntry().getKey();

        LogicLabel marker = instructionProcessor.nextLabel();
        List<LogicLabel> labels = IntStream.rangeClosed(min, max).mapToObj(i -> instructionProcessor.nextLabel()).toList();

        if (RANGE_LIMITING) {
            insertInstruction(index++, createJump(newContext, finalLabel, Condition.LESS_THAN, action.variable, LogicNumber.create(min)));
            insertInstruction(index++, createJump(newContext, finalLabel, Condition.GREATER_THAN, action.variable, LogicNumber.create(max)));
        }
        insertInstruction(index++, createMultiJump(newContext, labels.getFirst(), action.variable, LogicNumber.create(min), marker));

        for (int i = 0; i < labels.size(); i++) {
            insertInstruction(index++, createMultiLabel(newContext, labels.get(i), marker));
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
            return "Convert case at " + Objects.requireNonNull(astContext.node()).sourcePosition().formatForLog();
        }
    }
}
