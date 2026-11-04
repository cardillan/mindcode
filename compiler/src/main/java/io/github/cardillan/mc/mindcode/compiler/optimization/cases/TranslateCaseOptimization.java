package io.github.cardillan.mc.mindcode.compiler.optimization.cases;

import io.github.cardillan.mc.mindcode.compiler.MindcodeInternalError;
import io.github.cardillan.mc.mindcode.compiler.optimization.OptimizationContext;
import io.github.cardillan.mc.mindcode.compiler.optimization.OptimizationResult;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicNull;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicValue;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicVoid;
import io.github.cardillan.mc.mindcode.logic.opcodes.ProcessorVersion;
import io.github.cardillan.mc.profile.BuiltinEvaluation;
import io.github.cardillan.mc.profile.CompilerProfile;
import io.github.cardillan.mc.util.UtfUtils;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@NullMarked
public class TranslateCaseOptimization {
    private static final int MAX_TABLE_PADDING = 100;

    public static Optional<ConvertCaseOptimizationAction> create(int id, OptimizationContext optimizationContext,
            BiFunction<Supplier<OptimizationResult>, String, OptimizationResult> optimizationApplier, ConvertCaseActionParameters param) {
        CompilerProfile globalProfile = optimizationContext.getGlobalProfile();

        if (!globalProfile.getProcessorVersion().atLeast(ProcessorVersion.V8B)) return Optional.empty();

        CaseExpression expression = param.caseExpression();
        Collection<CaseExpression.Branch> branches = expression.getBranches();
        boolean canTranslate = true;
        int size = 0;

        Set<LogicVariable> assignmentTargets = expression.getAssignmentTargets();

        // The case expression does absolutely nothing.
        if (assignmentTargets.isEmpty()) return Optional.empty();

        boolean unsafe = param.context().getLocalProfile().isUnsafeCaseOptimization() && !expression.hasDeclaredElseBranch();
        boolean compact = unsafe || expression.range() == expression.size();

        List<Translation> translations = new ArrayList<>();
        for (LogicVariable variable : assignmentTargets) {
            // Are all assigned values of the same type and in a supported range?
            // Note: LogicVoid.VOID represents the case where the original value of the variable shall remain unchanged
            ValueAnalyzer analyzer = new ValueAnalyzer(globalProfile, optimizationContext.getInstructionProcessor().getMetadata(),
                    true);
            branches.forEach(b -> {
                LogicValue assignedValue = b.getAssignedValue(variable);
                if (assignedValue != LogicVoid.VOID) {
                    analyzer.inspect(assignedValue);
                    b.setIntegerValue(variable, analyzer.getLastValue());
                } else {
                    b.setIntegerValue(variable, Integer.MAX_VALUE);
                }
            });
            if (analyzer.getContentType() == null || analyzer.getRange() >= UtfUtils.MAX_SAFE_RANGE - 2) {
                canTranslate = false;
            }

            Translation t = new Translation(variable, analyzer);
            translations.add(t);

            t.voidOnElseBranch = expression.getElseBranch().getAssignedValue(variable) == LogicVoid.VOID;
            boolean voidOnRegularBranch = branches.stream().filter(b -> b != expression.getElseBranch())
                    .anyMatch(b -> b.getAssignedValue(variable) == LogicVoid.VOID);

            t.nullOnElseBranch = expression.getElseBranch().getAssignedValue(variable) == LogicNull.NULL;
            boolean nullOnRegularBranch = branches.stream().filter(b -> b != expression.getElseBranch())
                    .anyMatch(b -> b.getAssignedValue(variable) == LogicNull.NULL);

            // Ways in which null/void output values can appear in the map:
            // 1. Null/void on a regular branch
            // 2. Null/void on an else branch, while the map is not compact (the map is always compact when unsafe because
            //    unhandled values aren't supposed to occur).
            t.handleOutputNull = nullOnRegularBranch || t.nullOnElseBranch && !compact;
            t.handleOutputVoid = voidOnRegularBranch || t.voidOnElseBranch;

            Integer zeroValue = Optional.of(expression.getBranch(0)).map(b -> b.getIntegerValue(variable)).orElse(null);
            Integer nullValue = expression.getNullOrElseBranch().getIntegerValue(variable);
            t.nullKey = (expression.hasNullKey() || param.mindustryContent()) && !Objects.equals(zeroValue, nullValue);
            if (t.nullKey && nullValue != null && nullValue == Integer.MAX_VALUE) {
                t.nullKey = false;
                t.nullKeyVoid = true;
            }

            t.outputOffset = analyzer.getValues().stream().allMatch(optimizationContext.getInstructionProcessor()::canEncode) ? 0
                    : analyzer.getMin() >= 0 && analyzer.getMax() < 60 ? '0'
                    : UtfUtils.SAFE_START - analyzer.getMin();
        }

        // We can pad if nulls can't appear in the padding, or nulls in output are already handled.
        int firstKey = expression.firstKey();
        boolean canPadLow = unsafe || translations.stream().allMatch(
                t -> (!t.nullOnElseBranch || t.handleOutputNull) && (!t.voidOnElseBranch || t.handleOutputVoid));
        boolean paddingLow = firstKey > 0 && firstKey < MAX_TABLE_PADDING && canPadLow;
        boolean paddingHigh = param.mindustryContent() && globalProfile.getBuiltinEvaluation() == BuiltinEvaluation.FULL;

        // We need to compensate for the offset of the input value
        boolean inputOffset = firstKey != 0 && !paddingLow;

        for (Translation t : translations) {
            boolean noOutsideRange = (firstKey == 0 || paddingLow) && paddingHigh;
            if (t.voidOnElseBranch) {
                t.handleVoidMapExtras = !unsafe && !noOutsideRange;
            } else {
                t.handleNullMapExtras = !unsafe && (t.outputOffset != 0 || !t.nullOnElseBranch && !noOutsideRange);
            }

            t.combinedElseNullHandling = t.handleOutputNull && t.handleNullMapExtras && t.analyzer.getMin() + t.outputOffset > 1
                    && expression.getElseBranch().getAssignedValue(t.outputVariable) == LogicNull.NULL;
            if (t.combinedElseNullHandling) {
                t.handleOutputNull = false;
                t.handleNullMapExtras = false;
            }

            t.combinedElseVoidHandling = t.handleOutputVoid && t.handleVoidMapExtras && t.analyzer.getMin() + t.outputOffset > 1
                    && expression.getElseBranch().getAssignedValue(t.outputVariable) == LogicVoid.VOID;
            if (t.combinedElseVoidHandling) {
                t.handleOutputVoid = false;
                t.handleVoidMapExtras = false;
            }

            // Here we know at most one of combinedElseNullHandling and combinedElseVoidHandling is true:
            if (t.combinedElseNullHandling && t.combinedElseVoidHandling) {
                throw new MindcodeInternalError("combinedElseNullHandling and combinedElseVoidHandling can't both be true");
            }

            t.outputContent = t.analyzer.isMindustryContent();

            size += 1
                    + flag(param.mindustryContent() || inputOffset)
                    + flag(t.outputOffset != 0)
                    + flag(t.nullKey)
                    + flag(t.nullKeyVoid)
                    + flag(t.handleOutputNull)
                    + flag(t.handleNullMapExtras)
                    + flag(t.combinedElseNullHandling)
                    + flag(t.handleOutputVoid)
                    + flag(t.handleVoidMapExtras)
                    + flag(t.combinedElseVoidHandling)
                    + flag(t.outputContent);
        }

        // Original stats
        int originalSize = optimizationContext.contextInstructions(param.context()).realSize();
        int originalSteps = param.originalSteps() + translations.size() * (expression.size() + (expression.hasDeclaredElseBranch() ? expression.getElseValues() : 0));

        // New stats: we're executing every instruction: steps == size
        int selectCount = expression.keySet().size() + flag(expression.hasNullKey());
        int selectSize = translations.size() * selectCount;

        if (param.context().getGlobalProfile().isUseSelectCase() && (selectSize <= size || !canTranslate)) {
            double rawBenefit = (double) originalSteps / param.caseExpression().getStepsSize() - selectSize;
            double benefit = rawBenefit * param.context().totalWeight();

            return Optional.of(new SelectCaseOptimizationAction(id, optimizationContext, optimizationApplier,
                    param.duplicate(), translations,
                    selectSize, selectSize - originalSize, param.originalSteps(), benefit));

        } else if (param.context().getLocalProfile().isUseTextTranslations() && canTranslate) {
            // We're executing every instruction: steps == size
            double rawBenefit = (double) originalSteps / param.caseExpression().getStepsSize() - size;
            double benefit = rawBenefit * param.context().totalWeight();

            return Optional.of(new TranslateCaseOptimizationAction(id, optimizationContext, optimizationApplier,
                    param.duplicate(), translations, paddingLow, paddingHigh, inputOffset,
                    size, size - originalSize, param.originalSteps(), benefit));
        } else {
            return Optional.empty();
        }
    }

    private static int flag(boolean flag) {
        return flag ? 1 : 0;
    }

    static final class Translation {
        final LogicVariable outputVariable;
        final ValueAnalyzer analyzer;
        boolean nullKey;                        // Explicitly map input null to an output value
        boolean nullKeyVoid;                    // Explicitly map input null to the original value
        int outputOffset;
        boolean nullOnElseBranch;
        boolean handleOutputNull;
        boolean handleNullMapExtras;
        boolean combinedElseNullHandling;
        boolean voidOnElseBranch;
        boolean handleOutputVoid;
        boolean handleVoidMapExtras;
        boolean combinedElseVoidHandling;
        boolean outputContent;

        Translation(LogicVariable outputVariable, ValueAnalyzer analyzer) {
            this.outputVariable = outputVariable;
            this.analyzer = analyzer;
        }
    }
}
