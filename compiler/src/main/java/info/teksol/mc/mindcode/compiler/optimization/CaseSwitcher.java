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

import java.util.*;
import java.util.stream.IntStream;

import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;

@NullMarked
class CaseSwitcher extends BaseOptimizer {
    private static final int MINIMAL_GAP = 4;

    public CaseSwitcher(OptimizationContext optimizationContext) {
        super(Optimization.CASE_SWITCHING, optimizationContext);
    }

    private int invocations = 0;
    private int count = 0;

    private int groupCount = 0;

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
        List<OptimizationAction> result = new ArrayList<>();
        forEachContext(AstContextType.CASE, BASIC,
                returningNull((context -> findPossibleCaseSwitches(context, costLimit, result))));
        return result;
    }

    private List<Gap> findGaps(TreeMap<Integer, LogicLabel> targets) {
        List<Gap> gaps = new ArrayList<>();
        int last = targets.firstKey();

        // Else branch gaps
        for (Integer key : targets.keySet()) {
            if (key - last > MINIMAL_GAP) {
                int start = last + 1;
                gaps.add(new Gap(start, key - start, null));
            }
            last = key;
        }

//        // Consequent values
//        LogicLabel label = null;
//        int size = 0;
//        for (Map.Entry<Integer, LogicLabel> entry : targets.entrySet()) {
//            if (label == null) {
//                label = entry.getValue();
//                last = entry.getKey();
//                size = 1;
//            } else if (label.equals(entry.getValue()) && entry.getKey() == last + 1) {
//                size++;
//                last++;
//            } else {
//                if (size > MINIMAL_GAP) {
//                    gaps.add(new Gap(last - size + 1, size, label));
//                }
//                label = null;
//            }
//        }

        gaps.sort(null);
        return gaps;
    }

    private void findPossibleCaseSwitches(AstContext context, int costLimit, List<OptimizationAction> result) {
        groupCount++;

        LogicVariable variable = null;
        TreeMap<Integer, LogicLabel> targets = new TreeMap<>();

        if (context.findSubcontext(CONDITION) == null) {
            return;
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
                                    return;
                                }
                            }

                            if (targets.put(analyzer.getLastValue(), target) != null) {
                                return;
                            }
                        } else if (!jump.isUnconditional()) {
                            // Unconditional jump is a jump to next when branch
                            return;
                        }
                    } else {
                        // Something different from a jump in condition --> unsupported structure
                        return;
                    }
                }
            }
        }

        // Degenerate case expressions: no branches
        if (targets.isEmpty()) return;
        List<Gap> gaps = findGaps(targets);

        boolean symbolic = getProfile().isSymbolicLabels();

        // Cost calculation
        int min = targets.firstEntry().getKey();

        // In symbolic labels mode, having min different from 0 requires an additional executable instruction
        // This expands the jump table instead to avoid that instruction
        if (symbolic && min > 0 && min < MINIMAL_GAP) min = 0;

        ConvertCaseExpressionAction action = new ConvertCaseExpressionAction(context, removed, variable, targets,
                List.of(), min, analyzer.getContentType(), removeRangeCheck);

        if (action.benefit() > 0 && action.cost() <= costLimit) {
            result.add(action);
        }

        if (removeRangeCheck) return;

        int instructionSpace = getProfile().getInstructionLimit() - optimizationContext.getProgram().size();
        for (int i = 0; i < gaps.size(); i++) {
            action = new ConvertCaseExpressionAction(context, removed, variable, targets,
                    gaps.subList(0, i + 1), min, analyzer.getContentType(), removeRangeCheck);
            if (action.benefit() > 0 && action.cost() <= costLimit) {
                result.add(action);
            }
        }
    }

    private class ConvertCaseExpressionAction implements OptimizationAction {
        private final AstContext astContext;
        private final int group = groupCount;
        private int cost;
        private double benefit;
        private final LogicVariable variable;
        private final TreeMap<Integer, LogicLabel> targets;
        private final List<Gap> gaps;
        private final int min;
        private final ContentType contentType;
        private final boolean removeRangeCheck;
        private final boolean symbolic = getProfile().isSymbolicLabels();

        public ConvertCaseExpressionAction(AstContext astContext, int removed, LogicVariable variable,
                TreeMap<Integer, LogicLabel> targets, List<Gap> gaps, int min, ContentType contentType, boolean removeRangeCheck) {
            this.astContext = astContext;
            this.variable = variable;
            this.targets = targets;
            this.gaps = gaps.stream().sorted(Comparator.comparing(Gap::start)).toList();
            this.min = min;
            this.contentType = contentType;
            this.removeRangeCheck = removeRangeCheck;
            if (removeRangeCheck) {
                computeCostAndBenefitNoRangeCheck(removed);
            } else {
                computeCostAndBenefit(removed);
            }
        }

        @Override
        public AstContext astContext() {
            return astContext;
        }

        @Override
        public int cost() {
            return cost;
        }

        @Override
        public double benefit() {
            return benefit;
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            return applyOptimization(() -> convertCaseExpression(costLimit), toString());
        }

        @Override
        public @Nullable String getGroup() {
            return "CaseSwitcher" + group;
        }

        private void debug(Object message) {
            //System.out.println(message);
        }

        @Override
        public String toString() {
            return "Convert case at " + Objects.requireNonNull(astContext.node()).sourcePosition().formatForLog() +
                    " (segments: " + (gaps.size() + 1) + ")";
        }

        private void computeCostAndBenefitNoRangeCheck(int removed) {
            int max = targets.lastEntry().getKey();
            int symbolicCost = symbolic && min != 0 ? 1 : 0;                // Cost of computing offset
            int contentCost = contentType == ContentType.UNKNOWN ? 0 : 1;   // Cost of converting type to logic ID
            int jumpTableCost = (max - min + 1) + 1;                        // Table size plus initial jump
            cost = jumpTableCost + symbolicCost + contentCost - removed;

            double executionSteps = 2 + symbolicCost + contentCost;         // MultiJump + jump table + additional costs
            double savedSteps = (removed + 1) / 2.0;
            benefit = (savedSteps - executionSteps) * astContext.totalWeight();
        }

        double executionSteps = 0.0;

        private void computeCostAndBenefit(int removed) {
            // One time costs
            int symbolicCost = symbolic && min != 0 ? 1 : 0;                // Cost of computing offset
            int contentCost = contentType == ContentType.UNKNOWN ? 0 : 1;   // Cost of converting type to logic ID
            cost = contentCost - removed;
            executionSteps = contentCost;

            debug(this);
            debug("Initial execution steps: " + executionSteps);

            int segmentMin = min;
            for (Gap gap : gaps) {
                int segmentMax = gap.start() - 1;
                computeSegmentCostAndBenefit(segmentMin, segmentMax);
                segmentMin = gap.start() + gap.size();
            }

            // Final segment
            computeSegmentCostAndBenefit(segmentMin, targets.lastEntry().getKey());

            double savedSteps = (removed + 1) / 2.0;
            debug("Saved steps: " + savedSteps + ", new steps: " + executionSteps);
            debug("");
            benefit = (savedSteps - executionSteps) * astContext.totalWeight();
        }

        private int maxSelectEntries(int min) {
            return symbolic && min != 0 ? 4 : 3;
        }

        private void computeSegmentCostAndBenefit(int min, int max) {
            // Number of instructions without the jump table:
            //     range check (2)
            //     multijump (one or two depending on symbolic)
            int instructions = 2 + (symbolic && min != 0 ? 2 : 1);

            // Number of steps taken through the jump table: additional jump in the jump table
            int jumpTableSteps = instructions + 1;

            int allTargets = targets.size();
            int activeTargets = (int) targets.keySet().stream().filter(i -> i >= min && i <= max).count();
            int remainingTargets = (int) targets.keySet().stream().filter(i -> i >= min).count();

            if (activeTargets <= maxSelectEntries(min)) {
                int jumps = activeTargets + 1;
                int segmentSteps = activeTargets * (activeTargets + 1) / 2;
                int remainingSteps = (remainingTargets - activeTargets);
                double additionalSteps = (double)(segmentSteps + remainingSteps) / allTargets;

                debug("Segment " + min + " to " + max + ": selector, execution steps: " + additionalSteps + ", total segment steps: " + segmentSteps);
                cost += jumps + 1;
                executionSteps += additionalSteps;
                return;
            }


            int segmentSteps = jumpTableSteps * activeTargets;      // Jump table execution steps
            int remainingSteps = remainingTargets - activeTargets;  // One instruction - initial jump

            double additionalSteps = (double)(segmentSteps + remainingSteps) / allTargets;

            debug("Segment " + min + " to " + max + ": jump table, execution steps: " + additionalSteps + ", total segment steps: " + segmentSteps);

            // jump table (one per slot)
            int jumpTable = (max - min + 1);
            cost += instructions + jumpTable;
            executionSteps += additionalSteps;
        }

        private @Nullable AstContext newAstContext;
        private @Nullable LogicVariable caseVariable;
        private int index;

        protected void insertInstruction(LogicInstruction instruction) {
            optimizationContext.insertInstruction(index++, instruction);
        }

        private OptimizationResult convertCaseExpression(int costLimit) {
            index = firstInstructionIndex(Objects.requireNonNull(astContext.findSubcontext(CONDITION)));
            AstContext elseContext = astContext.findSubcontext(ELSE);
            AstContext finalContext = elseContext != null ? elseContext : Objects.requireNonNull(astContext.lastChild());
            LogicLabel finalLabel = obtainContextLabel(finalContext);

            newAstContext = astContext.createSubcontext(FLOW_CONTROL, 1.0);

            if (contentType == ContentType.UNKNOWN) {
                caseVariable = variable;
            } else {
                caseVariable = instructionProcessor.nextTemp();
                insertInstruction(createSensor(newAstContext, caseVariable, variable, LogicBuiltIn.ID));
            }

            int segmentMin = min;
            for (Gap gap : gaps) {
                int segmentMax = gap.start() - 1;
                LogicLabel nextSegmentLabel = instructionProcessor.nextLabel();
                generateJumpTable(segmentMin, segmentMax, nextSegmentLabel, gap.label() == null ? finalLabel : gap.label());
                insertInstruction(createLabel(newAstContext, nextSegmentLabel));
                segmentMin = gap.start() + gap.size();
            }

            // Final segment
            generateJumpTable(segmentMin, targets.lastEntry().getKey(), finalLabel, finalLabel);

            // Remove all conditions
            for (AstContext condition : astContext.findSubcontexts(CONDITION)) {
                removeMatchingInstructions(ix -> ix.belongsTo(condition));
            }

            count++;
            return OptimizationResult.REALIZED;
        }

        // Generates a jump table
        // Values less than min are sent to final label
        // Values greater than max are sent to the next segment
        private void generateJumpTable(int min, int max, LogicLabel nextSegmentLabel, LogicLabel finalLabel) {
            int activeTargets = (int) targets.keySet().stream().filter(i -> i >= min && i <= max).count();
            if (!removeRangeCheck && (activeTargets <= maxSelectEntries(min))) {
                generateSelectTable(min, max, nextSegmentLabel, finalLabel);
                return;
            }

            assert newAstContext != null;
            assert caseVariable != null;

            // Range check
            if (!removeRangeCheck) {
                insertInstruction(createJump(newAstContext, nextSegmentLabel, Condition.GREATER_THAN, caseVariable, LogicNumber.create(max)));
                if (contentType != ContentType.UNKNOWN && min == 0) {
                    insertInstruction(createJump(newAstContext, finalLabel, Condition.STRICT_EQUAL, caseVariable, LogicNull.NULL));
                } else {
                    insertInstruction(createJump(newAstContext, finalLabel, Condition.LESS_THAN, caseVariable, LogicNumber.create(min)));
                }
            }

            List<LogicLabel> labels = IntStream.rangeClosed(min, max).mapToObj(i -> instructionProcessor.nextLabel()).toList();
            LogicLabel marker = instructionProcessor.nextLabel();
            insertInstruction(createMultiJump(newAstContext, labels.getFirst(), caseVariable, LogicNumber.create(min), marker));

            for (int i = 0; i < labels.size(); i++) {
                insertInstruction(createMultiLabel(newAstContext, labels.get(i), marker));
                insertInstruction(createJumpUnconditional(newAstContext, targets.getOrDefault(min + i, finalLabel)));
            }
        }

        private void generateSelectTable(int min, int max, LogicLabel nextSegmentLabel, LogicLabel finalLabel) {
            assert newAstContext != null;
            assert caseVariable != null;

            if (nextSegmentLabel != finalLabel) {
                insertInstruction(createJump(newAstContext, nextSegmentLabel, Condition.GREATER_THAN, caseVariable, LogicNumber.create(max)));
            }

            targets.entrySet().stream().filter(e -> e.getKey() >= min && e.getKey() <= max)
                    .forEach(e -> insertInstruction(createJump(newAstContext, e.getValue(),
                                    Condition.EQUAL, caseVariable, LogicNumber.create(e.getKey())))
                    );

            if (nextSegmentLabel == finalLabel) {
                insertInstruction(createJumpUnconditional(newAstContext, finalLabel));
            }
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

    private record Gap(int start, int size, @Nullable LogicLabel label) implements Comparable<Gap> {
        @Override
        public int compareTo(Gap other) {
            return size == other.size
                    ? Integer.compare(start, other.start)
                    : Integer.compare(other.size, size);
        }
    }
}
