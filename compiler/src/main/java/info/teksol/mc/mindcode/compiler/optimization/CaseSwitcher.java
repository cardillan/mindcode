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

    static List<Segment> split(NavigableMap<Integer, LogicLabel> targets) {
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
        TreeMap<Integer, LogicLabel> targets = new TreeMap<>();

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
        List<Segment> segments = split(targets);
        List<Segment> merged = mergeSegments(segments);

        if (DEBUG) {
            System.out.println("Singular segments: \n" + segments.stream().map(Object::toString).collect(Collectors.joining("\n")));
        }

        boolean symbolic = getProfile().isSymbolicLabels();

        // Cost calculation
        int min = targets.firstEntry().getKey();

        // In symbolic labels mode, having min different from 0 requires an additional executable instruction
        // This expands the jump table instead to avoid that instruction
        if (symbolic && min > 0 && min < MAXIMAL_GAP) min = 0;

        ConvertCaseExpressionAction action = new ConvertCaseExpressionAction(context, removed, variable, targets,
                completeSegments(segments, List.of()), min, analyzer.getContentType(), removeRangeCheck);

        if (action.benefit() > 0 && action.cost() <= costLimit) {
            result.add(action);
        }

        if (removeRangeCheck) return;
        if (!OPTIMIZE_SEGMENTS) return;

        int lastCost = action.cost();
        int instructionSpace = getProfile().getInstructionLimit() - optimizationContext.getProgram().size();
        for (int i = 0; i < merged.size(); i++) {
            action = new ConvertCaseExpressionAction(context, removed, variable, targets,
                    completeSegments(segments, merged.subList(0, i + 1)), min, analyzer.getContentType(),
                    removeRangeCheck);
            if (action.cost() >= lastCost) break;

            if (action.benefit() > 0 && action.cost() <= costLimit) {
                result.add(action);
            }
            lastCost = action.cost();
        }
    }

    private class ConvertCaseExpressionAction implements OptimizationAction {
        private final AstContext astContext;
        private final int group = groupCount;
        private int cost;
        private double benefit;
        private final LogicVariable variable;
        private final TreeMap<Integer, LogicLabel> targets;
        private final List<Segment> segments;
        private final int min;
        private final boolean logicConversion;
        private final boolean removeRangeCheck;
        private final boolean symbolic = getProfile().isSymbolicLabels();

        public ConvertCaseExpressionAction(AstContext astContext, int removed, LogicVariable variable,
                TreeMap<Integer, LogicLabel> targets, List<Segment> segments, int min, ContentType contentType, boolean removeRangeCheck) {
            this.astContext = astContext;
            this.variable = variable;
            this.targets = targets;
            this.segments = segments;
            this.min = min;
            this.logicConversion = contentType != ContentType.UNKNOWN;
            this.removeRangeCheck = removeRangeCheck;
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
                    " (segments: " + segments.size() + ")";
        }

        private void computeCostAndBenefitNoRangeCheck(int removed) {
            int max = targets.lastEntry().getKey();
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
            int symbolicCost = symbolic && min != 0 ? 1 : 0;    // Cost of computing offset
            int contentCost = logicConversion ? 1 : 0;          // Cost of converting type to logic ID
            cost = contentCost - removed;
            executionSteps = contentCost;

            debug(this);
            debug("Case expression initialization: instructions: " + contentCost
                    + ", average steps: " + contentCost + ", total steps: " + contentCost * targets.size());

            int lastTo = Integer.MAX_VALUE;
            for (Segment segment : segments) {
                computeSegmentCostAndBenefit(segment, lastTo);
                lastTo = segment.to;
            }

            double originalSteps = (removed + 1) / 2.0;
            debug("Original steps: " + originalSteps + ", new steps: " + executionSteps);
            debug("Original size: " + removed + ", new size: " + (cost + removed) + ", cost: " + cost);
            debug("");
            benefit = (originalSteps - executionSteps) * astContext.totalWeight();
        }

        private @Nullable AstContext newAstContext;
        private @Nullable LogicVariable caseVariable;
        private int index;

        private int maxSelectEntries(int min) {
            return symbolic && min != 0 ? 4 : 3;
        }

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

            LogicLabel target = segment.majorityLabel;

            if (segment.from == lastTo || segment.size() == 1) {
                if (segment.from == lastTo) {
                    // This segment immediately follows last one
                    // We know values smaller than segmentTo cannot appear here

                    // All values in this segment lead to target (regardless of size)
                    // We jump to target if value is smaller than segment.to
                    insertInstruction(createJump(newAstContext, target, Condition.LESS_THAN, caseVariable, LogicNumber.create(segment.to)));
                } else if (handleNull) {
                    // segment.size() == 1
                    // This segment matches just the value of 0, and we need to distinguish it from null
                    insertInstruction(createJump(newAstContext, target, Condition.STRICT_EQUAL, caseVariable, LogicNumber.ZERO));
                } else {
                    // segment.size() == 1
                    // This segment matches just the value of segment.from.
                    insertInstruction(createJump(newAstContext, target, Condition.EQUAL, caseVariable, LogicNumber.create(segment.from)));
                }

                // For last segment jump to else branch, otherwise fallthrough to the next segment
                if (nextSegmentLabel == finalLabel) {
                    insertInstruction(createJumpUnconditional(newAstContext, finalLabel));
                }
            } else {
                // This segment matches values between from and to
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
                int lessThanJump = segment == segments.getFirst() ? 1 : 0;
                steps += lessThanJump + 1;
                activeSteps += defaultTargets * steps;

                return new SegmentStats(jumpToNext + branchTargets + elseTargets + lessThanJump + 1, activeSteps);
            }
        }

        private void generateMixedSegment(Segment segment, boolean firstSegment, LogicLabel nextSegmentLabel, LogicLabel finalLabel) {
            assert newAstContext != null;
            assert caseVariable != null;

            if (nextSegmentLabel != finalLabel) {
                insertInstruction(createJump(newAstContext, nextSegmentLabel, Condition.GREATER_THAN_EQ, caseVariable, LogicNumber.create(segment.to)));
            }

            LogicLabel defaultTarget = segment.majorityLabel == LogicLabel.EMPTY ? finalLabel : segment.majorityLabel;
            for (int value = segment.from; value < segment.to; value++) {
                LogicLabel target = targets.getOrDefault(value, finalLabel);
                if (!defaultTarget.equals(target)) {
                    boolean strict = logicConversion && value == 0;
                    insertInstruction(createJump(newAstContext, target, strict ? Condition.STRICT_EQUAL : Condition.EQUAL,
                            caseVariable, LogicNumber.create(value)));
                }
            }

            if (firstSegment && defaultTarget != finalLabel) {
                if (logicConversion && segment.from == 0) {
                    insertInstruction(createJump(newAstContext, finalLabel, Condition.STRICT_EQUAL, caseVariable, LogicNull.NULL));
                } else {
                    insertInstruction(createJump(newAstContext, finalLabel, Condition.LESS_THAN, caseVariable, LogicNumber.create(segment.from)));
                }
            }

            // Nothing matched: jump to default
            insertInstruction(createJumpUnconditional(newAstContext, defaultTarget));
        }

        private SegmentStats computeJumpTable(Segment segment, int activeTargets, int lastTo) {
            boolean first = segment == segments.getFirst();
            int segmentFrom = first ? min : segment.from;
            int leadingInstructions = 2 + (symbolic && segmentFrom != 0 ? 2 : 1);
            return new SegmentStats(leadingInstructions + segment.size(), activeTargets * (leadingInstructions + 1));
        }

        // Generates a jump table
        // Values less than 'from' are sent to final label
        // Values greater than or equal to 'to' are sent to the next segment
        private void generateJumpTable(Segment segment, int segmentFrom, LogicLabel nextSegmentLabel, LogicLabel finalLabel) {
            int activeTargets = (int) targets.keySet().stream().filter(i -> i >= segmentFrom && i < segment.to).count();
            if (!removeRangeCheck && (activeTargets <= maxSelectEntries(segmentFrom))) {
                generateSelectTable(segment, nextSegmentLabel, finalLabel);
                return;
            }

            assert newAstContext != null;
            assert caseVariable != null;

            // Range check
            if (!removeRangeCheck) {
                insertInstruction(createJump(newAstContext, nextSegmentLabel, Condition.GREATER_THAN_EQ, caseVariable, LogicNumber.create(segment.to)));
                if (logicConversion && segmentFrom == 0) {
                    insertInstruction(createJump(newAstContext, finalLabel, Condition.STRICT_EQUAL, caseVariable, LogicNull.NULL));
                } else {
                    insertInstruction(createJump(newAstContext, finalLabel, Condition.LESS_THAN, caseVariable, LogicNumber.create(segmentFrom)));
                }
            }

            List<LogicLabel> labels = IntStream.range(segmentFrom, segment.to).mapToObj(i -> instructionProcessor.nextLabel()).toList();
            LogicLabel marker = instructionProcessor.nextLabel();
            insertInstruction(createMultiJump(newAstContext, labels.getFirst(), caseVariable, LogicNumber.create(segmentFrom), marker));

            for (int i = 0; i < labels.size(); i++) {
                insertInstruction(createMultiLabel(newAstContext, labels.get(i), marker));
                insertInstruction(createJumpUnconditional(newAstContext, targets.getOrDefault(segmentFrom + i, finalLabel)));
            }
        }

        private void generateSelectTable(Segment segment, LogicLabel nextSegmentLabel, LogicLabel finalLabel) {
            assert newAstContext != null;
            assert caseVariable != null;

            if (nextSegmentLabel != finalLabel) {
                insertInstruction(createJump(newAstContext, nextSegmentLabel, Condition.GREATER_THAN_EQ, caseVariable, LogicNumber.create(segment.to)));
            }

            targets.subMap(segment.from, segment.to).forEach((value, label) ->
                    insertInstruction(createJump(newAstContext, label,
                            logicConversion && value == 0 ? Condition.STRICT_EQUAL : Condition.EQUAL,
                            caseVariable, LogicNumber.create(value))));

            if (nextSegmentLabel == finalLabel) {
                insertInstruction(createJumpUnconditional(newAstContext, finalLabel));
            }
        }

        protected void insertInstruction(LogicInstruction instruction) {
            optimizationContext.insertInstruction(index++, instruction);
        }

        private OptimizationResult convertCaseExpression(int costLimit) {
            index = firstInstructionIndex(Objects.requireNonNull(astContext.findSubcontext(CONDITION)));
            AstContext elseContext = astContext.findSubcontext(ELSE);
            AstContext finalContext = elseContext != null ? elseContext : Objects.requireNonNull(astContext.lastChild());
            LogicLabel finalLabel = obtainContextLabel(finalContext);

            newAstContext = astContext.createSubcontext(FLOW_CONTROL, 1.0);

            if (logicConversion) {
                caseVariable = instructionProcessor.nextTemp();
                insertInstruction(createSensor(newAstContext, caseVariable, variable, LogicBuiltIn.ID));
            } else {
                caseVariable = variable;
            }

            int lastTo = Integer.MAX_VALUE;
            for (Segment segment : segments) {
                debug("Optimizing segment " + segment + ", size: " + segment.size() + ", targets: " + targets.subMap(segment.from, segment.to).size());

                boolean first = segment == segments.getFirst();
                boolean last = segment == segments.getLast();
                LogicLabel nextSegmentLabel = last ? finalLabel : instructionProcessor.nextLabel();
                int segmentFrom = first ? min : segment.from;
                switch (segment.type) {
                    case SINGLE -> generateSingleSegment(segment, lastTo, nextSegmentLabel, finalLabel);
                    case MIXED -> generateMixedSegment(segment, first, nextSegmentLabel, finalLabel);
                    case JUMP_TABLE -> generateJumpTable(segment, segmentFrom, nextSegmentLabel, finalLabel);
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
