package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationResult;
import info.teksol.mc.mindcode.compiler.optimization.cases.CaseExpression.Branch;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.BaseResultInstruction;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.BuiltinEvaluation;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import info.teksol.mc.util.Utf8Utils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.INIT;

@NullMarked
public class TranslateCaseOptimizationAction implements ConvertCaseOptimizationAction {
    private static final int MAX_TABLE_PADDING = 100;

    private final int id;
    private final OptimizationContext optimizationContext;
    private final BiFunction<Supplier<OptimizationResult>, String, OptimizationResult> optimizationApplier;
    private final ConvertCaseActionParameters param;
    private final List<Translation> translations;
    private final boolean paddingLow;
    private final boolean paddingHigh;
    private final boolean inputOffset;

    private final int size;
    private final int originalSteps;
    private final int cost;
    private final double benefit;

    private boolean applied;

    private TranslateCaseOptimizationAction(int id, OptimizationContext optimizationContext,
            BiFunction<Supplier<OptimizationResult>, String, OptimizationResult> optimizationApplier,
            ConvertCaseActionParameters param, List<Translation> translations, boolean paddingLow,
            boolean paddingHigh, boolean inputOffset, int size, int cost, int originalSteps, double benefit) {
        this.id = id;
        this.optimizationContext = optimizationContext;
        this.optimizationApplier = optimizationApplier;
        this.param = param;
        this.translations = List.copyOf(translations);
        this.paddingLow = paddingLow;
        this.paddingHigh = paddingHigh;
        this.inputOffset = inputOffset;

        this.size = size;
        this.originalSteps = originalSteps;
        this.cost = cost;
        this.benefit = benefit;
    }

    @Override
    public int getId() {
        return id;
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
        return cost;
    }

    @Override
    public int rawCost() {
        return size;
    }

    @Override
    public double benefit() {
        return benefit;
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
    public OptimizationResult apply(int costLimit) {
        return optimizationApplier.apply(this::translateCaseExpression, toString());
    }

    @Override
    public @Nullable String getGroup() {
        return "CaseSwitcher" + param.group();
    }

    private @Nullable LogicInstruction lastInstruction;
    private int index;

    private OptimizationResult translateCaseExpression() {
        final AstContext astContext = param.context();

        AstContext initContext = astContext.findSubcontext(INIT);
        Predicate<LogicInstruction> matcher = ix -> ix.belongsTo(astContext) && !ix.belongsTo(initContext);

        // We'll completely replace the case statement with a new body
        index = optimizationContext.firstInstructionIndex(matcher);
        AstContext newAstContext = astContext.existingParent().createChild(astContext.existingNode(), AstContextType.BODY);
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(optimizationContext.getInstructionProcessor(),
                newAstContext, instruction -> optimizationContext.insertInstruction(index++, lastInstruction = instruction));

        // Remove the original code entirely
        optimizationContext.removeMatchingInstructions(matcher);
        applied = true;

        LogicVariable input;
        if (param.mindustryContent()) {
            input = creator.nextTemp();
            creator.createSensor(input, param.variable(), LogicBuiltIn.ID);
        } else {
            input = param.variable();
        }

        if (inputOffset) {
            LogicVariable tmp = creator.nextTemp();
            creator.createOp(Operation.SUB, tmp, input, LogicNumber.create(param.caseExpression().firstKey()));
            input = tmp;
        }

        LogicVariable finalInput = input;

        translations.stream()
                .sorted(Comparator.comparingInt(t -> t.outputVariable.equals(finalInput) ? 1 : 0))
                .forEach(translation -> createTranslation(creator, finalInput, translation));

        return OptimizationResult.REALIZED;
    }

    private LogicVariable variable = LogicVariable.INVALID;
    private int outputOffset;
    private int nullPlaceholder;
    private int voidPlaceholder;

    private void createTranslation(LocalContextfulInstructionsCreator creator, LogicVariable input,
            Translation t) {
        final CaseExpression expression = param.caseExpression();
        final ValueAnalyzer analyzer = t.analyzer;

        variable = t.outputVariable;
        outputOffset = t.outputOffset;
        nullPlaceholder = t.combinedElseNullHandling
                ? analyzer.getMin() + outputOffset > 33 ? 33 : analyzer.getMin() + outputOffset > 32 ? 32 : 1
                : outputOffset + analyzer.getMax() + 1;
        voidPlaceholder = t.combinedElseVoidHandling
                ? analyzer.getMin() + outputOffset > 33 ? 33 : analyzer.getMin() + outputOffset > 32 ? 32 : 1
                : outputOffset + analyzer.getMax() + 2;

        LogicVariable origOutput = creator.nextTemp();
        LogicVariable prevOutput = origOutput;
        creator.createRead(origOutput, createTranslationString(), input);

        // CASE 2: subtracting offset
        LogicVariable output = outputOffset != 0 ? creator.nextTemp() : prevOutput;
        if (outputOffset != 0) {
            creator.createOp(Operation.SUB, output, prevOutput, LogicNumber.create(outputOffset));
        }

        // CASE 3.1: providing value for a null key
        if (t.nullKey) {
            prevOutput = output;
            output = creator.nextTemp();
            creator.createSelect(output, Condition.STRICT_EQUAL, input, LogicNull.NULL,
                    expression.getNullOrElseBranch().getAssignedValueLiteral(variable, t.outputContent), prevOutput);
        }

        // CASE 4.1: handling null as an output value
        if (t.handleOutputNull) {
            prevOutput = output;
            output = creator.nextTemp();
            LogicValue nullPlaceholderValue = LogicNumber.create(nullPlaceholder);   // Translated value representing null
            creator.createSelect(output, Condition.EQUAL, origOutput, nullPlaceholderValue,
                    LogicNull.NULL, prevOutput);
        }

        // CASE 5.1: setting the value of the else branch
        if (t.handleNullMapExtras) {
            prevOutput = output;
            output = creator.nextTemp();
            creator.createSelect(output, Condition.STRICT_EQUAL, origOutput, LogicNull.NULL,
                    expression.getElseBranch().getAssignedValueLiteral(variable, t.outputContent), prevOutput);
        }

        // CASE 6.1: combined null and else handling (replaces 4.1 + 5.1)
        if (t.combinedElseNullHandling) {
            prevOutput = output;
            output = creator.nextTemp();
            LogicValue nullPlaceholderValue = LogicNumber.create(nullPlaceholder);   // Translated value representing null
            creator.createSelect(output, Condition.LESS_THAN_EQ, origOutput, nullPlaceholderValue,
                    LogicNull.NULL, prevOutput);
        }

        // CASE 7: mapping to logic content
        if (t.outputContent) {
            prevOutput = output;
            output = creator.nextTemp();
            LogicKeyword keyword = LogicKeyword.create(Objects.requireNonNull(analyzer.getContentType()).getLookupKeyword());
            creator.createLookup(keyword, output, prevOutput);
        }

        // CASE 3.2: providing value for a null key
        if (t.nullKeyVoid) {
            prevOutput = output;
            output = creator.nextTemp();
            creator.createSelect(output, Condition.STRICT_EQUAL, input, LogicNull.NULL,
                    variable, prevOutput);
        }

        // CASE 4.2: handling noop branch
        if (t.handleOutputVoid) {
            prevOutput = output;
            output = creator.nextTemp();
            LogicValue voidPlaceholderValue = LogicNumber.create(voidPlaceholder);   // Translated value representing null
            creator.createSelect(output, Condition.EQUAL, origOutput, voidPlaceholderValue,
                    variable, prevOutput);
        }

        // CASE 5.2: setting the value of the else branch
        if (t.handleVoidMapExtras) {
            prevOutput = output;
            output = creator.nextTemp();
            creator.createSelect(output, Condition.STRICT_EQUAL, origOutput, LogicNull.NULL,
                    variable, prevOutput);
        }

        // CASE 6.2: combined void and else handling (replaces 4.2 + 5.2)
        if (t.combinedElseVoidHandling) {
            prevOutput = output;
            output = creator.nextTemp();
            LogicValue nullPlaceholderValue = LogicNumber.create(voidPlaceholder);   // Translated value representing null
            creator.createSelect(output, Condition.LESS_THAN_EQ, origOutput, nullPlaceholderValue,
                    variable, prevOutput);
        }

        assert lastInstruction != null;
        BaseResultInstruction resultInstruction = ((BaseResultInstruction) lastInstruction).withResult(variable);
        optimizationContext.replaceInstruction(index - 1, resultInstruction);
    }

    private LogicString createTranslationString() {
        int firstKey = paddingLow ? 0 : param.caseExpression().firstKey();
        int lastKey = paddingHigh ? param.caseExpression().getTotalSize() - 1 : param.caseExpression().lastKey();

        String encoded = Utf8Utils.encode(IntStream.rangeClosed(firstKey, lastKey).map(this::mapKeyToValue));
        return LogicString.create(encoded);
    }

    private int mapKeyToValue(int key) {
        Integer value = param.caseExpression().getBranch(key).getIntegerValue(variable);
        return value == null ?  nullPlaceholder : value == Integer.MAX_VALUE ? voidPlaceholder : outputOffset + value;
    }

    @Override
    public String toString() {
        String strId = optimizationContext.getGlobalProfile().isDebugOutput() ? " (#" + id + ")" : "";
        return "Translate case at " + Objects.requireNonNull(param.context().node()).sourcePosition().formatForLog() + strId;
    }

    public static Optional<TranslateCaseOptimizationAction> create(int id, OptimizationContext optimizationContext,
            BiFunction<Supplier<OptimizationResult>, String, OptimizationResult> optimizationApplier, ConvertCaseActionParameters param) {
        CompilerProfile globalProfile = optimizationContext.getGlobalProfile();

        if (!globalProfile.getProcessorVersion().atLeast(ProcessorVersion.V8B)
                || !param.context().getLocalProfile().isUseTextTranslations()) return Optional.empty();

        CaseExpression expression = param.caseExpression();
        Collection<Branch> branches = expression.getBranches();

        int originalSize = optimizationContext.contextInstructions(param.context()).realSize();
        int originalSteps = param.originalSteps() + 2 * expression.size() + expression.getElseValues();
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
            if (analyzer.getContentType() == null || analyzer.getRange() >= Utf8Utils.MAX_SAFE_RANGE - 2)
                return Optional.empty();

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
            t.handleOutputNull = nullOnRegularBranch || t.nullOnElseBranch && (!compact || expression.firstKey() != 0);
            t.handleOutputVoid = voidOnRegularBranch || t.voidOnElseBranch;

            Integer zeroValue = Optional.of(expression.getBranch(0)).map(b -> b.getIntegerValue(variable)).orElse(null);
            Integer nullValue = expression.getNullOrElseBranch().getIntegerValue(variable);
            t.nullKey = (expression.hasNullKey() || param.mindustryContent()) && !Objects.equals(zeroValue, nullValue);
            if (t.nullKey && nullValue != null && nullValue == Integer.MAX_VALUE) {
                t.nullKey = false;
                t.nullKeyVoid = true;
            }

            t.outputOffset = analyzer.getValues().stream().allMatch(Utf8Utils::canEncode) ? 0
                    : analyzer.getMin() >= 0 && analyzer.getMax() < 60 ? '0'
                    : Utf8Utils.SAFE_START - analyzer.getMin();
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

        // We're executing every instruction: steps == size
        double rawBenefit = (double) originalSteps / param.caseExpression().getTotalSize() - size;
        double benefit = rawBenefit * param.context().totalWeight();

        return Optional.of(new TranslateCaseOptimizationAction( id, optimizationContext, optimizationApplier,
                param.duplicate(), translations, paddingLow, paddingHigh, inputOffset,
                size, size - originalSize, originalSteps, benefit));
    }

    private static int flag(boolean flag) {
        return flag ? 1 : 0;
    }

    private static final class Translation {
        LogicVariable outputVariable;
        private final ValueAnalyzer analyzer;
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

        private Translation(LogicVariable outputVariable, ValueAnalyzer analyzer) {
            this.outputVariable = outputVariable;
            this.analyzer = analyzer;
        }
    }
}
