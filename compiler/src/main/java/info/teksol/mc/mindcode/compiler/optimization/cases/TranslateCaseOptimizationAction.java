package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationResult;
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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    private final int cost;
    private final double benefit;

    private boolean applied;

    public TranslateCaseOptimizationAction(int id, OptimizationContext optimizationContext,
            BiFunction<Supplier<OptimizationResult>, String, OptimizationResult> optimizationApplier,
            ConvertCaseActionParameters param, ValueAnalyzer analyzer, LogicVariable outputVariable, boolean paddingLow,
            boolean paddingHigh, boolean inputOffset, boolean nullKey, int outputOffset, boolean handleOutputNull,
            boolean handleMapExtras, boolean combinedElseNullHandling, boolean outputContent, int size, int cost,
            int originalSteps, double benefit) {
        this.id = id;
        this.optimizationContext = optimizationContext;
        this.optimizationApplier = optimizationApplier;
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
        final CaseStatement statement = param.statement();

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
            LogicKeyword keyword = LogicKeyword.create(Objects.requireNonNull(analyzer.getContentType()).getLookupKeyword());
            creator.createLookup(keyword, outputVariable, output);
        } else {
            assert lastInstruction != null;
            BaseResultInstruction resultInstruction = ((BaseResultInstruction) lastInstruction).withResult(outputVariable);
            optimizationContext.replaceInstruction(index - 1, resultInstruction);
        }

        return OptimizationResult.REALIZED;
    }

    private LogicString createTranslationString() {
        int firstKey = paddingLow ? 0 : param.statement().firstKey();
        int lastKey = paddingHigh ? param.statement().getTotalSize() - 1 : param.statement().lastKey();

        String encoded = Utf8Utils.encode(IntStream.rangeClosed(firstKey, lastKey).map(this::mapKeyToValue));
        return LogicString.create(encoded);
    }

    private int mapKeyToValue(int key) {
        Integer value = param.statement().getBranch(key).getIntegerValue();
        return value == null ?  nullPlaceholder : outputOffset + value;
    }

    @Override
    public String toString() {
        String strId = optimizationContext.getGlobalProfile().isDebugOutput() ? " (#" + id + ")" : "";
        return "Translate case at " + Objects.requireNonNull(param.context().node()).sourcePosition().formatForLog() + strId;
    }

    public static Optional<TranslateCaseOptimizationAction> createTranslationAction(int id, OptimizationContext optimizationContext,
            BiFunction<Supplier<OptimizationResult>, String, OptimizationResult> optimizationApplier, ConvertCaseActionParameters param) {
        CompilerProfile globalProfile = optimizationContext.getGlobalProfile();

        if (!globalProfile.getProcessorVersion().atLeast(ProcessorVersion.V8B)
                || !param.context().getLocalProfile().isUseTextTranslations()) return Optional.empty();

        CaseStatement statement = param.statement();
        Collection<CaseStatement.Branch> branches = statement.getBranches();

        // Do all branches assign to the same variable?
        List<LogicVariable> variables = branches.stream().map(CaseStatement.Branch::getAssignTarget).distinct().toList();
        if (variables.size() != 1 || variables.getFirst() == LogicVariable.INVALID) return Optional.empty();
        LogicVariable outputVariable = variables.getFirst();

        // Are all assigned values of the same type and in a supported range?
        ValueAnalyzer analyzer = new ValueAnalyzer(globalProfile, optimizationContext.getInstructionProcessor().getMetadata(),
                true);
        branches.forEach(b -> {
            analyzer.inspect(b.getAssignValue());
            b.setIntegerValue(analyzer.getLastValue());
        });
        if (analyzer.getContentType() == null || analyzer.getRange() >= Utf8Utils.MAX_SAFE_RANGE) return Optional.empty();

        boolean nullOnElseBranch = statement.getElseBranch().getAssignValue() == LogicNull.NULL;
        boolean nullOnRegularBranch = branches.stream().filter(b -> b != statement.getElseBranch())
                .anyMatch(b -> b.getAssignValue() == LogicNull.NULL);

        // Ways in which null output values can appear in the map:
        // 1. Null on a regular branch
        // 2. Null on an else branch, while the map is not compact (the map is always compact when unsafe, because
        //    unhandled values aren't supposed to occur).
        boolean unsafe = param.context().getLocalProfile().isUnsafeCaseOptimization() && !statement.hasDeclaredElseBranch();
        boolean compact = unsafe || statement.range() == statement.size();
        boolean handleOutputNull = nullOnRegularBranch || nullOnElseBranch && !compact;

        // We can pad if nulls can't appear in the padding, or nulls in output are already handled.
        int firstKey = statement.firstKey();
        boolean paddingLow = firstKey > 0 && firstKey < MAX_TABLE_PADDING && (!nullOnElseBranch || handleOutputNull);
        boolean paddingHigh = param.mindustryContent() && globalProfile.getBuiltinEvaluation() == BuiltinEvaluation.FULL;

        // We need to compensate for the offset of the input value
        boolean inputOffset = firstKey != 0 && !paddingLow;

        Integer zeroValue = Optional.of(statement.getBranch(0)).map(CaseStatement.Branch::getIntegerValue).orElse(null);
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

        int originalSize = optimizationContext.contextInstructions(param.context()).realSize();
        int size = 1
                + flag(param.mindustryContent() || inputOffset)
                + flag(outputOffset != 0)
                + flag(nullKey)
                + flag(handleOutputNull)
                + flag(handleMapExtras)
                + flag(combinedElseNullHandling)
                + flag(outputContent);

        int originalSteps = param.originalSteps() + 2 * statement.size() + statement.getElseValues();
        // We're executing every instruction: steps == size
        double rawBenefit = (double) originalSteps / param.statement().getTotalSize() - size;
        double benefit = rawBenefit * param.context().totalWeight();

        return Optional.of(new TranslateCaseOptimizationAction( id, optimizationContext, optimizationApplier,
                param.duplicate(), analyzer, outputVariable, paddingLow, paddingHigh, inputOffset, nullKey, outputOffset,
                handleOutputNull, handleMapExtras, combinedElseNullHandling, outputContent, size, size - originalSize,
                originalSteps, benefit));
    }

    private static int flag(boolean flag) {
        return flag ? 1 : 0;
    }
}
