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
/// When the type of the input value is a Mindustry content, nulls are handled if the expression contains either a
/// `when null` clause or a `when 0` clause:
///
/// When there's a `when 0` clause, `null` values are explicitly tested at the beginning of the `when 0` branch body.
/// When found, they're directed either to the `when null` branch (if any) or to the `else` branch.
///
/// When there is a `when null` branch but no `when 0` branch, `null` values are explicitly tested at the beginning
/// of the `else` branch body. When found, they're directed to the `when null` branch. The `else` paths that possibly
/// contain a value of `0` are redirected to the null check, other `else` paths lead directly to the `else` body.
///
/// When there's neither the `null` branch nor the `0` branch, the null values do not need to be explicitly handled.
///
/// **Jump table compression**
///
/// Jump table compression doesn't occur when range checking is suppressed.
///
/// Jump table compression works by splitting the table into segments. There are a few types of segments for various
/// `when` value configurations. Many different segment configurations are generated, which are then evaluated for
/// efficiency, and the best possible optimization is chosen.
///
/// * Unless the range checking is suppressed, or the values are known to cover the minimum or maximum possible value,
///   a leading and trailing segment is created for values outside the expression's range.
/// * The segment needs to handle all targets within its range, including the `else` values.
/// * Code for choosing the correct segment for given input value is handled outside segments. Currently, bisection
///   search is used. The bisection routing is also responsible for handling the out-of-range values via the leading
///   and trailing segments (which represent values below and above the valid range, respectively).
/// * When the range check is omitted (either due to the `unsafe-case-optimization` compiler option or when handling
///   Mindustry content, possibly with `target-optimization` set to `specific`), the leading and/or trailing segments
///   are not generated.
///
/// **Segment types**
///
/// * SINGLE: the segment represents a continuous region of a single target. There's no logic involved, the SINGLE
///   segment is realized by a jump to the target body. When possible, this jump is inlined into the bisection table.
/// * MIXED: the segments represent a continuous region having a single majority target and a few exceptions. The
///   segment is realized by explicitly handling the targets first, and then jumping to the majority target.
/// * JUMP_TABLE: the segments represent a continuous region having a variety of targets. The segment is realized as
///   a jump table, or alternatively, as a selection table when the size of the segment is low.
///
/// Possible improvement: identify and handle continuous ranges of identical targets within MIXED or
/// SELECTION_TABLE segments.
///
/// The following dynamic attributes are defined for each segment:
///
/// * handleNulls: true if the segment needs to handle nulls for else values within or below its range.
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

        targets.computeElseValues(analyzer.contentType, metadata, getProfile().isTargetOptimization());

        SegmentConfigurationGenerator segmentConfigurationGenerator = new CombinatorialSegmentConfigurationGenerator(targets, analyzer.contentType != ContentType.UNKNOWN,
                getProfile().getCaseOptimizationStrength(), 1);

        // When no range checking, don't bother trying to merge segments.
        Set<SegmentConfiguration> configurations = removeRangeCheck
                ? Set.of(new SegmentConfiguration(segmentConfigurationGenerator.getPartitions(), List.of()))
                : segmentConfigurationGenerator.createSegmentConfigurations();

        debugOutput("Singular segments: \n" + segmentConfigurationGenerator.getPartitions().stream()
                .map(Object::toString).collect(Collectors.joining("\n")));
        debugOutput("Segment configurations: %,d", configurations.size());

        optimizationContext.addDiagnosticData(new CaseSwitcherConfigurations(context.sourcePosition(), configurations.size()));

        List<ConvertCaseExpressionAction> actions = new ArrayList<>();
        for (SegmentConfiguration segmentConfiguration : configurations) {
            createOptimizationActions(actions, context, jumps, valueSteps, variable, targets, analyzer,segmentConfiguration, removeRangeCheck);
        }

        actionCounter = 0;
        debugOutput("%nCase switching optimization: %,d distinct configurations generated.%n", actions.size());

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

    private void createOptimizationActions(List<ConvertCaseExpressionAction> actions, AstContext astContext,
            int jumps, int valueSteps, LogicVariable variable, Targets targets, WhenValueAnalyzer analyzer,
            SegmentConfiguration segmentConfiguration, boolean removeRangeCheck) {
        ContentType contentType = analyzer.getContentType();
        List<Segment> segments = segmentConfiguration.createSegments(removeRangeCheck, analyzer.isMindustryContent(), targets);

        actions.add(new ConvertCaseExpressionAction(astContext, jumps, valueSteps, variable, targets,
                segments, contentType, removeRangeCheck, false, false));

        int lowPadIndex = findLowPadSegment(segments);
        int highPadIndex = findHighPadSegment(targets, contentType, segments);

        if (lowPadIndex >= 0) {
            actions.add(new ConvertCaseExpressionAction(astContext, jumps, valueSteps, variable, targets,
                    padLow(segments, contentType, lowPadIndex), contentType, removeRangeCheck, true, false));
        }

        if (highPadIndex >= 0) {
            actions.add(new ConvertCaseExpressionAction(astContext, jumps, valueSteps, variable, targets,
                    padHigh(segments, targets, highPadIndex), contentType, removeRangeCheck, false, true));
        }

        if (lowPadIndex >= 0 && highPadIndex >= 0) {
            actions.add(new ConvertCaseExpressionAction(astContext, jumps, valueSteps, variable, targets,
                    padLow(padHigh(segments, targets, highPadIndex), contentType, lowPadIndex),
                    contentType, removeRangeCheck, true, true));
        }
    }

    private List<Segment> padLow(List<Segment> segments, ContentType contentType, int index) {
        if (index == 0) {
            return List.of(segments.getFirst().limitLow(0));
        } else {
            List<Segment> newSegments = new ArrayList<>(segments);
            Segment prev = newSegments.get(index - 1);
            Segment curr = newSegments.get(index);

            if (contentType == ContentType.UNKNOWN) {
                if (prev.from() < 0) {
                    newSegments.set(index - 1, prev.limitHigh(0));
                } else {
                    newSegments.set(index - 1, new Segment(prev.type(), 0, 0, prev.majorityLabel(), 0));
                }
                newSegments.set(index, curr.limitLow(0));
                if (prev.handleNulls()) {
                    newSegments.get(index).setHandleNulls();
                }
            } else {
                if (index != 1 || prev.from() != 0) throw new MindcodeInternalError("Mindustry content not starting at index 0");
                newSegments.removeFirst();
                newSegments.set(0, curr.limitLow(0));
                if (prev.handleNulls()) {
                    newSegments.getFirst().setHandleNulls();
                }
            }

            return newSegments;
        }
    }

    private List<Segment> padHigh(List<Segment> segments, Targets targets, int index) {
        List<Segment> newSegments = new ArrayList<>(segments);
        Segment curr = newSegments.get(index);
        newSegments.set(index, curr.limitHigh(targets.getTotalSize()));
        if (index < segments.size() - 1) {
            if (index != segments.size() - 2) throw new MindcodeInternalError("Pad high segment not the last one");
            newSegments.removeLast();
        }

        return newSegments;
    }

    private int findLowPadSegment(List<Segment> segments) {
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

    private int findHighPadSegment(Targets targets, ContentType contentType, List<Segment> segments) {
        // Only high pad Mindustry content when target optimization is set
        // When there is just one segment, it is because range checking is off, in which case padding high makes no sense
        if (contentType == ContentType.UNKNOWN || !getProfile().isTargetOptimization() || segments.size() < 2) return -1;

        // It can only be the very last segment
        int lastPossibleIndex = segments.getLast().type() == SegmentType.JUMP_TABLE ? segments.size() - 1 : segments.size() - 2;
        Segment segment = segments.get(lastPossibleIndex);
        if (segment.to() <= targets.getTotalSize() && segment.type() == SegmentType.JUMP_TABLE
                && (segments.getLast() == segment || segments.getLast().empty())) {
            return lastPossibleIndex;
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
        private final boolean symbolic = getProfile().isSymbolicLabels();
        private final String padding;

        private ConvertCaseExpressionAction(AstContext astContext, int jumps, int valueSteps, LogicVariable variable,
                Targets targets, List<Segment> segments, ContentType contentType, boolean removeRangeCheck,
                boolean lowPadded, boolean highPadded) {
            this.astContext = astContext;
            this.variable = variable;
            this.targets = targets;
            this.segments = segments;
            this.logicConversion = contentType != ContentType.UNKNOWN;
            this.removeRangeCheck = removeRangeCheck;
            this.padding = switch ((lowPadded ? 1 : 0) + (highPadded ? 2 : 0)) {
                case 1 -> "padded low";
                case 2 -> "padded high";
                case 3 -> "padded both";
                default -> "";
            };

            debugOutput("%n%n*** %s ***%n", this);

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
                    " (" + strId + "segments: " + numSegments + (padding.isEmpty() ? "" : ", " + padding) + ")";
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

        private boolean considerElse() {
            return targets.hasElseBranch() && targets.getElseValues() > 0;
        }

        // Per target, not totals
        int targetSteps = 0;
        int elseSteps = 0;

        private void computeCostAndBenefit(int originalJumps, int originalValueSteps) {
//            if (caseConfiguration == actionCounter) {
//                System.out.print(""); // For breakpoint
//            }

            // One time costs
            int contentCost = logicConversion ? 1 : 0;          // Cost of converting type to logic ID
            cost = contentCost - originalJumps;
            targetSteps = contentCost * targets.size();
            elseSteps = contentCost * targets.getElseValues();

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
                    targetSteps++;
                } else {
                    // There's a handler on the else branch
                    debugOutput("Null handling: instructions: 1 (null handling steps accounted for in individual segments)");
                }
                cost++;
            }

            int bisectionSteps = computeBisectionTable(segments, 0);

            segments.forEach(this::computeSegmentCostAndBenefit);

            if (considerElse()) {
                int originalSteps = originalValueSteps + targets.getElseValues() * originalJumps;

                debugOutput("Original steps: %d, new steps: %d (target: %d, else: %d; bisection: %s)",
                        originalSteps, targetSteps + elseSteps, targetSteps, elseSteps, bisectionSteps);

                rawBenefit = (double) (originalSteps - targetSteps - elseSteps) / targets.getTotalSize();
            } else {
                // We disregard else steps in both computations
                debugOutput("Original steps: %d, new steps: %d (target: %d, disregarding else; bisection: %d)", originalValueSteps,
                        targetSteps, targetSteps, bisectionSteps);

                rawBenefit = (double) (originalValueSteps - targetSteps) / targets.size();
            }
            benefit = rawBenefit * astContext.totalWeight();

            debugOutput("Original size: %d, new size: %d, cost: %d", originalJumps, cost + originalJumps, cost);
            if (!padding.isEmpty()) debugOutput("*** " + padding.toUpperCase() + " ***");
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
            int activeTargets = 0;
            for (int target : targets.keySet()) {
                if (segment.contains(target)) activeTargets++;
            }

            SegmentStats stats = switch (segment.type()) {
                case SINGLE -> computeSingleSegment(segment, activeTargets);
                case MIXED -> computeMixedSegment(segment, activeTargets);
                case JUMP_TABLE -> computeJumpTable(segment, activeTargets);
            };

            if (isDebugOutput()) {
                debugOutput("Segment %3d to %3d: %s, %s, depth %d, bisection steps: %d, total steps: %d",
                        segment.from(), segment.to(), segment.typeName(), stats, segment.depth(),
                        segment.depth() * (stats.values + stats.elseValues), stats.steps + stats.elseSteps);
            }

            cost += stats.size;
            targetSteps += stats.steps + segment.depth() * stats.values;
            elseSteps += stats.elseSteps + segment.depth() * stats.elseValues;
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

        private SegmentStats computeSingleSegment(Segment segment, int activeTargets) {
            boolean handleNull = logicConversion && segment.from() == 0;

            int size = segment.inline() ? 0 : 1;
            int steps = segment.inline() ? 0 : segment.size();
            int nullSteps = segment.handleNulls() ? segment.size() : 0;  // Only for empty segments

            if (segment.empty()) {
                return new SegmentStats(size, 0, 0, segment.size(), steps + nullSteps);
            } else {
                return new SegmentStats(size, segment.size(), steps, 0, 0);
            }
        }

        private LogicLabel findSingleSegmentTarget(Segment segment, LogicLabel finalLabel) {
            return segment.empty() ? segment.handleNulls() ? targets.getNullOrElseTarget() : finalLabel
                    : segment.majorityLabel() == LogicLabel.INVALID ? targets.getExisting(0)
                    : segment.majorityLabel();
        }

        private void generateSingleSegment(Segment segment, LogicLabel finalLabel) {
            assert newAstContext != null;
            insertInstruction(createJumpUnconditional(newAstContext, findSingleSegmentTarget(segment, finalLabel)));
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
                        activeTargets * (activeTargets + 1) / 2,
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
            return symbolic && from != 0 ? 3 : 2;
        }

        private SegmentStats computeJumpTable(Segment segment, int activeTargets) {
            int minorityTargets = segment.size() - segment.majoritySize();
            if (!removeRangeCheck && (minorityTargets <= maxSelectEntries(segment.from()))) {
                return computeMixedSegment(segment, activeTargets);
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
            int minorityTargets = segment.size() - segment.majoritySize();
            if (!removeRangeCheck && (minorityTargets <= maxSelectEntries(segment.from()))) {
                generateMixedSegment(segment, finalLabel);
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

        private int computeBisectionTable(List<Segment> segments, int depth) {
            int bisection = bisect(segments);
            if (bisection < 0) {
                segments.forEach(segment -> {
                    segment.setBisectionSteps(depth, false);
                    debugOutput("    %s",  segment);
                });
                return 0;
            }
            cost++;

            List<Segment> lowSegments = segments.subList(0, bisection);
            List<Segment> highSegments = segments.subList(bisection, segments.size());
            InlineSegment inlineSegment = findInlinedSegment(lowSegments, highSegments);

            int min = segments.getFirst().from();
            int max = segments.getLast().to();
            int steps = max - min;
            debugOutput("Bisection at %d (%d to %d)", highSegments.getFirst().from(), min, max);

            int nextDepth = depth + 1;

            if (inlineSegment == InlineSegment.LOW) {
                lowSegments.getFirst().setBisectionSteps(nextDepth, true);
                debugOutput("    %s",  lowSegments.getFirst());
                return steps + computeBisectionTable(highSegments, nextDepth);
            } else if (inlineSegment == InlineSegment.HIGH) {
                highSegments.getFirst().setBisectionSteps(nextDepth, true);
                debugOutput("    %s",  highSegments.getFirst());
                return steps + computeBisectionTable(lowSegments, nextDepth);
            } else {
                return steps
                        + computeBisectionTable(lowSegments, nextDepth)
                        + computeBisectionTable(highSegments, nextDepth);
            }
        }

        void generateBisectionTable(List<Segment> segments, LogicLabel finalLabel) {
            assert newAstContext != null;
            assert caseVariable != null;

            int bisection = bisect(segments);

            if (bisection >= 0) {
                List<Segment> lowSegments = segments.subList(0, bisection);
                List<Segment> highSegments = segments.subList(bisection, segments.size());
                InlineSegment inlineSegment = findInlinedSegment(lowSegments, highSegments);

                if (inlineSegment == InlineSegment.LOW) {
                    LogicLabel target = findSingleSegmentTarget(lowSegments.getFirst(), finalLabel);
                    insertInstruction(createJump(newAstContext, target, Condition.LESS_THAN, caseVariable,
                            LogicNumber.create(highSegments.getFirst().from())));
                    generateBisectionTable(highSegments, finalLabel);
                } else if (inlineSegment == InlineSegment.HIGH) {
                    LogicLabel target = findSingleSegmentTarget(highSegments.getFirst(), finalLabel);
                    insertInstruction(createJump(newAstContext, target, Condition.GREATER_THAN_EQ, caseVariable,
                            LogicNumber.create(highSegments.getFirst().from())));
                    generateBisectionTable(lowSegments, finalLabel);
                } else {
                    LogicLabel highTarget = instructionProcessor.nextLabel();
                    insertInstruction(createJump(newAstContext, highTarget, Condition.GREATER_THAN_EQ, caseVariable,
                            LogicNumber.create(highSegments.getFirst().from())));
                    generateBisectionTable(lowSegments, finalLabel);
                    insertInstruction(createLabel(newAstContext, highTarget));
                    generateBisectionTable(highSegments, finalLabel);
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

        private enum InlineSegment { NONE, LOW, HIGH }

        private InlineSegment findInlinedSegment(List<Segment> lowSegments, List<Segment> highSegments) {
            boolean low = canInline(lowSegments);
            boolean high = canInline(highSegments);

            if (low && high) {
                // Both can be inlined. Inline the larger one - saves more steps
                return size(lowSegments.getFirst()) > size(highSegments.getFirst()) ? InlineSegment.LOW : InlineSegment.HIGH;
            } else {
                return low ? InlineSegment.LOW : high ? InlineSegment.HIGH : InlineSegment.NONE;
            }
        }

        private int size(Segment segment) {
            // Empty segments have zero steps when `else` path is not considered
            return segment.empty() && !considerElse() ? 0 : segment.size();
        }

        // Returns true if the segment can be handled by a simple jump right from the bisection table.
        // A direct segment is either empty or isolated. Two direct segments cannot touch.
        // This is ensured by marking a non-empty, single segment isolated only if it doesn't touch an empty segment.
        boolean canInline(List<Segment> segments) {
            return segments.size() == 1 && (segments.getFirst().type() == SegmentType.SINGLE);
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

            generateBisectionTable(segments, finalLabel);

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
    record SegmentStats(int size, int values, int steps, int elseValues, int elseSteps) {
    }
}
