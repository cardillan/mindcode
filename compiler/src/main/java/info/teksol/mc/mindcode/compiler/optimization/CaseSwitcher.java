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
/// * The segment needs to handle all targets within its range. It might or might not handle `else` values within
///   its range. If it doesn't handle them, they fall through to the next segment.
/// * Number of else values below the segment's range is one of segments' attributes. The cost of the execution path
///   up to a segment is accounted for these `else` values, but the segment must account for the execution path
///   of these additional `else` values through its own code.
/// * A segment has a majority label. This is the label of the `when` body which is most frequent in the segment.
///   Some segments may handle non-majority labels first and then jump to the majority label.
/// * The very first instruction of each segment must handle values larger than the segment's range, except
///   for the last segment. If the segment consists of just one instruction (conditional jump), it is permissible
///   to flow into the following segment naturally (by evaluating the conditional jump as `false`). See the first
///   point.
/// * The last segment must also handle values larger than its range (by sending them to the else branch),
///   but doesn't need to do so as the very first instruction.
///
/// The following dynamic attributes are defined for each segment:
///
/// * priorElseValues: the number of `else` values below the segment's range.
/// * handleNulls: true if the segment needs to handle nulls for else values within or below its range.
/// * last: true if the segment is last.
@NullMarked
public class CaseSwitcher extends BaseOptimizer {
    private static final boolean OPTIMIZE_SEGMENTS = true;

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

        LogicVariable variable = null;
        Targets targets = new Targets();

        List<AstContext> conditions = context.findSubcontexts(CONDITION);
        ContextMatcher matcher = new ContextMatcher(conditions);

        boolean removeRangeCheck = getProfile().isUnsafeCaseOptimization()
                && context.node() instanceof AstCaseExpression exp && !exp.isElseDefined();

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
        if (variable == null || targets.isEmpty() || analyzer.hasNull && analyzer.contentType == ContentType.UNKNOWN)
            return;

        SegmentMerger segmentMerger = new CombinatorialSegmentMerger(targets, analyzer.contentType != ContentType.UNKNOWN,
                getProfile().getCaseOptimizationStrength(), 1);

        // When no range checking, don't bother trying to merge segments.
        Set<SegmentConfiguration> configurations = removeRangeCheck
                ? Set.of(new SegmentConfiguration(segmentMerger.getPartitions(), List.of()))
                : segmentMerger.createSegmentConfigurations();

        debugOutput("Case switching optimization: %d distinct configurations generated (%d in total)", configurations.size(),
                segmentMerger.getConfigurationCount());

        debugOutput("Singular segments: \n" + segmentMerger.getPartitions().stream()
                .map(Object::toString).collect(Collectors.joining("\n")));


        optimizationContext.addDiagnosticData(new CaseSwitcherConfigurations(context.sourcePosition(), configurations.size()));

        List<ConvertCaseExpressionAction> actions = new ArrayList<>();
        for (SegmentConfiguration mergedSegments : configurations) {
            createOptimizationActions(actions, costLimit, context, jumps, valueSteps, variable, targets,
                    mergedSegments.createSegments(analyzer.isMindustryContent(), targets),
                    analyzer.getContentType(), removeRangeCheck);
            if (!OPTIMIZE_SEGMENTS) break;
        }

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
        if (segments.stream().noneMatch(s -> s.contains(0))) {
            ConvertCaseExpressionAction action = new ConvertCaseExpressionAction(astContext, jumps, valueSteps, variable, targets, segments,
                    contentType, removeRangeCheck, true);
            actions.add(action);
            if (!action.zeroPadded) return;
        }

        actions.add(new ConvertCaseExpressionAction(astContext, jumps, valueSteps, variable, targets, segments,
                contentType, removeRangeCheck, false));
    }

    private int computeTotalSize(ContentType contentType, Targets targets) {
        String lookupKeyword = contentType.getLookupKeyword();
        if (lookupKeyword == null) return targets.size();
        Map<Integer, ?> lookupMap = metadata.getLookupMap(lookupKeyword);
        return lookupMap == null ? targets.size() : lookupMap.size();
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
        private final boolean padToZero;
        private final boolean symbolic = getProfile().isSymbolicLabels();

        private final int totalSize;
        private final int elseValues;
        private final int elseValuesLow;
        private final int elseValuesHigh;

        // Indicates that zero padding actually occurred.
        private boolean zeroPadded = false;

        private ConvertCaseExpressionAction(AstContext astContext, int jumps, int valueSteps, LogicVariable variable,
                Targets targets, List<Segment> segments, ContentType contentType, boolean removeRangeCheck, boolean padToZero) {
            this.astContext = astContext;
            this.variable = variable;
            this.targets = targets;
            this.segments = segments;
            this.logicConversion = contentType != ContentType.UNKNOWN;
            this.removeRangeCheck = removeRangeCheck;
            this.padToZero = padToZero;

            totalSize = computeTotalSize(contentType, targets);
            elseValues = Math.max(totalSize - targets.size(), 0);
            elseValuesLow = totalSize > 0 ? targets.firstKey() : 0;
            elseValuesHigh = totalSize > 0 ? totalSize - targets.lastKey() - 1 : 0;

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
            String strId = CaseSwitcher.super.isDebugOutput() ? "#" + id + ", " : "";
            return "Convert case at " + Objects.requireNonNull(astContext.node()).sourcePosition().formatForLog() +
                    " (" + strId + "segments: " + segments.size() + (zeroPadded ? ", zero based" : "") + ")";
        }

        private void computeCostAndBenefitNoRangeCheck(int originalJumps, int originalValueSteps) {
            int min = targets.firstKey();
            int max = targets.lastKey();

            if (padToZero && min > 0) {
                min = 0;
                zeroPadded = true;
            }

            int symbolicCost = symbolic && min != 0 ? 1 : 0;                // Cost of computing offset
            int contentCost = logicConversion ? 1 : 0;                      // Cost of converting type to logic ID
            int jumpTableCost = (max - min + 1) + 1;                        // Table size plus initial jump
            cost = jumpTableCost + symbolicCost + contentCost - originalJumps;

            int executionSteps = 2 + symbolicCost + contentCost;            // 1x multiJump, 1x jump table, additional costs
            rawBenefit = (double) originalValueSteps / totalSize - executionSteps;
            benefit = rawBenefit * astContext.totalWeight();
        }

        double executionSteps = 0.0;

        private void computeCostAndBenefit(int originalJumps, int originalValueSteps) {
//            if (caseConfiguration == actionCounter) {
//                int breakpoint = 1; // For breakpoint
//            }

            // One time costs
            int contentCost = logicConversion ? 1 : 0;          // Cost of converting type to logic ID
            cost = contentCost - originalJumps;
            executionSteps = contentCost;

            debugOutput(this);
            debugOutput("Case expression initialization: instructions: " + contentCost
                    + ", average steps: " + contentCost + ", total steps: " + contentCost * totalSize);

            // Account for null handling: an instruction per zero value
            // Note: null handling when zero is not there is accounted for in individual segments
            if (logicConversion && targets.hasNullOrZeroKey()) {
                if (targets.hasZeroKey()) {
                    // There's a handler on the zero branch
                    double nullHandling = 1.0 / totalSize;
                    debugOutput("Null handling: instructions: 1, average steps: %g, total steps: 1", nullHandling);
                    executionSteps += nullHandling;
                } else {
                    // There's a handler on the else branch
                    debugOutput("Null handling: instructions: 1 (null handling steps accounted for in individual segments)");
                }
                cost++;
            }

            int lastTo = segments.getFirst().from();
            int priorElseValues = elseValuesLow;
            boolean forceHandleNulls = false;
            for (Segment segment : segments) {
                segment.setPriorElseValues(priorElseValues + (segment.from() - lastTo));
                if (forceHandleNulls) segment.setHandleNulls();
                SegmentStats segmentStats = computeSegmentCostAndBenefit(segment);
                priorElseValues = segmentStats.unhandled;
                forceHandleNulls = priorElseValues > 0 && segment.handleNulls();
                lastTo = segment.to();
            }

            double originalSteps = ((double) originalValueSteps + elseValues * originalJumps) / totalSize;

            debugOutput("Original steps: " + originalSteps + ", new steps: " + executionSteps);
            debugOutput("Original size: " + originalJumps + ", new size: " + (cost + originalJumps) + ", cost: " + cost);
            if (zeroPadded) debugOutput("*** ZERO PADDED ***");
            debugOutput("");
            rawBenefit = originalSteps - executionSteps;
            benefit = rawBenefit * astContext.totalWeight();
        }

        private @Nullable AstContext newAstContext;
        private @Nullable LogicVariable caseVariable;
        private int index;

        private SegmentStats computeSegmentCostAndBenefit(Segment segment) {
            if (caseConfiguration == actionCounter) {
                System.out.print(""); // For breakpoint
            }

            int allTargets = targets.size();
            int activeTargets = (int) targets.keySet().stream().filter(segment::contains).count();
            int remainingTargets = (int) targets.keySet().stream().filter(i -> i >= segment.from()).count();

            int remainingSteps = elseValues == 0
                    ? remainingTargets - activeTargets
                    : segment == segments.getLast() ? 0 : totalSize - segment.to();

            SegmentStats segmentStats = switch (segment.type()) {
                case SINGLE -> computeSingleSegment(segment, activeTargets);
                case MIXED -> computeMixedSegment(segment, activeTargets);
                case JUMP_TABLE -> computeJumpTable(segment, activeTargets);
            };

            double elseSteps = elseValues > 0 ? segmentStats.elseSteps : 0;
            int domainSize = elseValues > 0 ? totalSize : allTargets;
            double additionalSteps = (segmentStats.steps + elseSteps + remainingSteps) / domainSize;
            debugOutput("Segment " + segment.from() + " to " + segment.to() + ": " + segment.type() + ", " + segmentStats
                    + ", remaining steps: " + remainingSteps + ", total steps: " + (segmentStats.steps + segmentStats.elseSteps + remainingSteps));

            cost += segmentStats.size;
            executionSteps += additionalSteps;

            return segmentStats;
        }

        /// SINGLE SEGMENT
        ///
        /// Notes:
        ///
        /// * A single segment has only one target: the majority label, which cannot be an else branch.
        ///   As such, if it targets zero, the size is inevitably 1, since zero keys are singled out apart
        ///   the rest of the keys.
        /// * When the segment handles nulls, the else jumps are directed towards the else/null target. Nulls
        ///   aren't otherwise handled.
        ///
        /// If a single segment contains exactly one target, and all previous targets have been handled,
        /// it consists of one instruction which jumps to the target. If it is the last one, there's
        /// an additional jump to the else branch; otherwise the else branch is free.
        ///
        /// **There is a fallthrough.**
        ///
        /// If a single segment contains more targets, the first jump in a segment leads to the next segment
        /// for values higher than the segment maximum. Then, one of these situations happens:
        ///
        /// 1. The segment starts at 0 and nulls may appear: here we only need to distinguish the null value.
        ///    If the value is null, we jump to the proper label. All other values land at the branch target.
        ///    No need to distinguish null from zero on the zero branch.
        ///
        ///    §§§ verify it is properly accounted for. Maybe add a flag to segment to say it handles the null value.
        ///
        /// 2. The segment starts at a higher value, or nulls may not appear: The second jump leads to the
        ///    target branch, the third jump to the else branch.
        ///
        /// In both cases, it takes three steps to reach the else branch.
        private SegmentStats computeSingleSegment(Segment segment, int activeTargets) {
            boolean handleNull = logicConversion && segment.from() == 0;

            // Must not happen - is not accounted for in the computeSingleSegment
            if (segment.majorityLabel() == LogicLabel.EMPTY) {
                throw new MindcodeInternalError("Single segment's majority target cannot be the else branch.");
            }

            // Must not happen.
            if (activeTargets != segment.size()) {
                throw new MindcodeInternalError("Segment size mismatch.");
            }

            if (segment.priorElseValues() == 0 || segment.size() == 1) {
                int size = segment.last() ? 2 : 1;
                int elseValuesAbove = segment.last() ? elseValuesHigh : 0;
                int elseValues = segment.priorElseValues() + elseValuesAbove;

                // A single segment doesn't have any `else` values of its own.
                int unhandledElseValues = segment.last() ? 0 : elseValues;

                return new SegmentStats(
                        size,
                        activeTargets,
                        activeTargets,          // One step per value
                        elseValues,
                        size * elseValues,   // `size` steps per else value
                        unhandledElseValues);
            } else {
                // Always three instructions:
                // * jump to the next segment for values >= to (or `else` branch if last),
                // * jump to the `else` branch for values < from: 2 steps per `else` value,
                // * jump to the target branch (might be replaced by the target eventually): 3 steps per target value.
                return new SegmentStats(
                        3,
                        activeTargets,
                        3 * activeTargets,
                        segment.priorElseValues(),
                        2 * segment.priorElseValues());
            }
        }

        private void generateSingleSegment(Segment segment, LogicLabel nextSegmentLabel, LogicLabel finalLabel) {
            assert newAstContext != null;
            assert caseVariable != null;

            LogicLabel elseTarget = segment.handleNulls() ? targets.getNullOrElseTarget() : finalLabel;

            if (segment.priorElseValues() == 0 || segment.size() == 1) {
                // Invalid represents the zero key
                LogicLabel target = segment.majorityLabel() == LogicLabel.INVALID ? targets.getExisting(0) : segment.majorityLabel();

                if (segment.priorElseValues() == 0) {
                    // This segment immediately follows the last one
                    // We know values smaller than segment.to() cannot appear here

                    // All values in this segment lead to target (regardless of size)
                    // We jump to target if the value is smaller than segment.to()
                    insertInstruction(createJump(newAstContext, target, Condition.LESS_THAN, caseVariable, LogicNumber.create(segment.to())));
                } else {
                    // segment.size() == 1
                    // This segment matches just the value of segment.from(). Null/zero are distinguished in the zero branch.
                    insertInstruction(createJump(newAstContext, target, Condition.EQUAL, caseVariable, LogicNumber.create(segment.from())));
                }

                // When in the last segment, jump to the else branch, otherwise fallthrough to the next segment
                if (segment.last()) {
                    insertInstruction(createJumpUnconditional(newAstContext, elseTarget));
                }
            } else {
                // This segment matches values between from and to
                insertInstruction(createJump(newAstContext, nextSegmentLabel, Condition.GREATER_THAN_EQ, caseVariable, LogicNumber.create(segment.to())));
                insertInstruction(createJump(newAstContext, elseTarget, Condition.LESS_THAN, caseVariable, LogicNumber.create(segment.from())));
                insertInstruction(createJumpUnconditional(newAstContext, segment.majorityLabel()));
            }
        }

        /// MIXED SEGMENT
        ///
        /// The majority branch of a mixed segment can be an else brach or a value branch.
        ///
        /// **The majority branch is the else branch**
        ///
        /// 1. If the segment is not the last one, jump to the next segment.
        ///    Executed for all active and else values.
        /// 2. The select table. Contains a jump for all active targets. The number of steps is (n + 1) / 2.
        /// 3. Jump to the else branch.
        ///
        /// **The majority branch is a when branch**
        ///
        /// 1. If the segment is not the last one, jump to the next segment.
        ///    Executed for all active and else values.
        /// 2. The select table. Contains a jump for all non-majority targets. The number of steps for
        ///    non-majority targets is (n + 1) / 2. Counted separately for the else branch and the value branches.
        /// 3. If the segment is the last one, jump to the majority target for values above `segment.to()`
        /// 4. If there's a gap between this and the previous segment, and the majority label is not the else branch,
        ///    jump to the else branch (or null-else branch) for values smaller than `segment.from()`
        /// 5. A jump to the majority label.
        private SegmentStats computeMixedSegment(Segment segment, int activeTargets) {
            if (segment.majorityLabel() == LogicLabel.INVALID) {
                throw new IllegalStateException("Invalid label in mixed segment");
            }

            boolean gap = segment.priorElseValues() > 0;

            int jumpToNext = segment.last() ? 0 : 1;
            int elseValues = segment.size() - activeTargets;
            int elseValuesBelow = segment.priorElseValues();
            int elseValuesAbove = segment.last() ? elseValuesHigh : 0;

            if (segment.majorityLabel() == LogicLabel.EMPTY) {
                // Majority branch is the else branch
                // There's an additional leading jump (jumpToNext) that needs to be accounted for
                // Final jump counts as else branch
                return new SegmentStats(jumpToNext + activeTargets + 1,
                        activeTargets,
                        activeTargets * (jumpToNext + (activeTargets + 1) / 2.0),
                        elseValues + elseValuesAbove + elseValuesBelow,
                        (elseValues + elseValuesAbove + elseValuesBelow) * (jumpToNext + activeTargets + 1));
            } else {
                // Majority branch is a when branch
                int majorityTargets = (int) targets.entrySet().stream().filter(
                        e -> segment.contains(e.getKey()) && segment.majorityLabel().equals(e.getValue())).count();
                int branchTargets = activeTargets - majorityTargets;
                int elseTargets = segment.size() - activeTargets;

                int activeSteps = jumpToNext * activeTargets;
                int elseSteps = jumpToNext * (elseTargets + elseValuesAbove + elseValuesBelow);

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
                elseSteps += size * (elseValuesAbove + elseValuesBelow);

                // Instruction by instruction
                if (gap && segment.last()) {
                    if (segment.handleNulls()) elseSteps += elseValuesBelow;    // These values go through the null handler
                    size++; activeSteps += majorityTargets; elseSteps += elseValuesAbove + elseValuesBelow;
                    size++; activeSteps += majorityTargets; elseSteps += elseValuesAbove;
                    size++; activeSteps += majorityTargets;
                } else if (gap) {
                    if (segment.handleNulls()) elseSteps += elseValuesBelow;    // These values go through the null handler
                    size++; activeSteps += majorityTargets; elseSteps += elseValuesBelow;
                    size++; activeSteps += majorityTargets;
                } else if (segment.last()) {
                    size++; activeSteps += majorityTargets; elseSteps += elseValuesAbove;
                    size++; activeSteps += majorityTargets;
                } else {
                    size++; activeSteps += majorityTargets;
                }

                return new SegmentStats(
                        jumpToNext + size,
                        activeTargets,
                        activeSteps,
                        elseValues + elseValuesAbove + elseValuesBelow,
                        elseSteps);
            }
        }

        private void generateMixedSegment(Segment segment, LogicLabel nextSegmentLabel, LogicLabel finalLabel) {
            assert newAstContext != null;
            assert caseVariable != null;

            boolean gap = segment.priorElseValues() > 0;
            boolean majorityIsElse = segment.majorityLabel() == LogicLabel.EMPTY;

            if (!segment.last()) {
                insertInstruction(createJump(newAstContext, nextSegmentLabel, Condition.GREATER_THAN_EQ, caseVariable, LogicNumber.create(segment.to())));
            }

            LogicLabel majorityTarget = majorityIsElse ? finalLabel : segment.majorityLabel();
            for (int value = segment.from(); value < segment.to(); value++) {
                LogicLabel target = targets.getOrDefault(value, finalLabel);
                LogicLabel updatedTarget = value == 0 && target == finalLabel ? targets.getNullOrElseTarget() : target;
                if (!majorityTarget.equals(target)) {
                    insertInstruction(createJump(newAstContext, target, Condition.EQUAL, caseVariable, LogicNumber.create(value)));
                }
            }

            if (majorityIsElse || !gap && !segment.last()) {
                insertInstruction(createJumpUnconditional(newAstContext, majorityTarget));
            } else if (gap && segment.last()) {
                LogicLabel elseOrNullTarget = segment.handleNulls() ? targets.getNullOrElseTarget() : finalLabel;
                insertInstruction(createJump(newAstContext, elseOrNullTarget, Condition.LESS_THAN, caseVariable, LogicNumber.create(segment.from())));
                insertInstruction(createJump(newAstContext, finalLabel, Condition.GREATER_THAN_EQ, caseVariable, LogicNumber.create(segment.to())));
                insertInstruction(createJumpUnconditional(newAstContext, majorityTarget));
            } else if (gap) {
                LogicLabel elseOrNullTarget = segment.handleNulls() ? targets.getNullOrElseTarget() : finalLabel;
                insertInstruction(createJump(newAstContext, elseOrNullTarget, Condition.LESS_THAN, caseVariable, LogicNumber.create(segment.from())));
                insertInstruction(createJumpUnconditional(newAstContext, majorityTarget));
            } else {  // `segment.last()` is true
                insertInstruction(createJump(newAstContext, finalLabel, Condition.GREATER_THAN_EQ, caseVariable, LogicNumber.create(segment.to())));
                insertInstruction(createJumpUnconditional(newAstContext, majorityTarget));
            }
        }

        private int maxSelectEntries(int from, boolean last) {
            return (symbolic && from != 0 ? 4 : 3) + (last ? 1 : 0);
        }

        private SegmentStats computeJumpTable(Segment segment, int activeTargets) {
            if (!removeRangeCheck && (activeTargets <= maxSelectEntries(segment.from(), segment.last()))) {
                // It's a select table actually
                int elseValues = segment.priorElseValues() + segment.size() - activeTargets
                        + (segment.last() ? elseValuesHigh : 0);
                int jumpToNext = segment.last() ? 0 : 1;
                return new SegmentStats(jumpToNext + activeTargets + 1,
                        activeTargets,
                        activeTargets * (jumpToNext + (activeTargets + 1) / 2.0),
                        elseValues,
                        elseValues * (jumpToNext + activeTargets + 1));
            } else if (padToZero && segment.padToZero()) {
                zeroPadded = true;
                int rangeCheck = removeRangeCheck ? 0 : logicConversion ? 1 : 2;
                int multiJump = 1;
                int tableSize = segment.to();
                int leadingInstructions = rangeCheck + multiJump;
                int elseValues = tableSize - activeTargets;
                int elseValuesAbove = segment.last() ? elseValuesHigh : 0;
                int elseStepsAbove = rangeCheck * elseValuesAbove;
                int nullCheck = targets.hasNullKey() ? 1 : 0;

                // The table starts at 0: there's no check for else values
                return new SegmentStats(
                        leadingInstructions + tableSize,
                        activeTargets,
                        activeTargets * (leadingInstructions + 1),
                        elseValues + elseValuesAbove,
                        elseValues * (leadingInstructions + 1) + elseStepsAbove + nullCheck);
            } else {
                int rangeCheck = removeRangeCheck ? 0 : logicConversion && segment.from() == 0 ? 1 : 2;
                int multiJump = symbolic && segment.from() != 0 ? 2 : 1;
                int tableSize = segment.size();
                int leadingInstructions = rangeCheck + multiJump;
                int elseValuesInTable = tableSize - activeTargets;
                int elseStepsInTable = elseValuesInTable * (leadingInstructions + 1);
                int elseValuesBelow = segment.priorElseValues();
                int elseStepsBelow = elseValuesBelow * rangeCheck;
                int elseValuesAbove = segment.last() ? elseValuesHigh : 0;
                int elseStepsAbove = elseValuesAbove * (rangeCheck > 0 ? 1 : 0);

                // A null check is executed for else values below
                if (segment.handleNulls()) elseStepsBelow += segment.priorElseValues();

                return new SegmentStats(
                        leadingInstructions + tableSize,
                        activeTargets,
                        activeTargets * (leadingInstructions + 1),
                        elseValuesInTable + elseValuesBelow + elseValuesAbove,
                        elseStepsInTable + elseStepsBelow + elseStepsAbove);
            }
        }

        // Generates a jump table
        // Values less than 'from' are sent to the final label
        // Values greater than or equal to 'to' are sent to the next segment
        private void generateJumpTable(Segment segment, LogicLabel nextSegmentLabel, LogicLabel finalLabel) {
            boolean last = segment == segments.getLast();
            int activeTargets = (int) targets.keySet().stream().filter(segment::contains).count();
            if (!removeRangeCheck && (activeTargets <= maxSelectEntries(segment.from(), last))) {
                generateSelectTable(segment, nextSegmentLabel, finalLabel);
                return;
            }

            assert newAstContext != null;
            assert caseVariable != null;

            int segmentFrom = padToZero && segment.padToZero() ? 0 : segment.from();

            // Range check
            if (!removeRangeCheck) {
                insertInstruction(createJump(newAstContext, nextSegmentLabel, Condition.GREATER_THAN_EQ, caseVariable, LogicNumber.create(segment.to())));
                if (!(logicConversion && segmentFrom == 0)) {
                    LogicLabel target = segment.handleNulls() ? targets.getNullOrElseTarget() : finalLabel;
                    insertInstruction(createJump(newAstContext, target, Condition.LESS_THAN, caseVariable, LogicNumber.create(segmentFrom)));
                }
            }

            List<LogicLabel> labels = IntStream.range(segmentFrom, segment.to()).mapToObj(i -> instructionProcessor.nextLabel()).toList();
            LogicLabel marker = instructionProcessor.nextLabel();
            insertInstruction(createMultiJump(newAstContext, labels.getFirst(), caseVariable, LogicNumber.create(segmentFrom), marker));

            for (int i = 0; i < labels.size(); i++) {
                insertInstruction(createMultiLabel(newAstContext, labels.get(i), marker));
                LogicLabel target = targets.getOrDefault(segmentFrom + i, finalLabel);
                LogicLabel updatedTarget = segmentFrom + i == 0 && target == finalLabel ? targets.getNullOrElseTarget() : target;
                insertInstruction(createJumpUnconditional(newAstContext, updatedTarget));
            }
        }

        private void generateSelectTable(Segment segment, LogicLabel nextSegmentLabel, LogicLabel finalLabel) {
            assert newAstContext != null;
            assert caseVariable != null;

            if (nextSegmentLabel != finalLabel) {
                insertInstruction(createJump(newAstContext, nextSegmentLabel, Condition.GREATER_THAN_EQ, caseVariable, LogicNumber.create(segment.to())));
            }

            targets.subMap(segment.from(), segment.to()).forEach((value, label) ->
                    insertInstruction(createJump(newAstContext, label, Condition.EQUAL, caseVariable, LogicNumber.create(value))));

            // If it is the first segment, we may need to handle nulls
            LogicLabel target = segment.handleNulls() ? targets.getNullOrElseTarget() : finalLabel;
            insertInstruction(createJumpUnconditional(newAstContext, target));
        }

        protected void insertInstruction(LogicInstruction instruction) {
            optimizationContext.insertInstruction(index++, instruction);
        }

        private OptimizationResult convertCaseExpression(int costLimit) {
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
                        // There's an explicit null branch, and no zero branch
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

            for (Segment segment : segments) {
                debugOutput("Optimizing segment " + segment + ", size: " + segment.size() + ", targets: " + targets.subMap(segment.from(), segment.to()).size());

                LogicLabel nextSegmentLabel = segment.last() ? finalLabel : instructionProcessor.nextLabel();
                switch (segment.type()) {
                    case SINGLE -> generateSingleSegment(segment, nextSegmentLabel, finalLabel);
                    case MIXED -> generateMixedSegment(segment, nextSegmentLabel, finalLabel);
                    case JUMP_TABLE -> generateJumpTable(segment, nextSegmentLabel, finalLabel);
                }

                if (!segment.last()) {
                    assert newAstContext != null;
                    insertInstruction(createLabel(newAstContext, nextSegmentLabel));
                }
            }

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
    /// Null path execution time through the zero branch is not considered - this is handled centrally.
    /// Null path execution time through the else branch is counted in the else steps for all else values
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
    /// @param unhandled  Number of else values not handled by this segment and falling through to the next one
    record SegmentStats(int size, int values, double steps, int elseValues, double elseSteps, int unhandled) {
        public SegmentStats(int size, int values, double steps, int elseValues, double elseSteps) {
            this(size, values, steps, elseValues, elseSteps, 0);
        }
    }
}
