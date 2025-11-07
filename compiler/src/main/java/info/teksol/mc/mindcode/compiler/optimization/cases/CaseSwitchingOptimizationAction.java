package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationResult;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.ContextlessInstructionCreator;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.LabelInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.GenerationGoal;
import info.teksol.mc.util.Indenter;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;

@NullMarked
public class CaseSwitchingOptimizationAction implements ConvertCaseOptimizationAction, ContextlessInstructionCreator {
    private static final Indenter indenter = new Indenter("    ");

    private final BooleanSupplier debugOutput;
    private final int id;
    private final OptimizationContext optimizationContext;
    private final InstructionProcessor instructionProcessor;
    private final BiFunction<Supplier<OptimizationResult>, String, OptimizationResult> optimizationApplier;
    private final ConvertCaseActionParameters param;
    public final List<Segment> segments;
    private final String padding;
    private final boolean moveAllBodies;
    private int cost;
    private int executionSteps;
    private double benefit;
    private double rawBenefit;
    private boolean applied;

    public CaseSwitchingOptimizationAction(int id, OptimizationContext optimizationContext,
            BiFunction<Supplier<OptimizationResult>, String, OptimizationResult> optimizationApplier,
            BooleanSupplier debugOutput, ConvertCaseActionParameters param, List<Segment> segments, String padding,
            boolean moveAllBodies) {
        this.id = id;
        this.debugOutput = debugOutput;
        this.optimizationContext = optimizationContext;
        this.instructionProcessor = optimizationContext.getInstructionProcessor();
        this.optimizationApplier = optimizationApplier;
        this.param = param;
        this.segments = segments.stream().map(Segment::duplicate).toList();
        this.padding = padding;
        this.moveAllBodies = moveAllBodies;

        debugOutput("%n%n*** %s ***%n", this);

        if (param.removeRangeCheck()) {
            computeCostAndBenefitNoRangeCheck();
        } else {
            computeCostAndBenefit();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public InstructionProcessor getProcessor() {
        return instructionProcessor;
    }

    @Override
    public LogicInstruction createInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> arguments) {
        return instructionProcessor.createInstruction(astContext, opcode, arguments);
    }

    @Override
    public GenerationGoal goal() {
        return param.context().getLocalProfile().getGoal();
    }

    @Override
    public Optimization optimization() {
        return Optimization.CASE_SWITCHING;
    }

    @Override
    public AstContext astContext() {
        return param.context();
    }

    @Override
    public int cost() {
        return cost - param.originalCost();
    }

    public int rawCost() {
        return cost;
    }

    @Override
    public double benefit() {
        return benefit;
    }

    @Override
    public int originalSteps() {
        return param.originalSteps();
    }

    @Override
    public int executionSteps() {
        return executionSteps;
    }

    @Override
    public boolean applied() {
        return applied;
    }

    @Override
    public OptimizationResult apply(int costLimit) {
        return optimizationApplier.apply(this::convertCaseExpression, toString());
    }

    @Override
    public @Nullable String getGroup() {
        return "CaseSwitcher" + param.group();
    }

    @Override
    public String toString() {
        int numSegments = segments.size() - (segments.getFirst().empty() ? 1 : 0) - (segments.getLast().empty() ? 1 : 0);
        String strId = isDebugOutput() ? "#" + id + ", " : "";
        return "Convert case at " + Objects.requireNonNull(param.context().node()).sourcePosition().formatForLog() +
                " (" + strId + "segments: " + numSegments + (padding.isEmpty() ? "" : ", " + padding) +
                (moveAllBodies && isDebugOutput() ? ", embed all)" : ")");
    }

    private void computeCostAndBenefitNoRangeCheck() {
        if (segments.size() != 1) throw new MindcodeInternalError("Unexpected number of segments");
        Segment segment = segments.getFirst();

        int min = segment.from();
        int max = segment.to();

        int symbolicCost = param.symbolic() && min != 0 ? 1 : 0;      // Cost of computing offset
        int contentCost = param.mindustryContent() ? 1 : 0;           // Cost of converting type to logic ID
        int jumpTableCost = (max - min) + 1;                        // Table size plus initial jump
        cost = jumpTableCost + symbolicCost + contentCost;

        int averageSteps = 2 + symbolicCost + contentCost;          // 1x multiJump, 1x jump table, additional costs
        rawBenefit = (double) param.originalSteps() / param.statement().getTotalSize() - averageSteps;
        benefit = rawBenefit * param.context().totalWeight();
        executionSteps = averageSteps * param.statement().size();

        debugOutput("Original steps: %d, new steps: %d", param.originalSteps(), executionSteps);
        debugOutput("Original size: %d, new size: %d, cost: %d", param.originalCost(), cost, cost - param.originalCost());
        if (isDebugOutput() && !padding.isEmpty())
            debugOutput("*** " + padding.toUpperCase() + " ***");
        debugOutput("");
    }

    // Per target, not totals
    int targetSteps = 0;
    int elseSteps = 0;

    private void computeCostAndBenefit() {
//            if (caseConfiguration == actionCounter) {
//                System.out.print(""); // For breakpoint
//            }

        // One time costs
        int contentCost = param.mindustryContent() ? 1 : 0;   // Cost of converting type to logic ID
        cost = contentCost;
        targetSteps = contentCost * param.statement().size();
        elseSteps = contentCost * param.statement().getElseValues();

        debugOutput(this);
        debugOutput("Case expression initialization: instructions: %d, average steps: %d, total steps: %d",
                contentCost, contentCost, contentCost * param.statement().getTotalSize());

        // Account for null handling: an instruction per zero value
        // Note: null handling when zero is not there is accounted for in individual segments
        if (param.handleNulls() && param.statement().hasNullOrZeroKey()) {
            if (param.statement().hasZeroKey()) {
                // There's a handler on the `0` branch
                double nullHandling = 1.0 / param.statement().getTotalSize();
                if (isDebugOutput())
                    debugOutput("Null handling: instructions: 1, average steps: %g, total steps: 1", nullHandling);
                targetSteps++;
            } else {
                // There's a handler on the else branch
                debugOutput("Null handling: instructions: 1 (null handling steps accounted for in individual segments)");
            }
            cost++;
        }

        computeEmbedding();

        int bisectionSteps = computeBisectionTable(segments, 0);

        if (moveAllBodies) {
            Set<LogicLabel> moved = new HashSet<>();
            for (Segment segment : segments) {
                LogicLabel label = segment.endLabel() == LogicLabel.INVALID ? param.statement().getExisting(0) : segment.endLabel();
                if (segment.embedded() && !moved.add(label)) {
                    segment.setEmbeddingSize(param.statement().getBranchSize(label) + 1);
                }
            }
        }

        segments.forEach(this::computeSegmentCostAndBenefit);

        if (param.considerElse()) {
            debugOutput("Original steps: %d, new steps: %d (target: %d, else: %d; bisection: %s)",
                    param.originalSteps(), targetSteps + elseSteps, targetSteps, elseSteps, bisectionSteps);

            rawBenefit = (double) (param.originalSteps() - targetSteps - elseSteps) / param.statement().getTotalSize();
            executionSteps = targetSteps + elseSteps;
        } else {
            // We disregard else steps in both computations
            debugOutput("Original steps: %d, new steps: %d (target: %d, disregarding else; bisection: %d)", param.originalSteps(),
                    targetSteps, targetSteps, bisectionSteps);

            rawBenefit = (double) (param.originalSteps() - targetSteps) / param.statement().size();
            executionSteps = targetSteps;
        }
        benefit = rawBenefit * param.context().totalWeight();

        debugOutput("Original size: %d, new size: %d, cost: %d", param.originalCost(), cost, cost - param.originalCost());
        if (isDebugOutput() && !padding.isEmpty())
            debugOutput("*** " + padding.toUpperCase() + " ***");
        debugOutput("");
    }

    private @Nullable AstContext newAstContext;
    private @Nullable LogicVariable caseVariable;
    private int index;

    private void computeSegmentCostAndBenefit(Segment segment) {
        int activeTargets = param.statement().targetCount(segment);

        SegmentStats stats = switch (segment.type()) {
            case SINGLE -> computeSingleSegment(segment, activeTargets);
            case MIXED -> computeMixedSegment(segment, activeTargets);
            case JUMP_TABLE -> computeJumpTable(segment, activeTargets);
        };

        if (isDebugOutput()) {
            debugOutput("Segment %3d to %3d: %s, %s, embed size %d, depth %d, bisection steps: %d, total steps: %d",
                    segment.from(), segment.to(), segment.typeName(), stats, segment.embeddingSize(), segment.depth(),
                    segment.depth() * (stats.values + stats.elseValues), stats.steps + stats.elseSteps);
        }

        cost += stats.size + segment.embeddingSize();
        targetSteps += stats.steps + segment.depth() * stats.values;
        elseSteps += stats.elseSteps + segment.depth() * stats.elseValues;
    }

    // There's a problem for sparse integer case expressions, where segments are created for values
    // inside the case range that aren't valid targets. This significantly increases bisection costs
    // compared to continuous case ranges, making a pure bisection solution unfeasible.
    // We might need a separate option which would tell the case expression need not handle else values
    // at all, including else values withing the case range.
    private int bisect(List<Segment> segments) {
        if (segments.size() <= 1) return -1;

        // Take the zero-size leading and trailing segments into account
        int min = segments.getFirst().from() - (segments.getFirst().size() == 0 ? 1 : 0);
        int max = segments.getLast().to() + (segments.getLast().size() == 0 ? 1 : 0);
        int middle = min + (max - min) / 2;

        int distance = Integer.MAX_VALUE;
        int best = -1;
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
        int size = segment.inline() || segment.embedded() ? 0 : 1;
        int steps = segment.inline() || segment.embedded() ? 0 : segment.size();
        int nullSteps = segment.handleNulls() ? segment.size() : 0;  // Only for empty segments, these can't be direct

        if (segment.empty()) {
            return new SegmentStats(size, 0, 0, segment.size(), steps + nullSteps);
        } else {
            return new SegmentStats(size, segment.size(), steps, 0, 0);
        }
    }

    private LogicLabel findSingleSegmentTarget(Segment segment, LogicLabel finalLabel) {
        return segment.empty()
                ? segment.handleNulls() ? param.statement().getNullOrElseTarget() : finalLabel
                : segment.majorityLabel() == LogicLabel.INVALID ? param.statement().getExisting(0) : segment.majorityLabel();
    }

    private void generateSingleSegment(Segment segment, LogicLabel finalLabel) {
        assert newAstContext != null;
        if (segment.embedded()) {
            moveBody(segment.endLabel());
        } else {
            insertInstruction(createJumpUnconditional(newAstContext, findSingleSegmentTarget(segment, finalLabel)));
        }
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
            throw new MindcodeInternalError("Invalid label in mixed segment");
        }

        int elseValues = segment.size() - activeTargets;

        if (segment.majorityLabel() == LogicLabel.EMPTY) {
            // Majority branch is the else branch
            // Final jump counts as else branch
            int finalJump = segment.embedded() ? 0 : 1;

            // Account for null handling on the else branch
            int nullHandling = segment.handleNulls() ? elseValues : 0;

            return new SegmentStats(activeTargets + finalJump,
                    activeTargets,
                    activeTargets * (activeTargets + 1) / 2,
                    elseValues,
                    elseValues * (activeTargets + finalJump) + nullHandling);
        } else {
            // Majority branch is a when branch
            int majorityTargets = (int) param.statement().entrySet().stream().filter(
                    e -> segment.contains(e.getKey()) && segment.majorityLabel().equals(e.getValue().label)).count();
            int activeSteps = 0;
            int elseSteps = 0;

            // This counts the number of steps for values that are handled by the select table.
            // Traversing the jumps in the opposite order from the one in which they'll be built,
            // so that the size always corresponds to the number of instructions the not-yet-handled
            // values will see.
            int size = 0;
            for (int value = segment.to() - 1; value >= segment.from(); value--) {
                LogicLabel target = param.statement().get(value);
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

            // The final jump to the majority target (not present for embedded segments)
            if (!segment.embedded()) {
                size++;
                activeSteps += majorityTargets;
            }

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
            LogicLabel target = param.statement().getOrDefault(value, finalLabel);
            if (!majorityTarget.equals(target)) {
                insertInstruction(createJump(newAstContext, target, Condition.EQUAL, caseVariable, LogicNumber.create(value)));
            }
        }

        if (segment.embedded()) {
            moveBody(segment.endLabel());
        } else {
            if (segment.handleNulls() && majorityIsElse) {
                insertInstruction(createJumpUnconditional(newAstContext, param.statement().getNullOrElseTarget()));
            } else {
                insertInstruction(createJumpUnconditional(newAstContext, majorityTarget));
            }
        }
    }

    private SegmentStats computeJumpTable(Segment segment, int activeTargets) {
        int multiJump = param.symbolic() && segment.from() != 0 ? 2 : 1;
        int tableSize = segment.size();
        int elseValues = tableSize - activeTargets;
        int elseSteps = elseValues * (multiJump + 1);

        // Account for null handling on the else branch
        int nullHandling = segment.handleNulls() ? 1 : 0;

        if (segment.from() == 0 && param.context().getCompilerProfile().useTextJumpTables()) {
            return new SegmentStats(
                    1,
                    activeTargets,
                    activeTargets,
                    elseValues,
                    elseValues + nullHandling);
        } else {
            // When embedded, the last jump in the jump table will be avoided
            int savedSize = segment.embedded() ? 1 : 0;
            int savedTarget = segment.endLabel() == LogicLabel.EMPTY ? 0 : savedSize;
            int savedElse = segment.endLabel() == LogicLabel.EMPTY ? savedSize : 0;

            return new SegmentStats(
                    multiJump + tableSize - savedSize,
                    activeTargets,
                    activeTargets * (multiJump + 1) - savedTarget,
                    elseValues,
                    elseSteps + nullHandling - savedElse);
        }
    }

    // Generates a jump table
    // Values less than 'from' are sent to the final label
    // Values greater than or equal to 'to' are sent to the next segment
    private void generateJumpTable(Segment segment, LogicLabel finalLabel) {
        assert newAstContext != null;
        assert caseVariable != null;

        LogicLabel marker = instructionProcessor.nextLabel();

        if (segment.from() == 0 && param.context().getLocalProfile().useTextJumpTables()) {
            // The when bodies have normal labels now. We need multilabels.
            // We keep the original labels since they might be in use elsewhere.
            // Thus, we need to create new ones and maintain a map.
            List<LogicLabel> labels = new ArrayList<>();
            Map<LogicLabel, LogicLabel> labelMap = new HashMap<>();
            for (int i = 0; i < segment.to(); i++) {
                LogicLabel target = param.statement().getOrDefault(i, finalLabel);
                LogicLabel updatedTarget = i == 0 && target == finalLabel ? param.statement().getNullOrElseTarget() : target;
                if (!labelMap.containsKey(updatedTarget)) {
                    LabelInstruction labelInstruction = optimizationContext.getLabelInstruction(updatedTarget);
                    LogicLabel jumpLabel = instructionProcessor.nextLabel();
                    labelMap.put(updatedTarget, jumpLabel);
                    optimizationContext.insertBefore(labelInstruction, createMultiLabel(labelInstruction.getAstContext(), jumpLabel, marker).setJumpTarget());
                }
                labels.add(labelMap.get(updatedTarget));
            }

            insertInstruction(createMultiJump(newAstContext, caseVariable, marker).setJumpTable(labels));
        } else {
            List<LogicLabel> labels = IntStream.range(segment.from(), segment.to()).mapToObj(i -> instructionProcessor.nextLabel()).toList();
            insertInstruction(createMultiJump(newAstContext, labels.getFirst(), caseVariable, LogicNumber.create(segment.from()), marker));
            int end = labels.size() - (segment.embedded() ? 1 : 0);

            for (int i = 0; i < end; i++) {
                insertInstruction(createMultiLabel(newAstContext, labels.get(i), marker));
                LogicLabel target = param.statement().getOrDefault(segment.from() + i, finalLabel);
                LogicLabel updatedTarget = segment.from() + i == 0 && target == finalLabel ? param.statement().getNullOrElseTarget() : target;
                insertInstruction(createJumpUnconditional(newAstContext, updatedTarget));
            }

            if (segment.embedded()) {
                insertInstruction(createMultiLabel(newAstContext, labels.getLast(), marker));
                moveBody(segment.endLabel());
            }
        }
    }

    private record BranchContents(OptimizationContext.LogicList body, OptimizationContext.LogicList jump) {}

    private final Map<LogicLabel, BranchContents> branches = new HashMap<>();

    private void moveBody(LogicLabel endLabel) {
        LogicLabel label = endLabel == LogicLabel.INVALID ? param.statement().getExisting(0) : endLabel;
        BranchContents branch = branches.get(label);

        if (branch == null) {
            // Moving the branch body for the first time: extract and remove the original instructions
            int startIndex = optimizationContext.getLabelInstructionIndex(label);

            AstContext labelContext = optimizationContext.instructionAt(startIndex).getAstContext();
            OptimizationContext.LogicList labelInstructions = optimizationContext.contextInstructions(labelContext);
            int multiLabels = (int) labelInstructions.stream().filter(LogicInstruction::isJumpTarget).count();
            startIndex -= multiLabels;
            int labelsSize = labelInstructions.size() - multiLabels;
            if (!labelContext.matches(AstContextType.CASE, FLOW_CONTROL) || (labelsSize != 1 && labelsSize != 3))
                throw new MindcodeInternalError("Unexpected context structure");

            AstContext bodyContext = optimizationContext.instructionAt(startIndex + labelInstructions.size()).getAstContext();
            while (bodyContext.existingParent() != astContext()) bodyContext = bodyContext.existingParent();
            OptimizationContext.LogicList bodyInstructions = optimizationContext.contextInstructions(bodyContext);
            if (!bodyContext.matches(AstContextType.CASE, AstSubcontextType.BODY))
                throw new MindcodeInternalError("Unexpected context structure");

            AstContext jumpContext = optimizationContext.instructionAt(startIndex + labelInstructions.size() + bodyInstructions.size()).getAstContext();
            OptimizationContext.LogicList jumpInstructions = optimizationContext.contextInstructions(jumpContext);
            if (!jumpContext.matches(AstContextType.CASE, FLOW_CONTROL) || jumpInstructions.size() != 2)
                throw new MindcodeInternalError("Unexpected context structure");

            int size = labelInstructions.size() + bodyInstructions.size() + 1;
            for (int i = 0; i < size; i++) {
                optimizationContext.removeInstruction(startIndex);
            }

            labelInstructions.forEach(this::insertInstruction);

            branch = new BranchContents(bodyInstructions, jumpInstructions);
            branches.put(label, branch);
        }

        // We're duplicating the contexts to ensure no AST context collision after moving.
        OptimizationContext.LogicList body = branch.body.duplicate(true);
        OptimizationContext.LogicList jump = branch.jump.duplicate(false);

        body.forEach(this::insertInstruction);
        insertInstruction(Objects.requireNonNull(jump.getFirst()));

        // By moving the body here, we've broken the common context. Start a new one.
        newAstContext = param.context().createSubcontext(FLOW_CONTROL, 1.0);
    }

    private void computeEmbedding() {
        // If we can't embed, remember the label to avoid
        LogicLabel avoidLabel = canEmbedZero() ? null : param.statement().get(0);

        Map<LogicLabel, Segment> bestSegments = new HashMap<>();
        for (Segment segment : segments) {
            // We won't embed a jump table segment in case of text-based jump tables
            if (segment.type() == SegmentType.JUMP_TABLE && param.context().getLocalProfile().useTextJumpTables())
                continue;

            LogicLabel label = segment.endLabel() == LogicLabel.INVALID ? param.statement().getExisting(0) : segment.endLabel();

            // We won't embed the else branch: we'd save a jump to the else branch,
            // but we'd need another jump to the end of the case statement anyway.
            if (label == LogicLabel.EMPTY || label == avoidLabel || !param.statement().isMovableLabel(label)) continue;

            segment.setMoveable();
            if (moveAllBodies) {
                segment.setEmbedded();
            } else {
                Segment previous = bestSegments.get(label);
                // Prefer non-single segments in case of tie, as they cannot be inlined
                if (previous == null || previous.endLabelWeight() < segment.endLabelWeight()
                        || (previous.endLabelWeight() == segment.endLabelWeight() && segment.type() != SegmentType.SINGLE)) {
                    bestSegments.put(label, segment);
                }
            }
        }

        bestSegments.values().forEach(Segment::setEmbedded);
    }

    // Returns true if the zero key can be retargeted:
    // Either nulls cannot appear, or the zero target is not shared with anything else.
    private boolean canEmbedZero() {
        // No nulls
        if (!param.handleNulls()) return true;

        // No zero target: zero cannot be embedded (won't be even attempted, so it is meaningless).
        LogicLabel zeroLabel = param.statement().get(0);
        if (zeroLabel == null) return false;

        // Return true if the zero target is not shared with anything else
        return param.statement().entrySet().stream().noneMatch(e -> e.getKey() != 0 && e.getValue().label == zeroLabel);
    }

    private int computeBisectionTable(List<Segment> segments, int depth) {
        int bisection = bisect(segments);
        if (bisection < 0) {
            segments.forEach(segment -> {
                segment.setBisectionSteps(depth, false);
                if (isDebugOutput())
                    debugOutput("%s%s", indenter.getIndent(depth), segment);
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
        if (isDebugOutput())
            debugOutput("%sBisection at %d (%d to %d)", indenter.getIndent(depth), highSegments.getFirst().from(), min, max);

        int nextDepth = depth + 1;

        if (inlineSegment == InlineSegment.LOW) {
            lowSegments.getFirst().setBisectionSteps(nextDepth, true);
            if (isDebugOutput())
                debugOutput("%s    %s", indenter.getIndent(depth), lowSegments.getFirst());
            return steps + computeBisectionTable(highSegments, nextDepth);
        } else if (inlineSegment == InlineSegment.HIGH) {
            highSegments.getFirst().setBisectionSteps(nextDepth, true);
            if (isDebugOutput())
                debugOutput("%s    %s", indenter.getIndent(depth), highSegments.getFirst());
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
                debugOutput("Building segment %s, size: %d, targets: %d", segment, segment.size(),
                        param.statement().subMap(segment.from(), segment.to()).size());
                switch (segment.type()) {
                    case SINGLE -> generateSingleSegment(segment, finalLabel);
                    case MIXED -> generateMixedSegment(segment, finalLabel);
                    case JUMP_TABLE -> generateJumpTable(segment, finalLabel);
                }
            }
        }
    }

    private enum InlineSegment {NONE, LOW, HIGH}

    private InlineSegment findInlinedSegment(List<Segment> lowSegments, List<Segment> highSegments) {
        boolean low = canInline(lowSegments);
        boolean high = canInline(highSegments);

        if (low && high) {
            // Both can be inlined.
            Segment lowSegment = lowSegments.getFirst();
            Segment highSegment = highSegments.getFirst();

            if (lowSegment.embedded() != highSegment.embedded()) {
                // Inline the non-embedded one: the inlined will get a conditional jump
                return lowSegment.embedded() ? InlineSegment.HIGH : InlineSegment.LOW;
            } else {
                // Inline the larger one - saves more steps
                // We need to reset the embedding (if any) on the segment that gets inlined
                if (size(lowSegment) > size(highSegment)) {
                    lowSegment.resetEmbedded();
                    return InlineSegment.LOW;
                } else {
                    highSegment.resetEmbedded();
                    return InlineSegment.HIGH;
                }
            }
        } else if (low) {
            // If we can inline the segment, it is better than embedding it.
            // Both inlining and embedding save a jump, but embedding increases code size.
            lowSegments.getFirst().resetEmbedded();
            return InlineSegment.LOW;
        } else if (high) {
            highSegments.getFirst().resetEmbedded();
            return InlineSegment.HIGH;
        } else {
            return InlineSegment.NONE;
        }
    }

    private int size(Segment segment) {
        // Empty segments have zero steps when the ` else ` path is not considered
        return segment.empty() && !param.considerElse() ? 0 : segment.size();
    }

    // Returns true if the segment can be handled by a simple jump right from the bisection table.
    boolean canInline(List<Segment> segments) {
        return segments.size() == 1 && (segments.getFirst().type() == SegmentType.SINGLE);
    }

    protected void insertInstruction(LogicInstruction instruction) {
        optimizationContext.insertInstruction(index++, instruction);
    }

    private OptimizationResult convertCaseExpression() {
        debugOutput("Converting case expression, configuration %d.", id);

        index = optimizationContext.firstInstructionIndex(Objects.requireNonNull(param.context().findSubcontext(CONDITION)));
        AstContext elseContext = param.context().findSubcontext(ELSE);
        AstContext finalContext = elseContext != null ? elseContext : Objects.requireNonNull(param.context().lastChild());
        LogicLabel finalLabel = optimizationContext.obtainContextLabel(finalContext);

        param.statement().setNullOrElseTarget(finalLabel);

        newAstContext = param.context().createSubcontext(FLOW_CONTROL, 1.0);

        if (param.mindustryContent()) {
            caseVariable = instructionProcessor.nextTemp();
            insertInstruction(createSensor(Objects.requireNonNull(param.context().parent()),
                    caseVariable, param.variable(), LogicBuiltIn.ID));
        } else {
            caseVariable = param.variable();
        }

        if (param.handleNulls()) {
            // We need to install a null check
            LogicLabel zeroLabel = param.statement().get(0);
            if (zeroLabel == null) {
                if (param.statement().getNullTarget() != null) {
                    // There's an explicit null branch, and no `0` branch
                    // Install a null check on the else branch:
                    // a 'jump strictEqual null' instruction in front of the else branch which jumps to the null branch
                    // This is targeted by the else branch
                    // The finalLabel skips this check and is to be used by code that is known not to handle nulls
                    int elseIndex = optimizationContext.getLabelInstructionIndex(finalLabel);
                    LogicInstruction elseLabelIx = optimizationContext.instructionAt(elseIndex);
                    param.statement().setNullOrElseTarget(instructionProcessor.nextLabel());
                    optimizationContext.insertInstruction(elseIndex++, createLabel(elseLabelIx.getAstContext(),
                            param.statement().getNullOrElseTarget()));
                    optimizationContext.insertInstruction(elseIndex, createJump(elseLabelIx.getAstContext(),
                            param.statement().getNullTarget(), Condition.STRICT_EQUAL, caseVariable, LogicNull.NULL));
                }
            } else {
                // There's a zero branch. Need to install the handler there
                if (param.statement().getNullTarget() == null) {
                    param.statement().setNullTarget(finalLabel);
                }

                LogicLabel nullOrZeroTarget = instructionProcessor.nextLabel();
                param.statement().addBranchKey(0, nullOrZeroTarget);

                int zeroIndex = optimizationContext.getLabelInstructionIndex(zeroLabel);
                LogicInstruction zeroLabelIx = optimizationContext.instructionAt(zeroIndex);
                optimizationContext.insertInstruction(zeroIndex++, createLabel(zeroLabelIx.getAstContext(),
                        nullOrZeroTarget));
                optimizationContext.insertInstruction(zeroIndex, createJump(zeroLabelIx.getAstContext(),
                        param.statement().getNullTarget(), Condition.STRICT_EQUAL, caseVariable, LogicNull.NULL));
            }
        }

        generateBisectionTable(segments, finalLabel);

        // Remove all conditions
        for (AstContext condition : param.context().findSubcontexts(CONDITION)) {
            optimizationContext.removeMatchingInstructions(ix -> ix.belongsTo(condition));
        }

        applied = true;
        return OptimizationResult.REALIZED;
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
    /// @param elseSteps  The total number of steps needed for all `else` values handled by this segment to resolve.
    ///                   Only the else values that actually land on the else branch are included.
    record SegmentStats(int size, int values, int steps, int elseValues, int elseSteps) {
    }

    private void debugOutput(Object message) {
        if (debugOutput.getAsBoolean()) System.out.println(message);
    }

    private void debugOutput(@PrintFormat String format, Object... args) {
        if (debugOutput.getAsBoolean()) {
            System.out.printf(format, args);
            System.out.println();
        }
    }

    private boolean isDebugOutput() {
        return debugOutput.getAsBoolean();
    }
}
