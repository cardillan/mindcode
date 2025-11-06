package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstCaseExpression;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.compiler.optimization.cases.*;
import info.teksol.mc.mindcode.compiler.optimization.cases.CaseStatement.Branch;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.mimex.ContentType;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.BuiltinEvaluation;
import info.teksol.mc.profile.GenerationGoal;
import info.teksol.mc.util.Indenter;
import info.teksol.mc.util.Utf8Utils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
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
/// * Code for choosing the correct segment for a given input value is handled outside segments. Currently, bisection
///   search is used. The bisection routing is also responsible for handling the out-of-range values via the leading
///   and trailing segments (which represent values below and above the valid range, respectively).
/// * When the range check is omitted (either due to the `unsafe-case-optimization` compiler option or when handling
///   Mindustry content, possibly with `builtin-evaluation` set to `full`), the leading and/or trailing segments
///   are not generated.
///
/// **Segment types**
///
/// * SINGLE: the segment represents a continuous region of a single target. There's no logic involved, the SINGLE
///   segment is realized by a jump to the target body. When possible, this jump is inlined into the bisection table.
/// * MIXED: the segments represent a continuous region having a single majority target and a few exceptions. The
///   segment is realized by explicitly handling the targets first and then jumping to the majority target.
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
    private static final int MAX_CASE_RANGE = 1000;
    private static final int MAX_TABLE_PADDING = 100;
    private static final Indenter indenter = new Indenter("    ");

    private final int caseConfiguration;
    private int actionCounter = 0;

    CaseSwitcher(OptimizationContext optimizationContext) {
        super(Optimization.CASE_SWITCHING, optimizationContext);
        this.caseConfiguration = getGlobalProfile().getCaseConfiguration();
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
        private final AstContext parent;
        private final AstSubcontextType subcontextType;
        private final List<AstContext> contexts;
        int lastMatchIndex = 0;

        public ContextMatcher(List<AstContext> contexts) {
            this.parent = contexts.getFirst().existingParent();
            this.subcontextType = contexts.getFirst().subcontextType();
            this.contexts = contexts;
        }

        public boolean matches(AstContext context) {
            if (context.parent() == parent && context.matches(subcontextType)) return true;

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

    private enum ExpState { CONDITION, BODY, FLOW }

    private @Nullable LogicLabel findNextLabel(AstContext context, LogicIterator iterator, JumpInstruction jump) {
        AstContext jumpAstContext = jump.getAstContext();
        AstContext flowContext = context.nextChild(jumpAstContext);
        while (iterator.hasNext() && iterator.peek(0) instanceof EmptyInstruction noop && noop.belongsTo(jumpAstContext)) {
            // Skipping noops, nothing else may occur here
            iterator.next();
        }
        if (flowContext != null && iterator.peek(0).belongsTo(flowContext)) {
            return obtainContextLabel(flowContext);
        } else {
            // Unsupported case structure
            return null;
        }
    }

    private void findPossibleCaseSwitches(AstContext context, int costLimit, List<OptimizationAction> result) {
        groupCount++;

        List<AstContext> conditionContexts = context.findSubcontexts(CONDITION);
        List<AstContext> bodyContexts = context.findSubcontexts(BODY);
        List<AstContext> elseContexts = context.findSubcontexts(ELSE);
        if (conditionContexts.isEmpty() || bodyContexts.isEmpty() || elseContexts.isEmpty()) return;

        ContextMatcher conditionMatcher = new ContextMatcher(conditionContexts);
        ContextMatcher bodyMatcher = new ContextMatcher(bodyContexts);
        ContextMatcher elseMatcher = new ContextMatcher(elseContexts);
        AstContext initContext = context.findSubcontext(INIT);

        // Explicit else
        boolean declaredElseBranch = !(context.node() instanceof AstCaseExpression exp) || exp.isElseDefined();
        boolean removeRangeCheck = context.getLocalProfile().isUnsafeCaseOptimization() && !declaredElseBranch;

        CaseStatement caseStatement = new CaseStatement(declaredElseBranch);
        ValueAnalyzer analyzer = new ValueAnalyzer(context);

        // Used to compute the size and execution costs of the case expression
        LogicVariable variable = null;
        int jumps = 0;
        int values = 0;
        int savedSteps = 0;
        ExpState expState = null;
        LogicLabel target = null;
        LogicLabel lastLabel = null;
        Integer rangeLowValue = null;

        // ANALYZE CASE STRUCTURE
        // Gathers all information about the case expression we need
        try (LogicIterator iterator = createIteratorAtContext(context)) {
            // Skip init context -- can't do anything with it
            if (initContext != null) {
                while (iterator.hasNext() && iterator.peek(0).belongsTo(initContext)) {
                    iterator.next();
                }
            }

            while (iterator.hasNext()) {
                LogicInstruction ix = iterator.next();
                if (conditionMatcher.matches(ix.getAstContext())) {
                    expState = ExpState.CONDITION;

                    // Ignore these in conditions
                    if (ix instanceof LabelInstruction || ix instanceof EmptyInstruction) continue;

                    if (!(ix instanceof JumpInstruction jump)) return;
                    jumps++;

                    if (jump.isUnconditional()) {
                        // Unconditional jump is a jump to the next when branch/value
                        // An unfinished range expression: we don't understand the structure of this expression, bail out
                        if (rangeLowValue != null) return;
                        savedSteps += values;
                    } else {
                        // X needs to be the case variable
                        if (!(jump.getX() instanceof LogicVariable var) || (variable != null && !var.equals(variable))) return;

                        // Unexpected context: we don't understand the structure of this expression, bail out
                        if (getLabelInstruction(jump.getTarget()).getAstContext().parent() != context) return;

                        variable = var;
                        LogicValue value = jump.getY();

                        if (jump.getCondition().isEquality()) {
                            // An unfinished range expression: we don't understand the structure of this expression, bail out
                            // Analyzer refuses our value: bail out
                            if (rangeLowValue != null || !analyzer.inspect(value)) return;

                            if (jump.getCondition() == Condition.EQUAL || jump.getCondition() == Condition.STRICT_EQUAL) {
                                target = jump.getTarget();
                            } else {
                                // NOT_EQUAL might have been created by the jump over jump optimization
                                target = findNextLabel(context, iterator, jump);
                                if (target == null) return;
                            }

                            if (!caseStatement.addBranchKey(analyzer.getLastValue(), target)) return;

                            savedSteps += values;
                            if (analyzer.getLastValue() != null) values++;  // We do not count nulls
                        } else {
                            // This is an inequality comparison -- can't be unconditional.
                            // Needs an integer variable
                            if (!value.isNumericConstant() || !value.isInteger()) return;

                            if (rangeLowValue == null) {
                                // Opening jump: must be LESS_THAN
                                if (jump.getCondition() != Condition.LESS_THAN) return;
                                rangeLowValue = value.getIntValue();
                            } else {
                                int rangeHighValue;     // Exclusive
                                if (jump.getCondition() == Condition.LESS_THAN || jump.getCondition() == Condition.LESS_THAN_EQ) {
                                    // Closing jump, in original form.
                                    target = jump.getTarget();
                                    rangeHighValue = value.getIntValue() + (jump.getCondition() == Condition.LESS_THAN_EQ ? 1 : 0);
                                } else {
                                    // The original jump modified by the jump over jump optimization
                                    target = findNextLabel(context, iterator, jump);
                                    if (target == null) return;
                                    // When the condition is met, the value doesn't belong to the range
                                    rangeHighValue = value.getIntValue() + (jump.getCondition() == Condition.GREATER_THAN ? 1 : 0);
                                }

                                int range = rangeHighValue - rangeLowValue;
                                if (range > MAX_CASE_RANGE) return;

                                if (!analyzer.inspectRange(rangeLowValue, rangeHighValue)) return;

                                // Add in all targets
                                if (!caseStatement.addBranchKeys(rangeLowValue, rangeHighValue, target)) return;

                                // Each value comes through these two jumps
                                values += 2 * range;
                                savedSteps += values;

                                // Range has been processed
                                rangeLowValue = null;
                            }
                        }
                    }
                } else if (bodyMatcher.matches(ix.getAstContext())) {
                    expState = ExpState.BODY;
                    if (!caseStatement.addBranchInstruction(ix)) return;
                } else if (elseMatcher.matches(ix.getAstContext())) {
                    if (lastLabel == null || !caseStatement.setElseBranch(lastLabel)) return;      // Unexpected structure
                    if (!caseStatement.addBranchInstruction(ix)) return;
                } else if (ix.getAstContext().parent() == context && ix.getAstContext().matches(FLOW_CONTROL)) {
                    if (expState == ExpState.BODY) {
                        // First instruction after a body

                        // This may happen with compile-time resolved case expressions
                        if (target == null) return;

                        // The first flow control after a body:
                        // If the first instruction isn't a jump, can't move this body.
                        expState = ix instanceof JumpInstruction jump && jump.isUnconditional() ? ExpState.FLOW : null;
                    } else if (ix instanceof LabelInstruction labelInstruction) {
                        if (expState == ExpState.FLOW) {
                            caseStatement.addMovableLabel(target);
                        }
                        lastLabel = labelInstruction.getLabel();
                    } else {
                        // Only labels expected here
                        return;
                    }
                } else if (!ix.getAstContext().belongsTo(context)) {
                    // End of statement
                    break;
                } else {
                    // An instruction not accounted for --> unrecognized structure
                    return;
                }
            }
        }

        // Unsupported case expressions: no input variable, no branches, range too large or null and integers
        if (variable == null || analyzer.contentType == null || caseStatement.isEmpty() || caseStatement.range() > MAX_CASE_RANGE) return;

        caseStatement.computeElseValues(analyzer.contentType, metadata, getGlobalProfile().getBuiltinEvaluation() == BuiltinEvaluation.FULL);

        int originalCost = jumps;
        int originalSteps = jumps * values - savedSteps
                + (caseStatement.hasDeclaredElseBranch() ? caseStatement.getElseValues() * originalCost : 0);

        ConvertCaseActionParameters param = new ConvertCaseActionParameters(groupCount, context, variable, caseStatement,
                originalCost, originalSteps, analyzer.isMindustryContent(), removeRangeCheck,
                getGlobalProfile().isSymbolicLabels(), caseStatement.hasDeclaredElseBranch() && caseStatement.getElseValues() > 0);

        // Do the translation optimization first, as other modifications may alter the caseStatement in an incompatible way
        List<ConvertCaseExpressionAction> actions = new ArrayList<>();
        TranslateCaseExpressionAction translationAction = createTranslationAction(param);
        if (translationAction != null) {
            actions.add(translationAction);
        }

        int caseOptimizationStrength = context.getLocalProfile().getCaseOptimizationStrength();

        SegmentConfigurationGenerator segmentConfigurationGenerator = new CombinatorialSegmentConfigurationGenerator(caseStatement,
                param.handleNulls(), caseOptimizationStrength);

        // When no range checking, don't bother trying to merge segments.
        Set<SegmentConfiguration> configurations = removeRangeCheck
                ? Set.of(new SegmentConfiguration(segmentConfigurationGenerator.getPartitions(), List.of()))
                : segmentConfigurationGenerator.createSegmentConfigurations();

        debugOutput("Singular segments: \n" + segmentConfigurationGenerator.getPartitions().stream()
                .map(Object::toString).collect(Collectors.joining("\n")));
        debugOutput("Segment configurations: %,d", configurations.size());

        optimizationContext.addDiagnosticData(new CaseSwitcherConfigurations(context.sourcePosition(), configurations.size()));

        for (SegmentConfiguration segmentConfiguration : configurations) {
            createOptimizationActions(param, actions, segmentConfiguration);
        }

        actionCounter = 0;
        debugOutput("%nCase switching optimization: %,d distinct configurations generated.%n", actions.size());

        if (!actions.isEmpty()) {
            List<ConvertCaseExpressionAction> selected = new ArrayList<>();
            if (caseConfiguration > 0) {
                actions.stream().filter(a -> a.getId() == caseConfiguration).forEach(selected::add);
            }

            if (selected.isEmpty()) {
                // Comparing by cost (ascending) then by benefit (descending)
                actions.sort(Comparator.comparing(OptimizationAction::cost).thenComparing(Comparator.comparing(OptimizationAction::benefit).reversed()));
                ConvertCaseExpressionAction last = null;
                for (ConvertCaseExpressionAction action : actions) {
                    if (action instanceof TranslateCaseExpressionAction) {
                        selected.add(action);
                    } else if (last == null || action.benefit() > last.benefit()) {
                        // This action has a higher or equal cost to the last.
                        // It needs to give a better benefit to be considered.
                        selected.add(action);
                        last = action;
                    }
                }
            }

            result.addAll(selected);
            optimizationContext.addDiagnosticData(ConvertCaseExpressionAction.class, selected);
        }
    }

    private void createOptimizationActions(ConvertCaseActionParameters param, List<ConvertCaseExpressionAction> actions,
            SegmentConfiguration segmentConfiguration) {
        List<Segment> segments = segmentConfiguration.createSegments(param.removeRangeCheck, param.handleNulls(),
                param.symbolic, param.statement);

        addOptimizationAction(actions, param, segments, "");

        int lowPadIndex = findLowPadSegment(segments);
        int highPadIndex = findHighPadSegment(param, segments);

        if (lowPadIndex >= 0) {
            addOptimizationAction(actions, param, padLow(segments, param, lowPadIndex), "padded low");
        }

        if (highPadIndex >= 0) {
            addOptimizationAction(actions, param, padHigh(segments, param, highPadIndex), "padded high");
        }

        if (lowPadIndex >= 0 && highPadIndex >= 0) {
            addOptimizationAction(actions, param, padLow(padHigh(segments, param, highPadIndex), param, lowPadIndex), "padded both");
        }
    }

    private void addOptimizationAction(List<ConvertCaseExpressionAction> actions, ConvertCaseActionParameters param,
            List<Segment> segments, String padding) {
        CaseSwitchingExpressionAction action = new CaseSwitchingExpressionAction(param, segments, padding, false);
        actions.add(action);
        if (experimental(param.context)) {
            boolean canMoveMoreBodies = action.segments.stream().anyMatch(s -> s.moveable() && !s.embedded());
            if (canMoveMoreBodies) {
                actions.add(new CaseSwitchingExpressionAction(param, segments, padding, true));
            }
        }
    }

    private List<Segment> padLow(List<Segment> segments, ConvertCaseActionParameters parameters, int index) {
        if (index == 0) {
            return List.of(segments.getFirst().padLow(0));
        } else {
            List<Segment> newSegments = new ArrayList<>(segments);
            Segment prev = newSegments.get(index - 1);
            Segment curr = newSegments.get(index);
            if (!prev.empty() && !prev.zero()) throw new MindcodeInternalError("Previous segment not empty");

            if (parameters.mindustryContent) {
                if (index != 1 || prev.from() != 0) throw new MindcodeInternalError("Mindustry content not starting at index 0");
                newSegments.removeFirst();
                newSegments.set(0, curr.padLow(0));
                if (prev.handleNulls()) {
                    newSegments.getFirst().setHandleNulls();
                }
            } else {
                newSegments.set(index - 1, Segment.empty(Math.min(prev.from(), 0), 0));
                newSegments.set(index, curr.padLow(0));
                if (prev.handleNulls()) {
                    newSegments.get(index).setHandleNulls();
                }
            }

            return newSegments;
        }
    }

    private List<Segment> padHigh(List<Segment> segments, ConvertCaseActionParameters parameters, int index) {
        List<Segment> newSegments = new ArrayList<>(segments);
        Segment curr = newSegments.get(index);
        newSegments.set(index, curr.padHigh(parameters.statement.getTotalSize()));
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

    private int findHighPadSegment(ConvertCaseActionParameters parameters, List<Segment> segments) {
        // Only high-pad Mindustry content when full builtin evaluation is active.
        // When range checking is off, padding high makes no sense.
        if (!parameters.mindustryContent || getGlobalProfile().getBuiltinEvaluation() != BuiltinEvaluation.FULL || parameters.removeRangeCheck) return -1;

        // It can only be the very last segment
        int lastPossibleIndex = segments.getLast().type() == SegmentType.JUMP_TABLE ? segments.size() - 1 : segments.size() - 2;
        Segment segment = segments.get(lastPossibleIndex);
        if (segment.to() <= parameters.statement.getTotalSize() && segment.type() == SegmentType.JUMP_TABLE
                && (segments.getLast() == segment || segments.getLast().empty())) {
            return lastPossibleIndex;
        }

        return -1;
    }

    public interface ConvertCaseExpressionAction extends OptimizationAction {
        int getId();
        int rawCost();
        int originalSteps();
        int executionSteps();
        boolean applied();
    }

    private record ConvertCaseActionParameters(
            int group,
            AstContext context,
            LogicVariable variable,
            CaseStatement statement,
            int originalCost,
            int originalSteps,
            boolean mindustryContent,
            boolean removeRangeCheck,
            boolean symbolic,
            boolean considerElse) {

        private ConvertCaseActionParameters(ConvertCaseActionParameters other) {
            this(other.group, other.context, other.variable, other.statement.duplicate(), other.originalCost, other.originalSteps,
                    other.mindustryContent, other.removeRangeCheck, other.symbolic, other.considerElse);
        }

        public ConvertCaseActionParameters duplicate() {
            return new ConvertCaseActionParameters(this);
        }

        public boolean handleNulls() {
            return mindustryContent || statement.hasNullKey();
        }
    }

    private @Nullable TranslateCaseExpressionAction createTranslationAction(ConvertCaseActionParameters param) {
        if (!getGlobalProfile().getProcessorVersion().atLeast(ProcessorVersion.V8B)
                || !param.context.getLocalProfile().isUseTextTranslations()) return null;

        CaseStatement statement = param.statement;
        Collection<Branch> branches = statement.getBranches();

        // Do all branches assign to the same variable?
        List<LogicVariable> variables = branches.stream().map(Branch::getAssignTarget).distinct().toList();
        if (variables.size() != 1 || variables.getFirst() == LogicVariable.INVALID) return null;
        LogicVariable outputVariable = variables.getFirst();

        // Are all assigned values of the same type and in a supported range?
        ValueAnalyzer analyzer = new ValueAnalyzer(param.context);
        branches.forEach(b -> {
            analyzer.inspect(b.getAssignValue());
            b.setIntegerValue(analyzer.getLastValue());
        });
        if (analyzer.contentType == null || analyzer.getRange() >= Utf8Utils.MAX_SAFE_RANGE) return null;

        boolean nullOnElseBranch = statement.getElseBranch().getAssignValue() == LogicNull.NULL;
        boolean nullOnRegularBranch = branches.stream().filter(b -> b != statement.getElseBranch())
                .anyMatch(b -> b.getAssignValue() == LogicNull.NULL);

        // Ways in which null output values can appear in the map:
        // 1. Null on a regular branch
        // 2. Null on an else branch, while the map is not compact (the map is always compact when unsafe, because
        //    unhandled values aren't supposed to occur).
        boolean unsafe = param.context.getLocalProfile().isUnsafeCaseOptimization() && !statement.hasDeclaredElseBranch();
        boolean compact = unsafe || statement.range() == statement.size();
        boolean handleOutputNull = nullOnRegularBranch || nullOnElseBranch && !compact;

        // We can pad if nulls can't appear in the padding, or nulls in output are already handled.
        int firstKey = statement.firstKey();
        boolean paddingLow = firstKey > 0 && firstKey < MAX_TABLE_PADDING && (!nullOnElseBranch || handleOutputNull);
        boolean paddingHigh = param.mindustryContent && getGlobalProfile().getBuiltinEvaluation() == BuiltinEvaluation.FULL;

        // We need to compensate for the offset of the input value
        boolean inputOffset = firstKey != 0 && !paddingLow;

        Integer zeroValue = Optional.of(statement.getBranch(0)).map(Branch::getIntegerValue).orElse(null);
        Integer nullValue = statement.getNullOrElseBranch().getIntegerValue();
        boolean nullKey = (statement.hasNullKey() || param.mindustryContent()) && !Objects.equals(zeroValue, nullValue);

        int outputOffset = analyzer.getValues().stream().allMatch(Utf8Utils::canEncode) ? 0
                : analyzer.getMin() >= 0 &&analyzer.getMax() < 60 ? '0'
                : Utf8Utils.SAFE_START - analyzer.getMin();

        boolean noOutsideRange = (firstKey == 0 || paddingLow) && paddingHigh;
        boolean handleMapExtras = !unsafe && (outputOffset != 0 || !nullOnElseBranch && !noOutsideRange);

        boolean combinedElseNullHandling = handleOutputNull && handleMapExtras && analyzer.getMin() + outputOffset > 1
                && statement.getElseBranch().getAssignValue() == LogicNull.NULL;
        if (combinedElseNullHandling) {
            handleOutputNull = false;
            handleMapExtras = false;
        }

        boolean outputContent = analyzer.isMindustryContent();

        int originalSize = contextInstructions(param.context).realSize();
        int size = 1
                + flag(param.mindustryContent || inputOffset)
                + flag(outputOffset != 0)
                + flag(nullKey)
                + flag(handleOutputNull)
                + flag(handleMapExtras)
                + flag(combinedElseNullHandling)
                + flag(outputContent);

        int originalSteps = param.originalSteps + 2 * statement.size() + statement.getElseValues();
        // We're executing every instruction: steps == size
        double rawBenefit = (double) originalSteps / param.statement.getTotalSize() - size;
        double benefit = rawBenefit * param.context.totalWeight();

        return new TranslateCaseExpressionAction(param.duplicate(), analyzer, outputVariable, paddingLow, paddingHigh,
                inputOffset, nullKey, outputOffset, handleOutputNull, handleMapExtras, combinedElseNullHandling, outputContent, size,
                size - originalSize, originalSteps, benefit);
    }

    private int flag(boolean flag) {
        return flag ? 1 : 0;
    }

    private class TranslateCaseExpressionAction extends AbstractOptimizationAction implements ConvertCaseExpressionAction {
        private final int id = ++actionCounter;
        private final ConvertCaseActionParameters param;
        private final ValueAnalyzer analyzer;
        private final LogicVariable outputVariable;
        private final boolean paddingLow;
        private final boolean paddingHigh;
        private final boolean inputOffset;
        private final boolean nullKey;
        private final int outputOffset;
        private final boolean handleOutputNull;
        private final boolean handleMapExtras;
        private final boolean combinedElseNullHandling;
        private final boolean outputContent;
        private final int nullPlaceholder;

        private final int size;
        private final int originalSteps;

        private boolean applied;

        public TranslateCaseExpressionAction(ConvertCaseActionParameters param, ValueAnalyzer analyzer,
                LogicVariable outputVariable, boolean paddingLow, boolean paddingHigh, boolean inputOffset, boolean nullKey,
                int outputOffset, boolean handleOutputNull, boolean handleMapExtras, boolean combinedElseNullHandling,
                boolean outputContent, int size, int cost, int originalSteps, double benefit) {
            super(param.context, cost, benefit);
            this.param = param;
            this.analyzer = analyzer;
            this.outputVariable = outputVariable;
            this.paddingLow = paddingLow;
            this.paddingHigh = paddingHigh;
            this.inputOffset = inputOffset;
            this.nullKey = nullKey;
            this.outputOffset = outputOffset;
            this.handleOutputNull = handleOutputNull;
            this.handleMapExtras = handleMapExtras;
            this.combinedElseNullHandling = combinedElseNullHandling;
            this.outputContent = outputContent;
            this.nullPlaceholder = combinedElseNullHandling
                    ? analyzer.getMin() + outputOffset > 32 ? 32 : 1
                    : outputOffset + analyzer.getMax() + 1;

            this.size = size;
            this.originalSteps = originalSteps;
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            return applyOptimization(this::translateCaseExpression, toString());
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public int rawCost() {
            return size;
        }

        @Override
        public int originalSteps() {
            return originalSteps;
        }

        @Override
        public int executionSteps() {
            return size;
        }

        @Override
        public boolean applied() {
            return applied;
        }

        @Override
        public @Nullable String getGroup() {
            return "CaseSwitcher" + param.group;
        }

        private @Nullable LogicInstruction lastInstruction;
        private int index;

        private OptimizationResult translateCaseExpression() {
            final CaseStatement statement = param.statement;

            AstContext initContext = astContext.findSubcontext(INIT);
            Predicate<LogicInstruction> matcher = ix -> ix.belongsTo(astContext) && !ix.belongsTo(initContext);

            // We'll completely replace the case statement with a new body
            index = firstInstructionIndex(matcher);
            AstContext newAstContext = astContext.existingParent().createChild(astContext.existingNode(), AstContextType.BODY);
            LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(instructionProcessor,
                    newAstContext, instruction -> optimizationContext.insertInstruction(index++, lastInstruction = instruction));

            // Remove the original code entirely
            removeMatchingInstructions(matcher);
            applied = true;

            LogicVariable input;
            if (param.mindustryContent) {
                input = creator.nextTemp();
                creator.createSensor(input, param.variable, LogicBuiltIn.ID);
            } else {
                input = param.variable;
            }

            if (inputOffset) {
                LogicVariable tmp = creator.nextTemp();
                creator.createOp(Operation.SUB, tmp, input, LogicNumber.create(statement.firstKey()));
                input = tmp;
            }

            LogicVariable origOutput = creator.nextTemp();
            LogicVariable prevOutput = origOutput;
            creator.createRead(origOutput, createTranslationString(), input);

            // CASE 2: subtracting offset
            LogicVariable output = outputOffset != 0 ? creator.nextTemp() : prevOutput;
            if (outputOffset != 0) {
                creator.createOp(Operation.SUB, output, prevOutput, LogicNumber.create(outputOffset));
            }

            // CASE 3: providing value for a null key
            if (nullKey) {
                prevOutput = output;
                output = creator.nextTemp();
                creator.createSelect(output, Condition.STRICT_EQUAL, input, LogicNull.NULL,
                        statement.getNullOrElseBranch().getAssignValue(), prevOutput);
            }

            // CASE 4: handling null as an output value
            if (handleOutputNull) {
                prevOutput = output;
                output = creator.nextTemp();
                LogicValue nullPlaceholderValue = LogicNumber.create(nullPlaceholder);   // Translated value representing null
                creator.createSelect(output, Condition.EQUAL, origOutput, nullPlaceholderValue,
                        LogicNull.NULL, prevOutput);
            }

            // CASE 5: setting the value of the else branch
            if (handleMapExtras) {
                prevOutput = output;
                output = creator.nextTemp();
                creator.createSelect(output, Condition.STRICT_EQUAL, origOutput, LogicNull.NULL,
                        statement.getElseBranch().getAssignValue(), prevOutput);
            }

            // CASE 6: handling null AND the else value (replaces 4 + 5)
            if (combinedElseNullHandling) {
                prevOutput = output;
                output = creator.nextTemp();
                LogicValue nullPlaceholderValue = LogicNumber.create(nullPlaceholder);   // Translated value representing null
                creator.createSelect(output, Condition.LESS_THAN_EQ, origOutput, nullPlaceholderValue,
                        statement.getElseBranch().getAssignValue(), prevOutput);
            }

            // CASE 7: mapping to logic content
            if (outputContent) {
                LogicKeyword keyword = LogicKeyword.create(Objects.requireNonNull(analyzer.contentType).getLookupKeyword());
                creator.createLookup(keyword, outputVariable, output);
            } else {
                assert lastInstruction != null;
                BaseResultInstruction resultInstruction = ((BaseResultInstruction) lastInstruction).withResult(outputVariable);
                replaceInstruction(index - 1, resultInstruction);
            }

            return OptimizationResult.REALIZED;
        }

        private LogicString createTranslationString() {
            int firstKey = paddingLow ? 0 : param.statement.firstKey();
            int lastKey = paddingHigh ? param.statement.getTotalSize() - 1 : param.statement.lastKey();

            String encoded = Utf8Utils.encode(IntStream.rangeClosed(firstKey, lastKey).map(this::mapKeyToValue));
            return LogicString.create(encoded);
        }

        private int mapKeyToValue(int key) {
            Integer value = param.statement.getBranch(key).getIntegerValue();
            return value == null ?  nullPlaceholder : outputOffset + value;
        }

        @Override
        public String toString() {
            String strId = CaseSwitcher.super.isDebugOutput() ? " (#" + id + ")" : "";
            return "Translate case at " + Objects.requireNonNull(param.context.node()).sourcePosition().formatForLog() + strId;
        }
    }

    private class CaseSwitchingExpressionAction implements ConvertCaseExpressionAction {
        private final int id = ++actionCounter;
        private final ConvertCaseActionParameters param;
        private final List<Segment> segments;
        private final String padding;
        private final boolean moveAllBodies;
        private int cost;
        private int executionSteps;
        private double benefit;
        private double rawBenefit;
        private boolean applied;

        private CaseSwitchingExpressionAction(ConvertCaseActionParameters param, List<Segment> segments, String padding, boolean moveAllBodies) {
            this.param = param;
            this.segments = segments.stream().map(Segment::duplicate).toList();
            this.padding = padding;
            this.moveAllBodies = moveAllBodies;

            debugOutput("%n%n*** %s ***%n", this);

            if (param.removeRangeCheck) {
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
        public GenerationGoal goal() {
            return param.context.getLocalProfile().getGoal();
        }

        @Override
        public Optimization optimization() {
            return optimization;
        }

        @Override
        public AstContext astContext() {
            return param.context;
        }

        @Override
        public int cost() {
            return cost - param.originalCost;
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
            return param.originalSteps;
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
            return applyOptimization(this::convertCaseExpression, toString());
        }

        @Override
        public @Nullable String getGroup() {
            return "CaseSwitcher" + param.group;
        }

        @Override
        public String toString() {
            int numSegments = segments.size() - (segments.getFirst().empty() ? 1 : 0) - (segments.getLast().empty() ? 1 : 0);
            String strId = CaseSwitcher.super.isDebugOutput() ? "#" + id + ", " : "";
            return "Convert case at " + Objects.requireNonNull(param.context.node()).sourcePosition().formatForLog() +
                    " (" + strId + "segments: " + numSegments + (padding.isEmpty() ? "" : ", " + padding) +
                    (moveAllBodies && CaseSwitcher.super.isDebugOutput() ? ", embed all)" : ")");
        }

        private void computeCostAndBenefitNoRangeCheck() {
            if (segments.size() != 1) throw new MindcodeInternalError("Unexpected number of segments");
            Segment segment = segments.getFirst();

            int min = segment.from();
            int max = segment.to();

            int symbolicCost = param.symbolic && min != 0 ? 1 : 0;      // Cost of computing offset
            int contentCost = param.mindustryContent ? 1 : 0;           // Cost of converting type to logic ID
            int jumpTableCost = (max - min) + 1;                        // Table size plus initial jump
            cost = jumpTableCost + symbolicCost + contentCost;

            int averageSteps = 2 + symbolicCost + contentCost;          // 1x multiJump, 1x jump table, additional costs
            rawBenefit = (double) param.originalSteps / param.statement.getTotalSize() - averageSteps;
            benefit = rawBenefit * param.context.totalWeight();
            executionSteps = averageSteps * param.statement.size();

            debugOutput("Original steps: %d, new steps: %d", param.originalSteps, executionSteps);
            debugOutput("Original size: %d, new size: %d, cost: %d", param.originalCost, cost, cost - param.originalCost);
            if (isDebugOutput() && !padding.isEmpty()) debugOutput("*** " + padding.toUpperCase() + " ***");
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
            int contentCost = param.mindustryContent ? 1 : 0;   // Cost of converting type to logic ID
            cost = contentCost;
            targetSteps = contentCost * param.statement.size();
            elseSteps = contentCost * param.statement.getElseValues();

            debugOutput(this);
            debugOutput("Case expression initialization: instructions: %d, average steps: %d, total steps: %d",
                    contentCost, contentCost, contentCost * param.statement.getTotalSize());

            // Account for null handling: an instruction per zero value
            // Note: null handling when zero is not there is accounted for in individual segments
            if (param.handleNulls() && param.statement.hasNullOrZeroKey()) {
                if (param.statement.hasZeroKey()) {
                    // There's a handler on the `0` branch
                    double nullHandling = 1.0 / param.statement.getTotalSize();
                    if (isDebugOutput()) debugOutput("Null handling: instructions: 1, average steps: %g, total steps: 1", nullHandling);
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
                    LogicLabel label = segment.endLabel() == LogicLabel.INVALID ? param.statement.getExisting(0) : segment.endLabel();
                    if (segment.embedded() && !moved.add(label)) {
                        segment.setEmbeddingSize(param.statement.getBranchSize(label) + 1);
                    }
                }
            }

            segments.forEach(this::computeSegmentCostAndBenefit);

            if (param.considerElse) {
                debugOutput("Original steps: %d, new steps: %d (target: %d, else: %d; bisection: %s)",
                        param.originalSteps, targetSteps + elseSteps, targetSteps, elseSteps, bisectionSteps);

                rawBenefit = (double) (param.originalSteps - targetSteps - elseSteps) / param.statement.getTotalSize();
                executionSteps = targetSteps + elseSteps;
            } else {
                // We disregard else steps in both computations
                debugOutput("Original steps: %d, new steps: %d (target: %d, disregarding else; bisection: %d)", param.originalSteps,
                        targetSteps, targetSteps, bisectionSteps);

                rawBenefit = (double) (param.originalSteps - targetSteps) / param.statement.size();
                executionSteps = targetSteps;
            }
            benefit = rawBenefit * param.context.totalWeight();

            debugOutput("Original size: %d, new size: %d, cost: %d", param.originalCost, cost, cost - param.originalCost);
            if (isDebugOutput() && !padding.isEmpty()) debugOutput("*** " + padding.toUpperCase() + " ***");
            debugOutput("");
        }

        private @Nullable AstContext newAstContext;
        private @Nullable LogicVariable caseVariable;
        private int index;

        private void computeSegmentCostAndBenefit(Segment segment) {
            int activeTargets = param.statement.targetCount(segment);

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
                    ? segment.handleNulls() ? param.statement.getNullOrElseTarget() : finalLabel
                    : segment.majorityLabel() == LogicLabel.INVALID ? param.statement.getExisting(0) : segment.majorityLabel();
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
                int majorityTargets = (int) param.statement.entrySet().stream().filter(
                        e -> segment.contains(e.getKey()) && segment.majorityLabel().equals(e.getValue().label)).count();
                int activeSteps = 0;
                int elseSteps = 0;

                // This counts the number of steps for values that are handled by the select table.
                // Traversing the jumps in the opposite order from the one in which they'll be built,
                // so that the size always corresponds to the number of instructions the not-yet-handled
                // values will see.
                int size = 0;
                for (int value = segment.to() - 1; value >= segment.from(); value--) {
                    LogicLabel target = param.statement.get(value);
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
                LogicLabel target = param.statement.getOrDefault(value, finalLabel);
                if (!majorityTarget.equals(target)) {
                    insertInstruction(createJump(newAstContext, target, Condition.EQUAL, caseVariable, LogicNumber.create(value)));
                }
            }

            if (segment.embedded()) {
                moveBody(segment.endLabel());
            } else {
                if (segment.handleNulls() && majorityIsElse) {
                    insertInstruction(createJumpUnconditional(newAstContext, param.statement.getNullOrElseTarget()));
                } else {
                    insertInstruction(createJumpUnconditional(newAstContext, majorityTarget));
                }
            }
        }

        private SegmentStats computeJumpTable(Segment segment, int activeTargets) {
            int multiJump = param.symbolic && segment.from() != 0 ? 2 : 1;
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
                    LogicLabel target = param.statement.getOrDefault(i, finalLabel);
                    LogicLabel updatedTarget = i == 0 && target == finalLabel ? param.statement.getNullOrElseTarget() : target;
                    if (!labelMap.containsKey(updatedTarget)) {
                        LabelInstruction labelInstruction = optimizationContext.getLabelInstruction(updatedTarget);
                        LogicLabel jumpLabel = instructionProcessor.nextLabel();
                        labelMap.put(updatedTarget, jumpLabel);
                        insertBefore(labelInstruction, createMultiLabel(labelInstruction.getAstContext(), jumpLabel, marker).setJumpTarget());
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
                    LogicLabel target = param.statement.getOrDefault(segment.from() + i, finalLabel);
                    LogicLabel updatedTarget = segment.from() + i == 0 && target == finalLabel ? param.statement.getNullOrElseTarget() : target;
                    insertInstruction(createJumpUnconditional(newAstContext, updatedTarget));
                }

                if (segment.embedded()) {
                    insertInstruction(createMultiLabel(newAstContext, labels.getLast(), marker));
                    moveBody(segment.endLabel());
                }
            }
        }

        private record BranchContents(LogicList body, LogicList jump) { }
        private final Map<LogicLabel, BranchContents> branches = new HashMap<>();

        private void moveBody(LogicLabel endLabel) {
            LogicLabel label = endLabel == LogicLabel.INVALID ? param.statement.getExisting(0) : endLabel;
            BranchContents branch = branches.get(label);

            if (branch == null) {
                // Moving the branch body for the first time: extract and remove the original instructions
                int startIndex = optimizationContext.getLabelInstructionIndex(label);

                AstContext labelContext = optimizationContext.instructionAt(startIndex).getAstContext();
                LogicList labelInstructions = optimizationContext.contextInstructions(labelContext);
                int multiLabels = (int) labelInstructions.stream().filter(LogicInstruction::isJumpTarget).count();
                startIndex -= multiLabels;
                int labelsSize = labelInstructions.size() - multiLabels;
                if (!labelContext.matches(AstContextType.CASE, FLOW_CONTROL) || (labelsSize != 1 && labelsSize != 3))
                    throw new MindcodeInternalError("Unexpected context structure");

                AstContext bodyContext = optimizationContext.instructionAt(startIndex + labelInstructions.size()).getAstContext();
                while (bodyContext.existingParent() != astContext()) bodyContext = bodyContext.existingParent();
                LogicList bodyInstructions = optimizationContext.contextInstructions(bodyContext);
                if (!bodyContext.matches(AstContextType.CASE, AstSubcontextType.BODY))
                    throw new MindcodeInternalError("Unexpected context structure");

                AstContext jumpContext = optimizationContext.instructionAt(startIndex + labelInstructions.size() + bodyInstructions.size()).getAstContext();
                LogicList jumpInstructions = optimizationContext.contextInstructions(jumpContext);
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
            LogicList body = branch.body.duplicate(true);
            LogicList jump = branch.jump.duplicate(false);

            body.forEach(this::insertInstruction);
            insertInstruction(Objects.requireNonNull(jump.getFirst()));

            // By moving the body here, we've broken the common context. Start a new one.
            newAstContext = param.context.createSubcontext(FLOW_CONTROL, 1.0);
        }

        private void computeEmbedding() {
            // If we can't embed, remember the label to avoid
            LogicLabel avoidLabel = canEmbedZero() ? null : param.statement.get(0);

            Map<LogicLabel, Segment> bestSegments = new HashMap<>();
            for (Segment segment : segments) {
                // We won't embed a jump table segment in case of text-based jump tables
                if (segment.type() == SegmentType.JUMP_TABLE && param.context.getLocalProfile().useTextJumpTables()) continue;

                LogicLabel label = segment.endLabel() == LogicLabel.INVALID ? param.statement.getExisting(0) : segment.endLabel();

                // We won't embed the else branch: we'd save a jump to the else branch,
                // but we'd need another jump to the end of the case statement anyway.
                if (label == LogicLabel.EMPTY || label == avoidLabel || !param.statement.isMovableLabel(label)) continue;

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
            LogicLabel zeroLabel = param.statement.get(0);
            if (zeroLabel == null) return false;

            // Return true if the zero target is not shared with anything else
            return param.statement.entrySet().stream().noneMatch(e -> e.getKey() != 0 && e.getValue().label == zeroLabel);
        }

        private int computeBisectionTable(List<Segment> segments, int depth) {
            int bisection = bisect(segments);
            if (bisection < 0) {
                segments.forEach(segment -> {
                    segment.setBisectionSteps(depth, false);
                    if (isDebugOutput()) debugOutput("%s%s", indenter.getIndent(depth), segment);
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
            if (isDebugOutput()) debugOutput("%sBisection at %d (%d to %d)", indenter.getIndent(depth), highSegments.getFirst().from(), min, max);

            int nextDepth = depth + 1;

            if (inlineSegment == InlineSegment.LOW) {
                lowSegments.getFirst().setBisectionSteps(nextDepth, true);
                if (isDebugOutput()) debugOutput("%s    %s", indenter.getIndent(depth), lowSegments.getFirst());
                return steps + computeBisectionTable(highSegments, nextDepth);
            } else if (inlineSegment == InlineSegment.HIGH) {
                highSegments.getFirst().setBisectionSteps(nextDepth, true);
                if (isDebugOutput()) debugOutput("%s    %s", indenter.getIndent(depth), highSegments.getFirst());
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
                            param.statement.subMap(segment.from(), segment.to()).size());
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
            return segment.empty() && !param.considerElse ? 0 : segment.size();
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

            index = firstInstructionIndex(Objects.requireNonNull(param.context.findSubcontext(CONDITION)));
            AstContext elseContext = param.context.findSubcontext(ELSE);
            AstContext finalContext = elseContext != null ? elseContext : Objects.requireNonNull(param.context.lastChild());
            LogicLabel finalLabel = obtainContextLabel(finalContext);

            param.statement.setNullOrElseTarget(finalLabel);

            newAstContext = param.context.createSubcontext(FLOW_CONTROL, 1.0);

            if (param.mindustryContent) {
                caseVariable = instructionProcessor.nextTemp();
                insertInstruction(createSensor(Objects.requireNonNull(param.context.parent()),
                        caseVariable, param.variable, LogicBuiltIn.ID));
            } else {
                caseVariable = param.variable;
            }

            if (param.handleNulls()) {
                // We need to install a null check
                LogicLabel zeroLabel = param.statement.get(0);
                if (zeroLabel == null) {
                    if (param.statement.getNullTarget() != null) {
                        // There's an explicit null branch, and no `0` branch
                        // Install a null check on the else branch:
                        // a 'jump strictEqual null' instruction in front of the else branch which jumps to the null branch
                        // This is targeted by the else branch
                        // The finalLabel skips this check and is to be used by code that is known not to handle nulls
                        int elseIndex = optimizationContext.getLabelInstructionIndex(finalLabel);
                        LogicInstruction elseLabelIx = instructionAt(elseIndex);
                        param.statement.setNullOrElseTarget(instructionProcessor.nextLabel());
                        optimizationContext.insertInstruction(elseIndex++, createLabel(elseLabelIx.getAstContext(),
                                param.statement.getNullOrElseTarget()));
                        optimizationContext.insertInstruction(elseIndex, createJump(elseLabelIx.getAstContext(),
                                param.statement.getNullTarget(), Condition.STRICT_EQUAL, caseVariable, LogicNull.NULL));
                    }
                } else {
                    // There's a zero branch. Need to install the handler there
                    if (param.statement.getNullTarget() == null) {
                        param.statement.setNullTarget(finalLabel);
                    }

                    LogicLabel nullOrZeroTarget = instructionProcessor.nextLabel();
                    param.statement.addBranchKey(0, nullOrZeroTarget);

                    int zeroIndex = optimizationContext.getLabelInstructionIndex(zeroLabel);
                    LogicInstruction zeroLabelIx = instructionAt(zeroIndex);
                    optimizationContext.insertInstruction(zeroIndex++, createLabel(zeroLabelIx.getAstContext(),
                            nullOrZeroTarget));
                    optimizationContext.insertInstruction(zeroIndex, createJump(zeroLabelIx.getAstContext(),
                            param.statement.getNullTarget(), Condition.STRICT_EQUAL, caseVariable, LogicNull.NULL));
                }
            }

            generateBisectionTable(segments, finalLabel);

            // Remove all conditions
            for (AstContext condition : param.context.findSubcontexts(CONDITION)) {
                removeMatchingInstructions(ix -> ix.belongsTo(condition));
            }

            count++;
            applied = true;
            return OptimizationResult.REALIZED;
        }
    }

    private class ValueAnalyzer {
        private final AstContext context;
        private boolean first = true;
        private @Nullable ContentType contentType;        // ContentType.UNKNOWN represents an integer
        private @Nullable Integer lastValue;

        private int min = Integer.MAX_VALUE;
        private int max = Integer.MIN_VALUE;
        private final Set<Integer> values = new HashSet<>();

        public ValueAnalyzer(AstContext context) {
            this.context = context;
        }

        public boolean inspect(LogicValue value) {
            if (value == LogicNull.NULL) {
                lastValue = null;
                return true;
            }

            ContentType category = categorize(value);

            if (first) {
                contentType = category;
                first = false;
            }

            return contentType == category && contentType != null;
        }

        public boolean inspectRange(int rangeLowValue, int rangeHighValue) {
            if (!first && contentType != ContentType.UNKNOWN) {
                contentType = null;
                return false;
            }

            contentType = ContentType.UNKNOWN;
            first = false;
            registerValue(rangeLowValue);
            registerValue(rangeHighValue);
            return true;
        }

        public @Nullable ContentType categorize(LogicValue value) {
            if (value.isNumericConstant() && value.isInteger()) {
                registerValue(value.getIntValue());
                return ContentType.UNKNOWN;
            } else if (advanced(context) && value instanceof LogicBuiltIn builtIn && builtIn.getObject() != null && canConvert(builtIn)) {
                registerValue(builtIn.getObject().logicId());
                return builtIn.getObject().contentType();
            }

            return null;
        }

        private void registerValue(int value) {
            lastValue = value;
            min = Math.min(min, value);
            max = Math.max(max, value);
            values.add(value);
        }

        private boolean canConvert(LogicBuiltIn builtIn) {
            MindustryContent object = builtIn.getObject();
            return object != null
                   && getGlobalProfile().getBuiltinEvaluation() != BuiltinEvaluation.NONE
                   && object.contentType().hasLookup
                   && object.logicId() >= 0
                   && (getGlobalProfile().getBuiltinEvaluation() == BuiltinEvaluation.FULL || metadata.isStableBuiltin(builtIn.getName()));
        }

        public @Nullable Integer getLastValue() {
            return lastValue;
        }

        public boolean isMindustryContent() {
            return contentType != ContentType.UNKNOWN;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public int getRange() {
            return max - min + 1;
        }

        public Set<Integer> getValues() {
            return values;
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
    /// @param elseSteps  The total number of steps needed for all `else` values handled by this segment to resolve.
    ///                   Only the else values that actually land on the else branch are included.
    record SegmentStats(int size, int values, int steps, int elseValues, int elseSteps) {
    }
}
