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
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;

// Notes on null handling:
//
// When the type of the input value is numeric, nulls are not supported and are not specifically handled.
// If a `when` clause contain null value for a numerical case expression, the optimization won't be applied.
//
// When the type of the input value is a Mindustry content items, nulls are handled whether the expression contains
// a `when null` clause or not. If the `when null` is present, nulls are redirected to the `null` branch, otherwise
// nulls are redirected to the `else` branch. Nulls are handled by a _null check_ installed at the beginning of
// a `when` branch body, which jumps to the `null`/`else` branch when null is detected.
//
// Null check on the zero branch adds one instruction and 1/n execution steps.
// Null check on the else branch adds one instruction and no execution steps.
//
// If a zero `when` value is present, the null check is installed on the zero branch, and the zero value is
// redirected to the null check. In the opposite case, the null check is installed on the `else` branch, and
// jumps for unhandled values that might be null are redirected to the null check, others to the `else` branch
// proper.
//
// Zero padding is established by adding a zero target leading to an else branch, which is then supplied with
// a null check.
@NullMarked
class CaseSwitcher extends BaseOptimizer {
    private static final boolean DEBUG = false;
    private static final boolean OPTIMIZE_SEGMENTS = true;

    private static final int MAXIMAL_GAP = 4;

    private static final int MINIMAL_SEGMENT_SIZE = 4;
    private static final int MAX_EXCEPTIONS_WHEN = 2;
    private static final int MAX_EXCEPTIONS_ELSE = 3;

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

    static List<Segment> split(Targets targets) {
        List<Segment> segments = new ArrayList<>();

        LogicLabel label = targets.firstEntry().getValue();
        int start = targets.firstKey();
        int last = targets.firstKey();

        // Else branch gaps
        for (Map.Entry<Integer, LogicLabel> entry : targets.entrySet()) {
            int key = entry.getKey();

            if (key - last > 1) {
                segments.add(new Segment(SegmentType.SINGLE, start, last + 1, label));
                segments.add(new Segment(SegmentType.SINGLE, last + 1, key, LogicLabel.EMPTY));
                label = entry.getValue();
                start = key;
            } else if (!label.equals(entry.getValue())) {
                segments.add(new Segment(SegmentType.SINGLE, start, key, label));
                label = entry.getValue();
                start = key;
            }

            last = key;
        }

        segments.add(new Segment(SegmentType.SINGLE, start, last + 1, label));

        return segments;
    }

    // Rules for merging segments:
    // 1. Number of exceptions in the segment is <= MAX_EXCEPTIONS
    // 2. The merged segment starts and stops with the same label, or has no neighbor at one or both ends
    // 3. The average segment size > 1
    static Segment findLargestSegment(List<Segment> segments) {
        LogicLabel majorityLabel = LogicLabel.EMPTY;
        SegmentType type = SegmentType.SINGLE;
        int largest = -1;
        int from = 0, to = 0;

        for (int i = 0; i < segments.size(); i++) {
            Segment start = segments.get(i);
            Segment stop = start;
            Segment last = start;

            boolean startGap = i == 0 || segments.get(i - 1).to != start.from;
            LogicLabel label = start.majorityLabel;
            int size = start.size();
            int lastSize = size;
            int count = 1;
            int exceptions = 0;
            int maxExceptions = label == LogicLabel.EMPTY ? MAX_EXCEPTIONS_ELSE : MAX_EXCEPTIONS_WHEN;

            for (int j = i + 1; j < segments.size(); j++) {
                Segment next = segments.get(j);

                // A hole in the middle is not allowed
                if (next.from != last.to) break;

                size += next.size();
                count++;
                if (!label.equals(next.majorityLabel)) {
                    exceptions += next.size();
                }

                // Exceeded exceptions
                if (exceptions > maxExceptions) {
                    break;
                }

                if (size > count) {
                    if (next.majorityLabel.equals(label) || startGap || j == segments.size() - 1 || segments.get(j + 1).from != next.to) {
                        // The merged segment may stop here
                        // Last never points at an impossible end segment
                        stop = next;
                        lastSize = size;
                    }
                }

                last = next;
            }

            if (lastSize > largest) {
                majorityLabel = label;
                largest = lastSize;
                from = start.from;
                to = stop.to;
                type = start == stop ? SegmentType.SINGLE : SegmentType.MIXED;
            }

        }

        return new Segment(type, from, to, majorityLabel);
    }

    static List<Segment> mergeSegments(List<Segment> segmentList) {
        List<Segment> segments = new ArrayList<>(segmentList);
        List<Segment> result = new ArrayList<>();

        while (segments.size() > 1) {
            Segment largest = findLargestSegment(segments);
            if (largest.size() < MINIMAL_SEGMENT_SIZE) break;

            result.add(largest);
            segments.removeIf(largest::contains);
        }

        // They're already sorted from largest to smallest
        return result;
    }

    static List<Segment> completeSegments(List<Segment> singularSegments, List<Segment> mergedSegments) {
        List<Segment> result = new ArrayList<>(mergedSegments);
        List<Segment> segments = new ArrayList<>(singularSegments);
        for (Segment mergedSegment : mergedSegments) {
            segments.removeIf(mergedSegment::contains);
        }

        int i = 0;
        while (i < segments.size()) {
            Segment segment = segments.get(i);
            int j = i + 1;
            while (j < segments.size() && segments.get(j).follows(segments.get(j - 1))) j++;
            result.add(new Segment(i == j - 1 ? SegmentType.SINGLE : SegmentType.JUMP_TABLE,
                    segment.from, segments.get(j - 1).to, segment.majorityLabel));
            i = j;
        }

        // Remove completely empty segments
        result.removeIf(segment -> segment.type == SegmentType.SINGLE && segment.majorityLabel == LogicLabel.EMPTY);
        result.sort(null);

        if (DEBUG) {
            //System.out.println("Singular segments: \n" + singularSegments.stream().map(Object::toString).collect(Collectors.joining("\n")));
            System.out.println("Merged segments: \n" + mergedSegments.stream().map(Object::toString).collect(Collectors.joining("\n")));
            System.out.println("Result: \n" + result.stream().map(Object::toString).collect(Collectors.joining("\n")));
            System.out.println();
            System.out.println();
        }

        return result;
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

        int removed = 0;

        WhenValueAnalyzer analyzer = new WhenValueAnalyzer();

        try (LogicIterator iterator = createIteratorAtContext(context)) {
            while (iterator.hasNext()) {
                LogicInstruction ix = iterator.next();
                if (ix.getAstContext().parent() == context && ix.getAstContext().matches(CONDITION) || matcher.matches(ix.getAstContext())) {
                    if (ix instanceof JumpInstruction jump) {
                        removed++;

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

        // Unsupported case expressions: no branches, or null and integers
        if (variable == null || targets.isEmpty() || analyzer.hasNull && analyzer.contentType == ContentType.UNKNOWN) return;

        LogicLabel zeroTarget = targets.get(0);
        if (analyzer.contentType != ContentType.UNKNOWN && zeroTarget != null) {
            // We need to handle zero separately because of possible null
            // Use INVALID as a placeholder
            targets.put(0, LogicLabel.INVALID);
        }

        List<Segment> segments = split(targets);

        // When no range checking, don't bother trying to merge segments.
        List<Segment> merged = removeRangeCheck ? List.of() : mergeSegments(segments);

        if (zeroTarget != null) targets.put(0, zeroTarget);

        if (DEBUG) {
            System.out.println("Singular segments: \n" + segments.stream().map(Object::toString).collect(Collectors.joining("\n")));
        }

        List<ConvertCaseExpressionAction> actions = new ArrayList<>();
        ConvertCaseExpressionAction action = createOptimizationActions(actions, costLimit, context, removed, variable, targets,
                completeSegments(segments, List.of()), analyzer.getContentType(), removeRangeCheck);

        if (!OPTIMIZE_SEGMENTS) return;

        double lastBenefit = action.benefit();
        for (int i = 0; i < merged.size(); i++) {
            action = createOptimizationActions(actions, costLimit, context, removed, variable, targets,
                    completeSegments(segments, merged.subList(0, i + 1)), analyzer.getContentType(),
                    removeRangeCheck);
        }

        if (!actions.isEmpty()) {
            // Comparing by cost (ascending) then by benefit (descending)
            actions.sort(Comparator.comparing(OptimizationAction::cost).thenComparing(Comparator.comparing(OptimizationAction::benefit).reversed()));
            ConvertCaseExpressionAction last = actions.getFirst();
            for (int i = 1; i < actions.size(); ) {
                ConvertCaseExpressionAction next = actions.get(i);
                if (next.benefit() <= last.benefit()) {
                    actions.remove(i);
                } else {
                    last = next;
                    i++;
                }
            }

            result.addAll(actions);
        }
    }

    private ConvertCaseExpressionAction createOptimizationActions(List<ConvertCaseExpressionAction> actions, int costLimit, AstContext astContext,
            int removed, LogicVariable variable, Targets targets, List<Segment> segments, ContentType contentType, boolean removeRangeCheck) {
        if (segments.stream().noneMatch(s -> s.contains(0))) {
            ConvertCaseExpressionAction action = new ConvertCaseExpressionAction(astContext, removed, variable, targets, segments,
                    contentType, removeRangeCheck, true);
            if (action.benefit() > 0 && action.cost() <= costLimit) {
                actions.add(action);
            }
            if (!action.zeroPadded) return action;
        }

        ConvertCaseExpressionAction action = new ConvertCaseExpressionAction(astContext, removed, variable, targets, segments,
                contentType, removeRangeCheck, false);
        if (action.benefit() > 0 && action.cost() <= costLimit) {
            actions.add(action);
        }
        return action;
    }

    private class ConvertCaseExpressionAction implements OptimizationAction {
        private final AstContext astContext;
        private final int group = groupCount;
        private int cost;
        private double benefit;
        private final LogicVariable variable;
        private final Targets targets;
        private final List<Segment> segments;
        private final boolean logicConversion;
        private final boolean removeRangeCheck;
        private final boolean padToZero;
        private final boolean symbolic = getProfile().isSymbolicLabels();

        private boolean zeroPadded = false;

        public ConvertCaseExpressionAction(AstContext astContext, int removed, LogicVariable variable,
                Targets targets, List<Segment> segments, ContentType contentType, boolean removeRangeCheck, boolean padToZero) {
            this.astContext = astContext;
            this.variable = variable;
            this.targets = targets;
            this.segments = segments;
            this.logicConversion = contentType != ContentType.UNKNOWN;
            this.removeRangeCheck = removeRangeCheck;
            this.padToZero = padToZero;
            debug("\n\n *** " + this + " *** \n");
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
            return Math.max(cost, 0);
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
            if (DEBUG) System.out.println(message);
        }

        @Override
        public String toString() {
            return "Convert case at " + Objects.requireNonNull(astContext.node()).sourcePosition().formatForLog() +
                    " (segments: " + segments.size() + (zeroPadded ? ", zero extension" : "") + ")";
        }

        private void computeCostAndBenefitNoRangeCheck(int removed) {
            int min = targets.firstKey();
            int max = targets.lastKey();

            if (padToZero) {
                if (min > 0) {
                    min = 0;
                    zeroPadded = true;
                }
            }

            int symbolicCost = symbolic && min != 0 ? 1 : 0;                // Cost of computing offset
            int contentCost = logicConversion ? 1 : 0;                      // Cost of converting type to logic ID
            int jumpTableCost = (max - min + 1) + 1;                        // Table size plus initial jump
            cost = jumpTableCost + symbolicCost + contentCost - removed;

            double executionSteps = 2 + symbolicCost + contentCost;         // MultiJump + jump table + additional costs
            double savedSteps = (removed + 1) / 2.0;
            benefit = (savedSteps - executionSteps) * astContext.totalWeight();
        }

        double executionSteps = 0.0;

        private void computeCostAndBenefit(int removed) {
            // One time costs
            int contentCost = logicConversion ? 1 : 0;          // Cost of converting type to logic ID
            cost = contentCost - removed;
            executionSteps = contentCost;

            debug(this);
            debug("Case expression initialization: instructions: " + contentCost
                    + ", average steps: " + contentCost + ", total steps: " + contentCost * targets.size());

            int lastTo = Integer.MIN_VALUE;
            for (Segment segment : segments) {
                computeSegmentCostAndBenefit(segment, lastTo);
                lastTo = segment.to;
            }

            // Account for null handling: an instruction per zero value
            if (logicConversion && targets.hasZeroKey()) {
                double nullHandling = 1.0 / targets.size();
                debug("Null handling: instructions: " + 1 + ", average steps: " + nullHandling + ", total steps: " + 1);
                executionSteps += nullHandling;
                cost++;
            }

            double originalSteps = (removed + 1) / 2.0;
            debug("Original steps: " + originalSteps + ", new steps: " + executionSteps);
            debug("Original size: " + removed + ", new size: " + (cost + removed) + ", cost: " + cost);
            if (zeroPadded) debug("*** ZERO PADDED ***");
            debug("");
            benefit = (originalSteps - executionSteps) * astContext.totalWeight();
        }

        private @Nullable AstContext newAstContext;
        private @Nullable LogicVariable caseVariable;
        private int index;

        private void computeSegmentCostAndBenefit(Segment segment, int lastTo) {
            // This segment matches the value of 0 and needs to distinguish it from null
            boolean handleNull = logicConversion && segment.from == 0;

            int allTargets = targets.size();
            int activeTargets = (int) targets.keySet().stream().filter(segment::contains).count();
            int remainingTargets = (int) targets.keySet().stream().filter(i -> i >= segment.from).count();
            int remainingSteps = remainingTargets - activeTargets;

            SegmentStats segmentStats = switch (segment.type) {
                case SINGLE -> computeSingleSegment(segment, activeTargets, lastTo);
                case MIXED -> computeMixedSegment(segment, activeTargets, lastTo);
                case JUMP_TABLE -> computeJumpTable(segment, activeTargets, lastTo);
            };

            double additionalSteps = (segmentStats.steps + remainingSteps) / allTargets;
            debug("Segment " + segment.from + " to " + segment.to + ": " + segment.type + ", instructions: " + segmentStats.size
                    + ", average steps: " + additionalSteps + ", total steps: " + additionalSteps * allTargets);

            cost += segmentStats.size;
            executionSteps += additionalSteps;
        }

        private SegmentStats computeSingleSegment(Segment segment, int activeTargets, int lastTo) {
            boolean last = segment == segments.getLast();
            boolean handleNull = logicConversion && segment.from == 0;

            if (segment.from == lastTo || segment.size() == 1) {
                return new SegmentStats(last ? 2 : 1, activeTargets);
            } else {
                return new SegmentStats(3, activeTargets * (handleNull ? 3 : 2));
            }
        }

        private void generateSingleSegment(Segment segment, int lastTo, LogicLabel nextSegmentLabel, LogicLabel finalLabel) {
            assert newAstContext != null;
            assert caseVariable != null;

            // This segment matches the value of 0 and needs to distinguish it from null
            boolean handleNull = logicConversion && segment.from == 0;

            // Invalid represents the zero key
            LogicLabel target = segment.majorityLabel == LogicLabel.INVALID ? targets.getExisting(0) : segment.majorityLabel;

            if (segment.from == lastTo || segment.size() == 1) {
                if (segment.from == lastTo) {
                    // This segment immediately follows last one
                    // We know values smaller than segment.to cannot appear here

                    // All values in this segment lead to target (regardless of size)
                    // We jump to target if value is smaller than segment.to
                    insertInstruction(createJump(newAstContext, target, Condition.LESS_THAN, caseVariable, LogicNumber.create(segment.to)));
                } else {
                    // segment.size() == 1
                    // This segment matches just the value of segment.from. Null/zero are distinguished in zero branch.
                    insertInstruction(createJump(newAstContext, target, Condition.EQUAL, caseVariable, LogicNumber.create(segment.from)));
                }

                // For last segment jump to else branch, otherwise fallthrough to the next segment
                if (nextSegmentLabel == finalLabel) {
                    insertInstruction(createJumpUnconditional(newAstContext, finalLabel));
                }
            } else {
                // This segment matches values between from and to
                // This can't be zero/null
                insertInstruction(createJump(newAstContext, nextSegmentLabel, Condition.GREATER_THAN_EQ, caseVariable, LogicNumber.create(segment.to)));

                if (handleNull) {
                    // This segment matches values from 0 to segment.to, and we need to distinguish 0 from null
                    insertInstruction(createJump(newAstContext, finalLabel, Condition.STRICT_EQUAL, caseVariable, LogicNull.NULL));
                    insertInstruction(createJumpUnconditional(newAstContext, target));
                } else {
                    insertInstruction(createJump(newAstContext, target, Condition.GREATER_THAN_EQ, caseVariable, LogicNumber.create(segment.from)));
                    insertInstruction(createJumpUnconditional(newAstContext, finalLabel));
                }
            }
        }

        private SegmentStats computeMixedSegment(Segment segment, int activeTargets, int lastTo) {
            if (segment.majorityLabel == LogicLabel.INVALID) {
                throw new IllegalStateException("Invalid label in mixed segment");
            }

            int jumpToNext = segment != segments.getLast() ? 1 : 0;

            if (segment.majorityLabel == LogicLabel.EMPTY) {
                // Majority branch is the else branch
                // There's additional leading jump (jumpToNext) that needs to be accounted for
                // Final jump is not counted, since it leads to the else branch
                return new SegmentStats(jumpToNext + activeTargets + 1,
                        activeTargets * (jumpToNext + (activeTargets + 1) / 2.0));
            } else {
                // Majority branch is a when branch
                int defaultTargets = (int) targets.entrySet().stream().filter(
                        e -> segment.contains(e.getKey()) && segment.majorityLabel.equals(e.getValue())).count();
                int branchTargets = activeTargets - defaultTargets;
                int elseTargets = segment.size() - activeTargets;

                // Execution steps depend on branch ordering. We'll compute it value by value
                int activeSteps = 0;
                int steps = jumpToNext;
                for (int value = segment.from; value < segment.to; value++) {
                    LogicLabel target = targets.get(value);
                    if (target == null) {
                        // Here is a jump to an else branch. It adds a step, but its execution isn't counted
                        steps++;
                    } else if (!segment.majorityLabel.equals(target)) {
                        // Here is a jump to a non-majority branch. It adds a step and is counted.
                        steps++;
                        activeSteps += steps;
                    }
                }

                // All default targets must go through all of the above, plus the less than jump, if any,
                // plus jump to the majority branch
                // LessThanJump gets skipped for segments other than first, or cases where it explicitly handles 0
                int lessThanJump = segment.from != lastTo || logicConversion && segment.from == 0 ? 0 : 1;
                steps += lessThanJump + 1;
                activeSteps += defaultTargets * steps;

                return new SegmentStats(jumpToNext + branchTargets + elseTargets + lessThanJump + 1, activeSteps);
            }
        }

        private void generateMixedSegment(Segment segment, int lastTo, LogicLabel nextSegmentLabel, LogicLabel finalLabel) {
            assert newAstContext != null;
            assert caseVariable != null;

            if (nextSegmentLabel != finalLabel) {
                insertInstruction(createJump(newAstContext, nextSegmentLabel, Condition.GREATER_THAN_EQ, caseVariable, LogicNumber.create(segment.to)));
            }

            LogicLabel defaultTarget = segment.majorityLabel == LogicLabel.EMPTY ? finalLabel : segment.majorityLabel;
            for (int value = segment.from; value < segment.to; value++) {
                LogicLabel target = targets.getOrDefault(value, finalLabel);
                if (!defaultTarget.equals(target)) {
                    insertInstruction(createJump(newAstContext, target, Condition.EQUAL, caseVariable, LogicNumber.create(value)));
                }
            }

            if (segment.from != lastTo && defaultTarget != finalLabel) {
                if (!logicConversion || segment.from != 0) {
                    // This is always first segment here, we may need to handle nulls
                    insertInstruction(createJump(newAstContext, targets.getNullOrElseTarget(), Condition.LESS_THAN, caseVariable, LogicNumber.create(segment.from)));
                }
            }

            // Nothing matched: jump to default
            insertInstruction(createJumpUnconditional(newAstContext, defaultTarget));
        }

        private int maxSelectEntries(int from, boolean last) {
            return (symbolic && from != 0 ? 4 : 3) + (last ? 1 : 0);
        }

        private SegmentStats computeJumpTable(Segment segment, int activeTargets, int lastTo) {
            boolean last = segment == segments.getLast();
            if (!removeRangeCheck && (activeTargets <= maxSelectEntries(segment.from, last))) {
                // It's a select table actually
                int jumpToNext = last ? 0 : 1;
                return new SegmentStats(jumpToNext + activeTargets + 1,
                        activeTargets * (jumpToNext + (activeTargets + 1) / 2.0));
            } else if (padToZero && segment.from > 0 && lastTo < 0) {
                zeroPadded = true;
                int rangeCheck = logicConversion ? 1 : 2;
                int multiJump = 1;
                int tableSize = segment.to;
                int leadingInstructions = rangeCheck + multiJump;
                return new SegmentStats(leadingInstructions + tableSize, activeTargets * (leadingInstructions + 1));
            } else {
                int rangeCheck = logicConversion && segment.from == 0 ? 1 : 2;
                int multiJump = symbolic && segment.from != 0 ? 2 : 1;
                int tableSize = segment.size();
                int leadingInstructions = rangeCheck + multiJump;
                return new SegmentStats(leadingInstructions + tableSize, activeTargets * (leadingInstructions + 1));
            }
        }

        // Generates a jump table
        // Values less than 'from' are sent to final label
        // Values greater than or equal to 'to' are sent to the next segment
        private void generateJumpTable(Segment segment, int lastTo, LogicLabel nextSegmentLabel, LogicLabel finalLabel) {
            boolean last = segment == segments.getLast();
            int activeTargets = (int) targets.keySet().stream().filter(segment::contains).count();
            if (!removeRangeCheck && (activeTargets <= maxSelectEntries(segment.from, last))) {
                generateSelectTable(segment, nextSegmentLabel, finalLabel);
                return;
            }

            assert newAstContext != null;
            assert caseVariable != null;

            int segmentFrom = padToZero && segment.from > 0 && lastTo < 0 ? 0 : segment.from;

            // Range check
            if (!removeRangeCheck) {
                insertInstruction(createJump(newAstContext, nextSegmentLabel, Condition.GREATER_THAN_EQ, caseVariable, LogicNumber.create(segment.to)));
                if (!(logicConversion && segmentFrom == 0)) {
                    // If it is first segment, we may need to handle nulls
                    LogicLabel target = segment == segments.getFirst() ? targets.getNullOrElseTarget() : finalLabel;
                    insertInstruction(createJump(newAstContext, target, Condition.LESS_THAN, caseVariable, LogicNumber.create(segmentFrom)));
                }
            }

            List<LogicLabel> labels = IntStream.range(segmentFrom, segment.to).mapToObj(i -> instructionProcessor.nextLabel()).toList();
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
                insertInstruction(createJump(newAstContext, nextSegmentLabel, Condition.GREATER_THAN_EQ, caseVariable, LogicNumber.create(segment.to)));
            }

            targets.subMap(segment.from, segment.to).forEach((value, label) ->
                    insertInstruction(createJump(newAstContext, label, Condition.EQUAL, caseVariable, LogicNumber.create(value))));

            // If it is first segment, we may need to handle nulls
            LogicLabel target = segment == segments.getFirst() ? targets.getNullOrElseTarget() : finalLabel;
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

            int lastTo = Integer.MIN_VALUE;
            for (Segment segment : segments) {
                debug("Optimizing segment " + segment + ", size: " + segment.size() + ", targets: " + targets.subMap(segment.from, segment.to).size());

                boolean last = segment == segments.getLast();
                LogicLabel nextSegmentLabel = last ? finalLabel : instructionProcessor.nextLabel();
                switch (segment.type) {
                    case SINGLE -> generateSingleSegment(segment, lastTo, nextSegmentLabel, finalLabel);
                    case MIXED -> generateMixedSegment(segment, lastTo, nextSegmentLabel, finalLabel);
                    case JUMP_TABLE -> generateJumpTable(segment, lastTo, nextSegmentLabel, finalLabel);
                }

                if (!last) {
                    assert newAstContext != null;
                    insertInstruction(createLabel(newAstContext, nextSegmentLabel));
                }

                lastTo = segment.to;
            }

            // Remove all conditions
            for (AstContext condition : astContext.findSubcontexts(CONDITION)) {
                removeMatchingInstructions(ix -> ix.belongsTo(condition));
            }

            count++;
            return OptimizationResult.REALIZED;
        }
    }

     static class Targets {
        private final NavigableMap<Integer, LogicLabel> targets = new TreeMap<>();
        private @Nullable LogicLabel nullTarget;        // Handles null only
        private @Nullable LogicLabel elseTarget;        // Handles else only
        private @Nullable LogicLabel nullOrElseTarget;  // Handles else or null

         public boolean hasZeroKey() {
             return targets.containsKey(0);}

        public @Nullable LogicLabel get(Integer key) {
            return targets.get(key);
        }

        public LogicLabel getExisting(Integer key) {
            return Objects.requireNonNull(targets.get(key));
        }

        public LogicLabel getOrDefault(Integer key, LogicLabel defaultValue) {
            return targets.getOrDefault(key, defaultValue);
        }

        public @Nullable LogicLabel put(@Nullable Integer key, LogicLabel value) {
            if (key == null) {
                LogicLabel previous = nullTarget;
                nullTarget = value;
                return previous;
            } else {
                return targets.put(key, value);
            }
        }

        public boolean isEmpty() {
            return targets.isEmpty();
        }

        public int size() {
            return targets.size();
        }

        public SortedMap<Integer, LogicLabel> subMap(int from, int to) {
            return targets.subMap(from, to);
        }

        public Map.Entry<Integer, LogicLabel> firstEntry() {
            return targets.firstEntry();
        }

        public Map.Entry<Integer, LogicLabel> lastEntry() {
            return targets.lastEntry();
        }

        public Integer firstKey() {
            return targets.firstKey();
        }

         public Integer lastKey() {
             return targets.lastKey();
         }

         public Set<Map.Entry<Integer, LogicLabel>> entrySet() {
            return targets.entrySet();
        }

        public Set<Integer> keySet() {
            return targets.keySet();
        }

         public LogicLabel getElseTarget() {
             return Objects.requireNonNull(elseTarget);
         }

         public LogicLabel getNullOrElseTarget() {
             return Objects.requireNonNull(nullOrElseTarget);
         }

         public LogicLabel getNullTarget() {
             return Objects.requireNonNull(nullTarget);
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

    enum SegmentType {
        /// A segment containing one distinct value
        SINGLE,

        // A merged segment containing one predominant value and a few possible exceptions
        MIXED,

        /// A segment containing multiple distinct values, produces a jump table
        JUMP_TABLE
    }

    // Note: to is exclusive
    record Segment(SegmentType type, int from, int to, LogicLabel majorityLabel) implements Comparable<Segment> {
        @Override
        public int compareTo(@NotNull CaseSwitcher.Segment o) {
            return Integer.compare(from, o.from);
        }

        public int size() {
            return to - from;
        }

        public boolean contains(Segment other) {
            return from < other.to && to > other.from;
        }

        public boolean touches(Segment other) {
            return from <= other.to && to >= other.from;
        }

        public boolean contains(int value) {
            return from <= value && value < to;
        }

        public boolean follows(Segment other) {
            return from == other.to;
        }
    }

    record SegmentStats(int size, double steps) {
    }
}
