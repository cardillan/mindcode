package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstCaseExpression;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.optimization.cases.*;
import info.teksol.mc.profile.BuiltinEvaluation;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.BASIC;

/// Notes on null handling:
///
/// Nulls are handled if the expression contains either a `when null` clause or a `when 0` clause:
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
@NullMarked
public class CaseSwitcher extends BaseOptimizer {
    private final int caseConfiguration;
    private int actionCounter = 0;

    CaseSwitcher(OptimizationContext optimizationContext) {
        super(Optimization.CASE_SWITCHING, optimizationContext);
        this.caseConfiguration = getGlobalProfile().getCaseConfiguration();
    }

    private int invocations = 0;
    private int switchedCount = 0;
    private int translatedCount = 0;
    private int fastDispatchedCount = 0;

    private int groupCount = 0;

    @Override
    public void generateFinalMessages() {
        iterations = invocations;
        super.generateFinalMessages();
        if (switchedCount > 0) {
            emitMessage(MessageLevel.INFO, "%6d case expressions converted to switched jumps by %s.", switchedCount, getName());
        }
        if (translatedCount > 0) {
            emitMessage(MessageLevel.INFO, "%6d case expressions converted to value translations by %s.", translatedCount, getName());
        }
        if (fastDispatchedCount > 0) {
            emitMessage(MessageLevel.INFO, "%6d case expressions converted to fast dispatch by %s.", fastDispatchedCount, getName());
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
                returningNull((context -> findPossibleCaseSwitches(context, result))));
        return result;
    }

    @Override
    protected boolean isDebugOutput() {
        return super.isDebugOutput() && (caseConfiguration == actionCounter || actionCounter == 0);
    }

    private OptimizationResult applyOptimization(Supplier<OptimizationResult> optimization, String title, Runnable counter) {
        OptimizationResult optimizationResult = super.applyOptimization(optimization, title);
        if (optimizationResult == OptimizationResult.REALIZED) counter.run();
        return optimizationResult;
    }


    private OptimizationResult applySwitchedOptimization(Supplier<OptimizationResult> optimization, String title) {
        return applyOptimization(optimization, title, () -> switchedCount++);
    }

    private OptimizationResult applyTranslatedOptimization(Supplier<OptimizationResult> optimization, String title) {
        return applyOptimization(optimization, title, () -> translatedCount++);
    }

    private OptimizationResult applyFastDispatchOptimization(Supplier<OptimizationResult> optimization, String title) {
        return applyOptimization(optimization, title, () -> fastDispatchedCount++);
    }

    private void findPossibleCaseSwitches(AstContext context, List<OptimizationAction> result) {
        groupCount++;

        boolean declaredElseBranch = !(context.node() instanceof AstCaseExpression exp) || exp.isElseDefined();
        boolean removeRangeCheck = context.getLocalProfile().isUnsafeCaseOptimization() && !declaredElseBranch;

        ValueAnalyzer analyzer = new ValueAnalyzer(getGlobalProfile(), metadata, advanced(context));
        CaseExpression caseExpression = CaseExpression.analyze(optimizationContext, analyzer, context, declaredElseBranch);
        if (caseExpression == null || analyzer.getContentType() == null) return;

        ConvertCaseActionParameters param = new ConvertCaseActionParameters(groupCount, context, caseExpression.getVariable(), caseExpression,
                caseExpression.getOriginalCost(), caseExpression.getOriginalSteps(), analyzer.isMindustryContent(), removeRangeCheck,
                getGlobalProfile().isSymbolicLabels(), caseExpression.hasDeclaredElseBranch() && caseExpression.getElseValues() > 0);

        List<ConvertCaseOptimizationAction> actions = new ArrayList<>();
        if (caseExpression.supportsTranslation()) {
            TranslateCaseOptimizationAction.create(++actionCounter, optimizationContext,
                    this::applyTranslatedOptimization, param).ifPresent(actions::add);
        }

        if (getGlobalProfile().isNullCounterIsNoop() && context.getLocalProfile().useTextJumpTables()) {
            actions.add(new FastDispatchOptimizationAction(++actionCounter, optimizationContext,
                    this::applyFastDispatchOptimization, this::isDebugOutput, param));
        }

        int caseOptimizationStrength = context.getLocalProfile().getCaseOptimizationStrength();

        SegmentConfigurationGenerator segmentConfigurationGenerator = new CombinatorialSegmentConfigurationGenerator(caseExpression,
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
            List<ConvertCaseOptimizationAction> selected = new ArrayList<>();
            if (caseConfiguration > 0) {
                actions.stream().filter(a -> a.getId() == caseConfiguration).forEach(selected::add);
            }

            if (selected.isEmpty()) {
                // Comparing by cost (ascending) then by benefit (descending)
                actions.sort(Comparator.comparing(OptimizationAction::cost).thenComparing(Comparator.comparing(OptimizationAction::benefit).reversed()));
                ConvertCaseOptimizationAction last = null;
                for (ConvertCaseOptimizationAction action : actions) {
                    if (action instanceof TranslateCaseOptimizationAction) {
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
            optimizationContext.addDiagnosticData(ConvertCaseOptimizationAction.class, selected);
        }
    }

    private void createOptimizationActions(ConvertCaseActionParameters param, List<ConvertCaseOptimizationAction> actions,
            SegmentConfiguration segmentConfiguration) {
        List<Segment> segments = segmentConfiguration.createSegments(param.removeRangeCheck(), param.handleNulls(),
                param.symbolic(), param.caseExpression());

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

    private void addOptimizationAction(List<ConvertCaseOptimizationAction> actions, ConvertCaseActionParameters param,
            List<Segment> segments, String padding) {
        CaseSwitchingOptimizationAction action = new CaseSwitchingOptimizationAction(++actionCounter, optimizationContext,
                this::applySwitchedOptimization, this::isDebugOutput, param, segments, padding, false);
        actions.add(action);
        if (experimental(param.context())) {
            boolean canMoveMoreBodies = action.segments.stream().anyMatch(s -> s.moveable() && !s.embedded());
            if (canMoveMoreBodies) {
                actions.add(new CaseSwitchingOptimizationAction(++actionCounter, optimizationContext,
                        this::applySwitchedOptimization, this::isDebugOutput, param, segments, padding, true));
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

            if (parameters.mindustryContent()) {
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
        newSegments.set(index, curr.padHigh(parameters.caseExpression().getTotalSize()));
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
        if (!parameters.mindustryContent() || getGlobalProfile().getBuiltinEvaluation() != BuiltinEvaluation.FULL || parameters.removeRangeCheck()) return -1;

        // It can only be the very last segment
        int lastPossibleIndex = segments.getLast().type() == SegmentType.JUMP_TABLE ? segments.size() - 1 : segments.size() - 2;
        Segment segment = segments.get(lastPossibleIndex);
        if (segment.to() <= parameters.caseExpression().getTotalSize() && segment.type() == SegmentType.JUMP_TABLE
                && (segments.getLast() == segment || segments.getLast().empty())) {
            return lastPossibleIndex;
        }

        return -1;
    }
}
