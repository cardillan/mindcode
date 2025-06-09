package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstCaseExpression;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.compiler.optimization.cases.*;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.JumpInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.mimex.ContentType;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;

/// Notes on null handling:
///
/// When the type of the input value is numeric, nulls are not supported and are not specifically handled.
/// If a `when` clause contains a null value for a numerical case expression, the optimization won't be applied.
///
/// When the type of the input value is a Mindustry content, nulls are handled whether the expression contains
/// a `when null` clause or not. If the `when null` is present, nulls are redirected to the `null` branch, otherwise
/// nulls are redirected to the `else` branch. Nulls are handled by a _null check_ installed either at the beginning of
/// the zero `when` branch body (it there's such a branch) or at the beginning of the `else` branch body. The null
/// check skips to the null `when` branch body.
///
/// When there's neither the `null` branch nor the `0` branch, the null values do not need to be explicitly handled.
///
/// Notes on jump table compression:
///
/// Jump table compression doesn't occur when range checking is suppressed.
///
/// Jump table compression works by splitting the table into segments. There are a few types of segments for various
/// `when` value configurations. Many different segment configurations are generated, which are then evaluated for
/// efficiency, and the best possible optimization is chosen.
///
/// * The segment needs to handle all targets within its range, including the `else` values.
/// * Number of else values below the segment's range is one of the segment's attributes. The cost of the execution
///   path up to a segment is accounted for these `else` values, but the segment must account for the execution path
///   of these additional `else` values through its own code.
/// * A segment has a majority label. This is the label of the `when` body which is most frequent in the segment.
///   Some segments may handle non-majority labels first and then jump to the majority label.
/// * Code responsible for routing the value to the corresponding segment is handled outside segments. Currently,
///   a bisection search is used.
///
/// The following dynamic attributes are defined for each segment:
///
/// * priorElseValues: the number of `else` values below the segment's range.
/// * handleNulls: true if the segment needs to handle nulls for else values within or below its range.
/// * last: true if the segment is last.
@NullMarked
public class CaseSwitcher extends BaseOptimizer {
    private final int caseConfiguration;
    private int actionCounter = 0;

    CaseSwitcher(OptimizationContext optimizationContext) {
        super(Optimization.CASE_SWITCHING, optimizationContext);
        this.caseConfiguration = getProfile().getCaseConfiguration();
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

    @Override
    protected boolean isDebugOutput() {
        return super.isDebugOutput() && (caseConfiguration == actionCounter || actionCounter == 0);
    }

    private static class ContextMatcher {
        final List<AstContext> contexts;
        int lastMatchIndex = 0;

        public ContextMatcher(List<AstContext> contexts) {
            this.contexts = contexts;
        }

        public boolean matches(AstContext context) {
            for (int i = 0; i < contexts.size(); i++) {
                int index = (i + lastMatchIndex) % contexts.size();
                if (context.belongsTo(contexts.get(index))) {
                    lastMatchIndex = index;
                    return true;
                }
            }
            return false;
        }
    }

    private void findPossibleCaseSwitches(AstContext context, int costLimit, List<OptimizationAction> result) {
        groupCount++;

        List<AstContext> conditions = context.findSubcontexts(CONDITION);
        ContextMatcher matcher = new ContextMatcher(conditions);

        boolean hasElseBranch = !(context.node() instanceof AstCaseExpression exp) || exp.isElseDefined();
        boolean removeRangeCheck = getProfile().isUnsafeCaseOptimization() && !hasElseBranch;

        LogicVariable variable = null;
        Targets targets = new Targets(hasElseBranch);
        WhenValueAnalyzer analyzer = new WhenValueAnalyzer();

        // Used to compute the size and execution costs of the case expression
        int jumps = 0, values = 0, savedSteps = 0;

        try (LogicIterator iterator = createIteratorAtContext(context)) {
            while (iterator.hasNext()) {
                LogicInstruction ix = iterator.next();
                if (ix.getAstContext().parent() == context && ix.getAstContext().matches(CONDITION) || matcher.matches(ix.getAstContext())) {
                    if (ix instanceof JumpInstruction jump) {
                        jumps++;

                        // NOT_EQUAL might have been created by jump over jump optimization
                        if (jump.getCondition().isEquality()
                                && jump.getX() instanceof LogicVariable var && (variable == null || var.equals(variable))
                                && analyzer.analyze(jump.getY())
                                && getLabelInstruction(jump.getTarget()).getAstContext().parent() == context) {
                            variable = var;
                            LogicLabel target;
                            if (jump.getCondition() == Condition.EQUAL || jump.getCondition() == Condition.STRICT_EQUAL) {
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

                            savedSteps += values;
                            if (analyzer.getLastValue() != null) {
                                values++;
                            }
                        } else if (jump.isUnconditional()) {
                            savedSteps += values;
                        } else {
                            // Unconditional jump is a jump to the next when branch
                            return;
                        }
                    } else {
                        // Something different from a jump in a condition --> unsupported structure
                        return;
                    }
                }
            }
        }

        int valueSteps = jumps * values - savedSteps;

        // Unsupported case expressions: no input variable, no branches, or null and integers
        if (variable == null || analyzer.contentType == null || targets.isEmpty()
                || analyzer.hasNull && analyzer.contentType == ContentType.UNKNOWN) return;

        targets.computeElseValues(analyzer.contentType, metadata);

        SegmentMerger segmentMerger = new CombinatorialSegmentMerger(targets, analyzer.contentType != ContentType.UNKNOWN,
                getProfile().getCaseOptimizationStrength(), 1);

        // When no range checking, don't bother trying to merge segments.
        Set<SegmentConfiguration> configurations = removeRangeCheck
                ? Set.of(new SegmentConfiguration(segmentMerger.getPartitions(), List.of()))
                : segmentMerger.createSegmentConfigurations();

        debugOutput("Singular segments: \n" + segmentMerger.getPartitions().stream()
                .map(Object::toString).collect(Collectors.joining("\n")));


        optimizationContext.addDiagnosticData(new CaseSwitcherConfigurations(context.sourcePosition(), configurations.size()));

        List<ConvertCaseExpressionAction> actions = new ArrayList<>();
        for (SegmentConfiguration mergedSegments : configurations) {
            createOptimizationActions(actions, costLimit, context, jumps, valueSteps, variable, targets,
                    mergedSegments.createSegments(removeRangeCheck, analyzer.isMindustryContent(), targets),
                    analyzer.getContentType(), removeRangeCheck);
        }

        actionCounter = 0;
        debugOutput("%nCase switching optimization: %d distinct configurations generated.%n", actions.size());

        if (!actions.isEmpty()) {
            if (caseConfiguration > 0) {
                actions.stream().filter(a -> a.id == caseConfiguration).forEach(result::add);
                if (!result.isEmpty()) return;
            }

            // Comparing by cost (ascending) then by benefit (descending)
            actions.sort(Comparator.comparing(OptimizationAction::cost).thenComparing(Comparator.comparing(OptimizationAction::benefit).reversed()));
            ConvertCaseExpressionAction last = null;
            for (ConvertCaseExpressionAction action : actions) {
                // This action has a higher or equal cost to the last.
                // It needs to give a better benefit to be considered.
                if (last == null || action.benefit() > last.benefit()) {
                    result.add(action);
                    last = action;
                }
            }
        }
    }

    private void createOptimizationActions(List<ConvertCaseExpressionAction> actions, int costLimit, AstContext astContext,
            int jumps, int valueSteps, LogicVariable variable, Targets targets, List<Segment> segments, ContentType contentType,
            boolean removeRangeCheck) {
        int zeroPadSegment = findZeroPadSegment(segments);
        if (zeroPadSegment == 0) {
            Segment curr = segments.getFirst();
            Segment newSegment = new Segment(curr.type(), 0, curr.to(), curr.majorityLabel());
            actions.add(new ConvertCaseExpressionAction(astContext, jumps, valueSteps, variable, targets, List.of(newSegment),
                    contentType, removeRangeCheck, true));
        } else if (zeroPadSegment > 0) {
            List<Segment> newSegments = new ArrayList<>(segments);
            Segment prev = newSegments.get(zeroPadSegment - 1);
            Segment curr = newSegments.get(zeroPadSegment);

            if (contentType == ContentType.UNKNOWN) {
                if (prev.from() < 0) {
                    newSegments.set(zeroPadSegment - 1, new Segment(prev.type(), prev.from(), 0, prev.majorityLabel()));
                } else {
                    newSegments.set(zeroPadSegment - 1, new Segment(prev.type(), 0, 0, prev.majorityLabel()));
                }
                newSegments.set(zeroPadSegment, new Segment(curr.type(), 0, curr.to(), curr.majorityLabel()));
                if (prev.handleNulls()) {
                    newSegments.get(zeroPadSegment).setHandleNulls();
                }
            } else {
                if (zeroPadSegment != 1 || prev.from() != 0) throw new MindcodeInternalError("Mindustry content not starting at index 0");
                newSegments.removeFirst();
                newSegments.set(0, new Segment(curr.type(), 0, curr.to(), curr.majorityLabel()));
                if (prev.handleNulls()) {
                    newSegments.getFirst().setHandleNulls();
                }
            }

            actions.add(new ConvertCaseExpressionAction(astContext, jumps, valueSteps, variable, targets, newSegments,
                    contentType, removeRangeCheck, true));
        }

        actions.add(new ConvertCaseExpressionAction(astContext, jumps, valueSteps, variable, targets, segments,
                contentType, removeRangeCheck, false));
    }

    private int findZeroPadSegment(List<Segment> segments) {
        // It is the first one?
        // Support for single segments (the removeRangeCheck scenario)
        int firstPossibleIndex = segments.size() < 2 ? 0 : 1;
        Segment segment = segments.get(firstPossibleIndex);
        if (segment.from() > 0 && segment.type() == SegmentType.JUMP_TABLE) {
            return firstPossibleIndex;
        }

        for (int i = 2; i < segments.size(); i++) {
            Segment prev = segments.get(i - 1);
            Segment curr = segments.get(i);
            if (prev.empty() && prev.contains(0) && curr.type() == SegmentType.JUMP_TABLE) {
                return i;
            }
        }

        return -1;
    }

    public class ConvertCaseExpressionAction implements OptimizationAction {
        private final int id = ++actionCounter;
        private final AstContext astContext;
        private final int group = groupCount;
        private int cost;
        private double rawBenefit;
        private double benefit;
        private final LogicVariable variable;
        private final Targets targets;
        private final List<Segment> segments;
        private final boolean logicConversion;
        private final boolean removeRangeCheck;
        private final boolean zeroPadded;
        private final boolean symbolic = getProfile().isSymbolicLabels();

        private ConvertCaseExpressionAction(AstContext astContext, int jumps, int valueSteps, LogicVariable variable,
                Targets targets, List<Segment> segments, ContentType contentType, boolean removeRangeCheck, boolean zeroPadded) {
            this.astContext = astContext;
            this.variable = variable;
            this.targets = targets;
            this.segments = segments;
            this.logicConversion = contentType != ContentType.UNKNOWN;
            this.removeRangeCheck = removeRangeCheck;
            this.zeroPadded = zeroPadded;

            debugOutput("\n\n *** " + this + " *** \n");
            if (removeRangeCheck) {
                computeCostAndBenefitNoRangeCheck(jumps, valueSteps);
            } else {
                computeCostAndBenefit(jumps, valueSteps);
            }
        }

        @Override
        public Optimization optimization() {
            return optimization;
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

        public double rawBenefit() {
            return rawBenefit;
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            return applyOptimization(() -> convertCaseExpression(costLimit), toString());
        }

        @Override
        public @Nullable String getGroup() {
            return "CaseSwitcher" + group;
        }

        @Override
        public String toString() {
            int numSegments = segments.size() - (segments.getFirst().empty() ? 1 : 0) - (segments.getLast().empty() ? 1 : 0);
            String strId = CaseSwitcher.super.isDebugOutput() ? "#" + id + ", " : "";
            return "Convert case at " + Objects.requireNonNull(astContext.node()).sourcePosition().formatForLog() +
                    " (" + strId + "segments: " + numSegments + (zeroPadded ? ", zero based" : "") + ")";
        }

        private void computeCostAndBenefitNoRangeCheck(int originalJumps, int originalValueSteps) {
            int min = targets.firstKey();
            int max = targets.lastKey();

            int symbolicCost = symbolic && min != 0 ? 1 : 0;                // Cost of computing offset
            int contentCost = logicConversion ? 1 : 0;                      // Cost of converting type to logic ID
            int jumpTableCost = (max - min + 1) + 1;                        // Table size plus initial jump
            cost = jumpTableCost + symbolicCost + contentCost - originalJumps;

            int executionSteps = 2 + symbolicCost + contentCost;            // 1x multiJump, 1x jump table, additional costs
            rawBenefit = (double) originalValueSteps / targets.getTotalSize() - executionSteps;
            benefit = rawBenefit * astContext.totalWeight();
        }

        // Per target, not totals
        double targetSteps = 0.0;
        double elseSteps = 0.0;

        private void computeCostAndBenefit(int originalJumps, int originalValueSteps) {
            if (caseConfiguration == actionCounter) {
                System.out.print(""); // For breakpoint
            }

            // One time costs
            int contentCost = logicConversion ? 1 : 0;          // Cost of converting type to logic ID
            cost = contentCost - originalJumps;
            targetSteps = (double) contentCost * targets.size() / targets.getTotalSize();
            elseSteps = (double) contentCost * targets.getElseValues() / targets.getTotalSize();

            debugOutput(this);
            debugOutput("Case expression initialization: instructions: " + contentCost
                    + ", average steps: " + contentCost + ", total steps: " + contentCost * targets.getTotalSize());

            // Account for null handling: an instruction per zero value
            // Note: null handling when zero is not there is accounted for in individual segments
            if (logicConversion && targets.hasNullOrZeroKey()) {
                if (targets.hasZeroKey()) {
                    // There's a handler on the `0` branch
                    double nullHandling = 1.0 / targets.getTotalSize();
                    debugOutput("Null handling: instructions: 1, average steps: %g, total steps: 1", nullHandling);
                    targetSteps += nullHandling;
                } else {
                    // There's a handler on the else branch
                    debugOutput("Null handling: instructions: 1 (null handling steps accounted for in individual segments)");
                }
                cost++;
            }

            double bisectionSteps = (double) (computeBisectionSteps(segments, 0)) / targets.getTotalSize();

            segments.forEach(this::computeSegmentCostAndBenefit);

            if (targets.hasElseBranch() && targets.getElseValues() > 0) {
                double elseWeight = 1.0;
                double originalSteps = ((double) originalValueSteps + targets.getElseValues() * originalJumps * elseWeight) / targets.getTotalSize();

                elseSteps *= elseWeight;

                debugOutput("Original steps: %s, else weight: %s, new steps: %s (target: %s, else: %s; bisection: %s)",
                        originalSteps, elseWeight, targetSteps + elseSteps, targetSteps, elseSteps, bisectionSteps);

                rawBenefit = originalSteps - targetSteps - elseSteps;
            } else {
                // We disregard else steps in both computations
                double originalSteps = (double) originalValueSteps / targets.size();
                targetSteps = targetSteps * targets.getTotalSize() / targets.size();

                debugOutput("Original steps: %s, new steps: %s (target: %s, disregarding else; bisection: %s)", originalSteps,
                        targetSteps, targetSteps, bisectionSteps);

                rawBenefit = originalSteps - targetSteps;
            }
            benefit = rawBenefit * astContext.totalWeight();

            debugOutput("Original size: %d, new size: %d, cost: %d", originalJumps, cost + originalJumps, cost);
            if (zeroPadded) debugOutput("*** ZERO PADDED ***");
            debugOutput("");
        }

        private @Nullable AstContext newAstContext;
        private @Nullable LogicVariable caseVariable;
        private int index;

        private void computeSegmentCostAndBenefit(Segment segment) {
//            if (caseConfiguration == actionCounter) {
//                System.out.print(""); // For breakpoint
//            }

            int allTargets = targets.size();
            int activeTargets = (int) targets.keySet().stream().filter(segment::contains).count();
            int remainingTargets = (int) targets.keySet().stream().filter(i -> i >= segment.from()).count();

            SegmentStats stats = switch (segment.type()) {
                case SINGLE -> computeSingleSegment(segment, activeTargets);
                case MIXED -> computeMixedSegment(segment, activeTargets);
                case JUMP_TABLE -> computeJumpTable(segment, activeTargets);
            };

            debugOutput("Segment %3d to %3d: %s, %s, total steps: %s, bisection steps: %s", segment.from(), segment.to(), segment.typeName(),
                    stats, stats.steps + stats.elseSteps, segment.bisectionSteps());

            cost += stats.size;
            targetSteps += (stats.steps + segment.bisectionSteps() * stats.values) / targets.getTotalSize();
            elseSteps += (stats.elseSteps + segment.bisectionSteps() * stats.elseValues) / targets.getTotalSize();
        }

        // Note: this might need to be updated for integer case expressions,
        // as those do not consider the `else` targets and need to compute segment weights differently
        private int bisect(List<Segment> segments) {
            if (segments.size() <= 1) return -1;

            int middle = segments.getFirst().from() + (segments.getLast().to() - segments.getFirst().from()) / 2;

            int distance = Integer.MAX_VALUE, best = -1;
            for (int i = 1; i < segments.size(); i++) {
                Segment segment = segments.get(i);
                int d = Math.abs(segment.from() - middle);
                if (d < distance) {
                    distance = d;
                    best = i;
                }
            }

            return best;
        }

        private int computeBisectionSteps(List<Segment> segments, int depth) {
            int bisection = bisect(segments);
            if (bisection < 0) {
                segments.forEach(segment -> segment.setBisectionSteps(depth));
                return 0;
            }

            cost++;

            int min = segments.getFirst().from();
            int max = segments.getLast().to();
            int steps = max - min;
            Segment split = segments.get(bisection);
            debugOutput("Bisection at %d (%d to %d)", split.from(), min, max);

            // The bisecting jump at this level is executed once for each target in the list
            return steps
                    + computeBisectionSteps(segments.subList(0, bisection), depth + 1)
                    + computeBisectionSteps(segments.subList(bisection, segments.size()), depth + 1);
        }

        /// SINGLE SEGMENT
        ///
        /// Notes:
        ///
        /// * A single segment has only one target: the majority label, which cannot be an else branch.
        /// * The segment consists of the jump to the majority label
        private SegmentStats computeSingleSegment(Segment segment, int activeTargets) {
            boolean handleNull = logicConversion && segment.from() == 0;

            // Must not happen - is not accounted for in the computeSingleSegment
            if (segment.empty()) {
                return new SegmentStats(0, 0, 0, segment.size(),
                        segment.handleNulls() ? segment.size() : 0);
            } else {
                return new SegmentStats(1, segment.size(), segment.size(), 0, 0);
            }
        }

        private void generateSingleSegment(Segment segment, LogicLabel finalLabel) {
            if (segment.empty()) throw new MindcodeInternalError("Empty segment");

            assert newAstContext != null;
            assert caseVariable != null;

            LogicLabel target = segment.majorityLabel() == LogicLabel.INVALID ? targets.getExisting(0) : segment.majorityLabel();
            insertInstruction(createJumpUnconditional(newAstContext, target));
        }

        /// MIXED SEGMENT
        ///
        /// The majority branch of a mixed segment can be an else branch or a value branch.
        ///
        /// **The majority branch is the else branch**
        ///
        /// 1. The select table. Contains a jump for all active targets. The number of steps is (n + 1) / 2.
        /// 2. Jump to the else branch.
        ///
        /// **The majority branch is a when branch**
        ///
        /// 1. The select table. Contains a jump for all non-majority targets. The number of steps for
        ///    non-majority targets is (n + 1) / 2. Counted separately for the else branch and the value branches.
        /// 2. A jump to the majority label.
        private SegmentStats computeMixedSegment(Segment segment, int activeTargets) {
            if (segment.majorityLabel() == LogicLabel.INVALID) {
                throw new IllegalStateException("Invalid label in mixed segment");
            }

            int elseValues = segment.size() - activeTargets;

            if (segment.majorityLabel() == LogicLabel.EMPTY) {
                // Majority branch is the else branch
                // Final jump counts as else branch

                // Account for null handling on the else branch
                int nullHandling = segment.handleNulls() ? elseValues : 0;

                return new SegmentStats(activeTargets + 1,
                        activeTargets,
                        activeTargets * (activeTargets + 1) / 2.0,
                        elseValues,
                        elseValues * (activeTargets + 1) + nullHandling);
            } else {
                // Majority branch is a when branch
                int majorityTargets = (int) targets.entrySet().stream().filter(
                        e -> segment.contains(e.getKey()) && segment.majorityLabel().equals(e.getValue())).count();
                int branchTargets = activeTargets - majorityTargets;
                int elseTargets = segment.size() - activeTargets;

                int activeSteps = 0;
                int elseSteps = 0;

                // This counts the number of steps for values that are handled by the select table.
                // Traversing the jumps in the opposite order from the one in which they'll be built,
                // so that the size always corresponds to the number of instructions the not-yet-handled
                // values will see.
                int size = 0;
                for (int value = segment.to() - 1; value >= segment.from(); value--) {
                    LogicLabel target = targets.get(value);
                    if (target == null) {
                        // Here is a jump to an else branch. It adds a step and is counted.
                        size++;
                        elseSteps += size;
                    } else if (!segment.majorityLabel().equals(target)) {
                        // Here is a jump to a non-majority branch. It adds a step and is counted.
                        size++;
                        activeSteps += size;
                    }
                }

                // Values that ended up in the jumps so far are already counted.
                // Compute execution through the select table by majority targets and else values
                activeSteps += size * majorityTargets;

                // The final jump to majority target
                size++;
                activeSteps += majorityTargets;

                return new SegmentStats(
                        size,
                        activeTargets,
                        activeSteps,
                        elseValues,
                        elseSteps);
            }
        }

        private void generateMixedSegment(Segment segment, LogicLabel finalLabel) {
            assert newAstContext != null;
            assert caseVariable != null;

            boolean majorityIsElse = segment.majorityLabel() == LogicLabel.EMPTY;

            LogicLabel majorityTarget = majorityIsElse ? finalLabel : segment.majorityLabel();
            for (int value = segment.from(); value < segment.to(); value++) {
                LogicLabel target = targets.getOrDefault(value, finalLabel);
                LogicLabel updatedTarget = value == 0 && target == finalLabel ? targets.getNullOrElseTarget() : target;
                if (!majorityTarget.equals(target)) {
                    insertInstruction(createJump(newAstContext, target, Condition.EQUAL, caseVariable, LogicNumber.create(value)));
                }
            }

            if (segment.handleNulls() && majorityIsElse) {
                insertInstruction(createJumpUnconditional(newAstContext, targets.getNullOrElseTarget()));
            } else {
                insertInstruction(createJumpUnconditional(newAstContext, majorityTarget));
            }
        }

        private int maxSelectEntries(int from) {
            return symbolic && from != 0 ? 4 : 3;
        }

        private SegmentStats computeJumpTable(Segment segment, int activeTargets) {
            if (!removeRangeCheck && (activeTargets <= maxSelectEntries(segment.from()))) {
                // It's a select table actually
                int elseValues = segment.size() - activeTargets;
                return new SegmentStats(activeTargets + 1,
                        activeTargets,
                        activeTargets * (activeTargets + 1) / 2.0,
                        elseValues,
                        elseValues * (activeTargets + 1));
            } else {
                int multiJump = symbolic && segment.from() != 0 ? 2 : 1;
                int tableSize = segment.size();
                int elseValues = tableSize - activeTargets;
                int elseSteps = elseValues * (multiJump + 1);

                // Account for null handling on the else branch
                int nullHandling = segment.handleNulls() ? 1 : 0;

                return new SegmentStats(
                        multiJump + tableSize,
                        activeTargets,
                        activeTargets * (multiJump + 1),
                        elseValues,
                        elseSteps + nullHandling);
            }
        }

        // Generates a jump table
        // Values less than 'from' are sent to the final label
        // Values greater than or equal to 'to' are sent to the next segment
        private void generateJumpTable(Segment segment, LogicLabel finalLabel) {
            int activeTargets = (int) targets.keySet().stream().filter(segment::contains).count();
            if (!removeRangeCheck && (activeTargets <= maxSelectEntries(segment.from()))) {
                generateSelectTable(segment, finalLabel);
                return;
            }

            assert newAstContext != null;
            assert caseVariable != null;

            List<LogicLabel> labels = IntStream.range(segment.from(), segment.to()).mapToObj(i -> instructionProcessor.nextLabel()).toList();
            LogicLabel marker = instructionProcessor.nextLabel();
            insertInstruction(createMultiJump(newAstContext, labels.getFirst(), caseVariable, LogicNumber.create(segment.from()), marker));

            for (int i = 0; i < labels.size(); i++) {
                insertInstruction(createMultiLabel(newAstContext, labels.get(i), marker));
                LogicLabel target = targets.getOrDefault(segment.from() + i, finalLabel);
                LogicLabel updatedTarget = segment.from() + i == 0 && target == finalLabel ? targets.getNullOrElseTarget() : target;
                insertInstruction(createJumpUnconditional(newAstContext, updatedTarget));
            }
        }

        private void generateSelectTable(Segment segment, LogicLabel finalLabel) {
            assert newAstContext != null;
            assert caseVariable != null;

            targets.subMap(segment.from(), segment.to()).forEach((value, label) ->
                    insertInstruction(createJump(newAstContext, label, Condition.EQUAL, caseVariable, LogicNumber.create(value))));

            LogicLabel target = segment.handleNulls() ? targets.getNullOrElseTarget() : finalLabel;
            insertInstruction(createJumpUnconditional(newAstContext, target));
        }

        void buildBisectionTable(List<Segment> segments, LogicLabel finalLabel) {
            assert newAstContext != null;
            assert caseVariable != null;

            int bisection = bisect(segments);

            if (bisection >= 0) {
                List<Segment> lowSegments = segments.subList(0, bisection);
                List<Segment> highSegments = segments.subList(bisection, segments.size());

                if (isEmpty(lowSegments)) {
                    LogicLabel target = lowSegments.stream().anyMatch(Segment::handleNulls) ? targets.getNullOrElseTarget() : finalLabel;
                    insertInstruction(createJump(newAstContext, target, Condition.LESS_THAN, caseVariable,
                            LogicNumber.create(highSegments.getFirst().from())));
                    buildBisectionTable(highSegments, finalLabel);
                } else if (isEmpty(highSegments)) {
                    LogicLabel target = highSegments.stream().anyMatch(Segment::handleNulls) ? targets.getNullOrElseTarget() : finalLabel;
                    insertInstruction(createJump(newAstContext, target, Condition.GREATER_THAN_EQ, caseVariable,
                            LogicNumber.create(highSegments.getFirst().from())));
                    buildBisectionTable(lowSegments, finalLabel);
                } else {
                    LogicLabel highTarget = instructionProcessor.nextLabel();
                    insertInstruction(createJump(newAstContext, highTarget, Condition.GREATER_THAN_EQ, caseVariable,
                            LogicNumber.create(highSegments.getFirst().from())));
                    buildBisectionTable(lowSegments, finalLabel);
                    insertInstruction(createLabel(newAstContext, highTarget));
                    buildBisectionTable(highSegments, finalLabel);
                }
            } else {
                for (Segment segment : segments) {
                    debugOutput("Building segment " + segment + ", size: " + segment.size() + ", targets: " + targets.subMap(segment.from(), segment.to()).size());
                    switch (segment.type()) {
                        case SINGLE -> generateSingleSegment(segment, finalLabel);
                        case MIXED -> generateMixedSegment(segment, finalLabel);
                        case JUMP_TABLE -> generateJumpTable(segment, finalLabel);
                    }
                }
            }
        }

        private boolean isEmpty(List<Segment> segments) {
            return segments.stream().allMatch(Segment::empty);
        }

        protected void insertInstruction(LogicInstruction instruction) {
            optimizationContext.insertInstruction(index++, instruction);
        }

        private OptimizationResult convertCaseExpression(int costLimit) {
            //System.out.println(id);

            index = firstInstructionIndex(Objects.requireNonNull(astContext.findSubcontext(CONDITION)));
            AstContext elseContext = astContext.findSubcontext(ELSE);
            AstContext finalContext = elseContext != null ? elseContext : Objects.requireNonNull(astContext.lastChild());
            LogicLabel finalLabel = obtainContextLabel(finalContext);

            targets.elseTarget = finalLabel;
            targets.nullOrElseTarget = finalLabel;

            newAstContext = astContext.createSubcontext(FLOW_CONTROL, 1.0);

            if (logicConversion) {
                caseVariable = instructionProcessor.nextTemp();
                insertInstruction(createSensor(Objects.requireNonNull(astContext.parent()),
                        caseVariable, variable, LogicBuiltIn.ID));

                // We need to install a null check
                LogicLabel zeroLabel = targets.get(0);
                if (zeroLabel == null) {
                    if (targets.nullTarget != null) {
                        // There's an explicit null branch, and no `0` branch
                        // Install a null check on the else branch
                        int elseIndex = optimizationContext.getLabelInstructionIndex(finalLabel);
                        LogicInstruction elseLabelIx = instructionAt(elseIndex);
                        targets.nullOrElseTarget = instructionProcessor.nextLabel();
                        optimizationContext.insertInstruction(elseIndex++, createLabel(elseLabelIx.getAstContext(),
                                targets.nullOrElseTarget));
                        optimizationContext.insertInstruction(elseIndex, createJump(elseLabelIx.getAstContext(),
                                targets.nullTarget, Condition.STRICT_EQUAL, caseVariable, LogicNull.NULL));
                    }
                } else {
                    // There's a zero branch. Need to install the handler there
                    if (targets.nullTarget == null) {
                        targets.nullTarget = finalLabel;
                    }

                    LogicLabel nullOrZeroTarget = instructionProcessor.nextLabel();
                    targets.put(0, nullOrZeroTarget);

                    int zeroIndex = optimizationContext.getLabelInstructionIndex(zeroLabel);
                    LogicInstruction zeroLabelIx = instructionAt(zeroIndex);
                    optimizationContext.insertInstruction(zeroIndex++, createLabel(zeroLabelIx.getAstContext(),
                            nullOrZeroTarget));
                    optimizationContext.insertInstruction(zeroIndex, createJump(zeroLabelIx.getAstContext(),
                            targets.nullTarget, Condition.STRICT_EQUAL, caseVariable, LogicNull.NULL));
                }
            } else {
                caseVariable = variable;
            }

            buildBisectionTable(segments, finalLabel);

            // Remove all conditions
            for (AstContext condition : astContext.findSubcontexts(CONDITION)) {
                removeMatchingInstructions(ix -> ix.belongsTo(condition));
            }

            count++;
            optimizationContext.addDiagnosticData(ConvertCaseExpressionAction.this);
            return OptimizationResult.REALIZED;
        }
    }

    private class WhenValueAnalyzer {
        private boolean first = true;
        private boolean hasNull = false;
        private @Nullable ContentType contentType;        // ContentType.UNKNOWN represents an integer
        private @Nullable Integer lastValue;

        public boolean analyze(LogicValue value) {
            if (value == LogicNull.NULL) {
                lastValue = null;
                hasNull = true;
                return true;
            }

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

        public @Nullable Integer getLastValue() {
            return lastValue;
        }

        public boolean isMindustryContent() {
            return contentType != ContentType.UNKNOWN;
        }

        public ContentType getContentType() {
            return Objects.requireNonNull(contentType);
        }
    }

    /// Contains the size and execution info of a segment.
    ///
    /// Execution time needed to skip to another segment is not included, it must always be one.
    /// Null path execution time through the `0` branch is not considered - this is handled centrally.
    /// Null path execution time through the `else` branch is counted in the else steps for all else values
    /// that go through the null check.
    ///
    /// @param size       The size of the code in the segment.
    /// @param values     The total number of target values handled by this segment.
    /// @param steps      The total number of steps needed for all target values handled by this segment to resolve.
    ///                   Each target value is considered separately, and all execution steps needed to handle the
    ///                   target value are counted.
    /// @param elseValues The total number of else values handled by this segment. Those values that actually land
    ///                   on the else branch are counted.
    /// @param elseSteps  The total number of steps needed for all else values handled by this segment to resolve.
    ///                   Only the else values that actually land on the else branch are included.
    record SegmentStats(int size, int values, double steps, int elseValues, double elseSteps) {
    }
}
