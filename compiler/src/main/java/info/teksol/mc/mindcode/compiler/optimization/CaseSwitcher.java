package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstCaseExpression;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.JumpInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.mimex.ContentType;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
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

        boolean removeRangeCheck = getProfile().isUnsafeCaseOptimization()
                && context.node() instanceof AstCaseExpression exp && !exp.isElseDefined();

        int removed = 0;

        WhenValueAnalyzer analyzer = new WhenValueAnalyzer();

        try (LogicIterator iterator = createIteratorAtContext(context)) {
            while (iterator.hasNext()) {
                LogicInstruction ix = iterator.next();
                if (ix.getAstContext().parent() == context && ix.getAstContext().matches(CONDITION)) {
                    if (ix instanceof JumpInstruction jump) {
                        removed++;

                        // NOT_EQUAL might have been created by jump over jump optimization
                        if ((jump.getCondition() == Condition.EQUAL || jump.getCondition() == Condition.NOT_EQUAL)
                                && jump.getX() instanceof LogicVariable var && (variable == null || var.equals(variable))
                                && analyzer.analyze(jump.getY())
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

                            if (targets.put(analyzer.getLastValue(), target) != null) {
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

        boolean symbolic = getProfile().isSymbolicLabels();

        // Cost calculation
        int min = targets.firstEntry().getKey();
        int max = targets.lastEntry().getKey();

        // In symbolic labels mode, having min different from 0 requires an additional executable instruction
        // This expands the jump table instead to avoid that instruction
        if (symbolic && min >= 1 && min <= 3) min = 0;

        // Cost of computing offset
        int symbolicCost = symbolic && min != 0 ? 1 : 0;

        // Cost of converting type to logic ID
        int contentCost = analyzer.getContentType() != ContentType.UNKNOWN ? 1 : 0;

        // Cost of range check
        int rangeCheckCost = removeRangeCheck ? 0 : 2;

        // Jump table cost
        int jumpTableCost = (max - min + 1);

        // Adding one jump for each value between min and max inclusive (the jump table)
        // Savings are equal to removed instructions
        int cost = jumpTableCost + rangeCheckCost + symbolicCost + contentCost - removed;

        // New sequence of executed instructions will be: jump lessThan / jump greaterThan / goto offset / jump to branch
        // We save half of the switch jumps on average
        int executionSteps = 2 + rangeCheckCost + symbolicCost + contentCost;
        double benefit = ((removed + 1) / 2.0 - executionSteps) * context.totalWeight();

        return benefit <= 0 || cost > costLimit ? null
                : new ConvertCaseExpressionAction(context, cost, benefit, variable, targets, min, analyzer.getContentType(), removeRangeCheck);
    }

    private OptimizationResult convertCaseExpression(ConvertCaseExpressionAction action, int costLimit) {
        AstContext context = action.astContext;
        NavigableMap<Integer, LogicLabel> targets = action.targets;

        int index = firstInstructionIndex(Objects.requireNonNull(context.findSubcontext(CONDITION)));
        AstContext elseContext = context.findSubcontext(ELSE);
        AstContext finalContext = elseContext != null ? elseContext : Objects.requireNonNull(context.lastChild());
        LogicLabel finalLabel = obtainContextLabel(finalContext);
        AstContext newContext = context.createSubcontext(FLOW_CONTROL, 1.0);
        int min = action.min;
        int max = targets.lastEntry().getKey();

        LogicLabel marker = instructionProcessor.nextLabel();
        List<LogicLabel> labels = IntStream.rangeClosed(min, max).mapToObj(i -> instructionProcessor.nextLabel()).toList();

        LogicVariable caseVariable;
        if (action.contentType == ContentType.UNKNOWN) {
            caseVariable = action.variable;
        } else {
            caseVariable = instructionProcessor.nextTemp();
            insertInstruction(index++, createSensor(newContext, caseVariable, action.variable, LogicBuiltIn.ID));
        }

        if (!action.removeRangeCheck) {
            if (action.contentType != ContentType.UNKNOWN && min == 0) {
                insertInstruction(index++, createJump(newContext, finalLabel, Condition.STRICT_EQUAL, caseVariable, LogicNull.NULL));
            } else {
                insertInstruction(index++, createJump(newContext, finalLabel, Condition.LESS_THAN, caseVariable, LogicNumber.create(min)));
            }
            insertInstruction(index++, createJump(newContext, finalLabel, Condition.GREATER_THAN, caseVariable, LogicNumber.create(max)));
        }
        insertInstruction(index++, createMultiJump(newContext, labels.getFirst(), caseVariable, LogicNumber.create(min), marker));

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
        private final int min;
        private final ContentType contentType;
        private final boolean removeRangeCheck;

        public ConvertCaseExpressionAction(AstContext astContext, int cost, double benefit, LogicVariable variable,
                NavigableMap<Integer, LogicLabel> targets, int min, ContentType contentType, boolean removeRangeCheck) {
            super(astContext, cost, benefit);
            this.variable = variable;
            this.targets = targets;
            this.min = min;
            this.contentType = contentType;
            this.removeRangeCheck = removeRangeCheck;
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

    private class WhenValueAnalyzer {
        private boolean first = true;
        private @Nullable ContentType contentType;        // ContentType.UNKNOWN represents an integer
        private int lastValue;

        public boolean analyze(LogicValue value) {
            ContentType category = categorize(value);

            if (first) {
                contentType = category;
                first = false;
            }

            return contentType == category && contentType != null;
        }

        public @Nullable ContentType categorize(LogicValue value) {
            if (value.isNumericConstant() && value.isInteger()) {
                lastValue = value.getIntValue();
                return ContentType.UNKNOWN;
            } else if (advanced() && value instanceof LogicBuiltIn builtIn && builtIn.getObject() != null && canConvert(builtIn)) {
                lastValue = builtIn.getObject().logicId();
                return builtIn.getObject().contentType();
            }

            return null;
        }

        private boolean canConvert(LogicBuiltIn builtIn) {
            MindustryContent object = builtIn.getObject();
            return object != null
                    && object.contentType().hasLookup
                    && object.logicId() >= 0
                    && (getProfile().isTargetOptimization() || metadata.isStableBuiltin(builtIn.getName()));
        }

        public int getLastValue() {
            return lastValue;
        }

        public ContentType getContentType() {
            return Objects.requireNonNull(contentType);
        }
    }
}
