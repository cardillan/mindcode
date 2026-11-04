package io.github.cardillan.mc.mindcode.compiler.optimization.cases;

import io.github.cardillan.mc.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mc.mindcode.compiler.astcontext.AstContextType;
import io.github.cardillan.mc.mindcode.compiler.optimization.Optimization;
import io.github.cardillan.mc.mindcode.compiler.optimization.OptimizationContext;
import io.github.cardillan.mc.mindcode.compiler.optimization.OptimizationResult;
import io.github.cardillan.mc.mindcode.compiler.optimization.cases.TranslateCaseOptimization.Translation;
import io.github.cardillan.mc.mindcode.logic.arguments.*;
import io.github.cardillan.mc.mindcode.logic.instructions.BaseResultInstruction;
import io.github.cardillan.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import io.github.cardillan.mc.mindcode.logic.instructions.LogicInstruction;
import io.github.cardillan.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static io.github.cardillan.mc.mindcode.compiler.astcontext.AstSubcontextType.INIT;

@NullMarked
public class TranslateCaseOptimizationAction implements ConvertCaseOptimizationAction {
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

    TranslateCaseOptimizationAction(int id, OptimizationContext optimizationContext,
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

    @Override
    public String toString() {
        String strId = optimizationContext.getGlobalProfile().isDebugOutput() ? " (#" + id + ")" : "";
        return "Translate case at " + Objects.requireNonNull(param.context().node()).sourcePosition().formatForLog() + strId;
    }

    private @Nullable LogicInstruction lastInstruction;
    private int index;

    private OptimizationResult translateCaseExpression() {
        final AstContext astContext = param.context();

        AstContext initContext = astContext.findSubcontext(INIT);
        Predicate<LogicInstruction> matcher = ix -> ix.belongsTo(astContext);
        List<LogicInstruction> initialization = optimizationContext.instructions(ix -> ix.belongsTo(initContext));

        // We'll completely replace the case statement with a new body
        index = optimizationContext.firstInstructionIndex(matcher);
        AstContext newAstContext = astContext.existingParent().createChild(astContext.existingNode(), AstContextType.CODE);
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(optimizationContext.getInstructionProcessor(),
                newAstContext, instruction -> optimizationContext.insertInstruction(index++, lastInstruction = instruction));

        // Remove the original code entirely
        optimizationContext.removeMatchingInstructions(matcher);
        applied = true;

        // Reinsert initialization code
        initialization.forEach(ix -> optimizationContext.insertInstruction(index++, ix.withContext(newAstContext)));

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

    private void createTranslation(LocalContextfulInstructionsCreator creator, LogicVariable input, Translation t) {
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

        String encoded = optimizationContext.getInstructionProcessor().encode(IntStream.rangeClosed(firstKey, lastKey).map(this::mapKeyToValue).toArray());
        return LogicString.createEscaped(encoded);
    }

    private int mapKeyToValue(int key) {
        Integer value = param.caseExpression().getBranch(key).getIntegerValue(variable);
        return value == null ?  nullPlaceholder : value == Integer.MAX_VALUE ? voidPlaceholder : outputOffset + value;
    }
}
