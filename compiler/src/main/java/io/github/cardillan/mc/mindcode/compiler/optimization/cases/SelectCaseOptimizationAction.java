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
import io.github.cardillan.mc.mindcode.logic.mimex.ContentType;
import io.github.cardillan.mc.mindcode.logic.mimex.MindustryMetadata;
import io.github.cardillan.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.github.cardillan.mc.mindcode.compiler.astcontext.AstSubcontextType.INIT;

@NullMarked
public class SelectCaseOptimizationAction implements ConvertCaseOptimizationAction {
    private final int id;
    private final OptimizationContext optimizationContext;
    private final BiFunction<Supplier<OptimizationResult>, String, OptimizationResult> optimizationApplier;
    private final ConvertCaseActionParameters param;
    private final List<Translation> translations;

    private final int size;
    private final int originalSteps;
    private final int cost;
    private final double benefit;

    private boolean applied;

    SelectCaseOptimizationAction(int id, OptimizationContext optimizationContext,
            BiFunction<Supplier<OptimizationResult>, String, OptimizationResult> optimizationApplier,
            ConvertCaseActionParameters param, List<Translation> translations,
            int size, int cost, int originalSteps, double benefit) {
        this.id = id;
        this.optimizationContext = optimizationContext;
        this.optimizationApplier = optimizationApplier;
        this.param = param;
        this.translations = List.copyOf(translations);

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
        return optimizationApplier.apply(this::selectCaseExpression, toString());
    }

    @Override
    public @Nullable String getGroup() {
        return "CaseSwitcher" + param.group();
    }

    @Override
    public String toString() {
        String strId = optimizationContext.getGlobalProfile().isDebugOutput() ? " (#" + id + ")" : "";
        return "Select case at " + Objects.requireNonNull(param.context().node()).sourcePosition().formatForLog() + strId;
    }

    private @Nullable LogicInstruction lastInstruction;
    private int index;

    private OptimizationResult selectCaseExpression() {
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

        translations.forEach(translation -> createSelect(creator, translation));

        return OptimizationResult.REALIZED;
    }

    private void createSelect(LocalContextfulInstructionsCreator creator, Translation translation) {
        CaseExpression expression = param.caseExpression();
        MindustryMetadata metadata = optimizationContext.getInstructionProcessor().getMetadata();
        ContentType inputContent = param.contentType();
        LogicVariable outputVariable = translation.outputVariable;

        // Start with else value
        LogicValue lastValue = expression.getElseBranch().getAssignedValue(outputVariable, outputVariable);

        for (int key : expression.keySet()) {
            LogicValue match = getValueLiteral(metadata, inputContent, key);
            LogicValue selectedValue = expression.getBranch(key).getAssignedValue(outputVariable, outputVariable);
            LogicVariable output = creator.nextTemp();
            creator.createSelect(output, Condition.EQUAL, param.variable(), match, selectedValue, lastValue);
            lastValue = output;
        }

        if (expression.hasNullKey()) {
            LogicValue nullValue = expression.getNullOrElseBranch().getAssignedValue(outputVariable, outputVariable);
            creator.createSelect(outputVariable, Condition.STRICT_EQUAL, param.variable(), LogicNull.NULL,
                    nullValue, lastValue);
        } else {
            assert lastInstruction != null;
            BaseResultInstruction resultInstruction = ((BaseResultInstruction) lastInstruction).withResult(outputVariable);
            optimizationContext.replaceInstruction(index - 1, resultInstruction);
        }
    }

    private LogicValue getValueLiteral(MindustryMetadata metadata, ContentType contentType, @Nullable Integer value) {
        return value == null ? LogicNull.NULL
                : contentType == ContentType.UNKNOWN ? LogicNumber.create(value)
                : LogicBuiltIn.create(Objects.requireNonNull(metadata.getNamedContentByLogicId(contentType, value)), false);
    }
}
