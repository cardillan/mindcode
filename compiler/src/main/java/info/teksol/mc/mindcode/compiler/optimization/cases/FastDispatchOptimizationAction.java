package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationResult;
import info.teksol.mc.mindcode.compiler.optimization.cases.CaseExpression.Branch;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.GenerationGoal;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;

@NullMarked
public class FastDispatchOptimizationAction implements ConvertCaseOptimizationAction, ContextlessInstructionCreator {

    private final BooleanSupplier debugOutput;
    private final int id;
    private final OptimizationContext optimizationContext;
    private final InstructionProcessor instructionProcessor;
    private final BiFunction<Supplier<OptimizationResult>, String, OptimizationResult> optimizationApplier;
    private final ConvertCaseActionParameters param;
    private int cost;
    private int executionSteps;
    private double benefit;
    private boolean applied;

    public FastDispatchOptimizationAction(int id, OptimizationContext optimizationContext,
            BiFunction<Supplier<OptimizationResult>, String, OptimizationResult> optimizationApplier,
            BooleanSupplier debugOutput, ConvertCaseActionParameters param) {
        this.id = id;
        this.debugOutput = debugOutput;
        this.optimizationContext = optimizationContext;
        this.instructionProcessor = optimizationContext.getInstructionProcessor();
        this.optimizationApplier = optimizationApplier;
        this.param = param;

        debugOutput("%n%n*** %s ***%n", this);

        computeCostAndBenefit();
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
        String strId = optimizationContext.getGlobalProfile().isDebugOutput() ? " (#" + id + ")" : "";
        return "Fast-dispatch case at " + Objects.requireNonNull(param.context().node()).sourcePosition().formatForLog() + strId;
    }

    private void computeCostAndBenefit() {
        int commonInstructions = param.mindustryContent() ? 2 : 1;  // The jump plus an additional cost of converting type to logic ID
        int targetSteps = commonInstructions * param.caseExpression().size();
        int elseSteps = param.considerElse() ? commonInstructions * param.caseExpression().getElseValues() : 0;

        // Null handling is executed only on the `0` or `else` branch
        // We count it even when disregarding else. It's a small inaccuracy.
        int nullHandling = param.handleNulls() && param.caseExpression().hasNullOrZeroKey() ? 1 : 0;
        cost = commonInstructions + nullHandling;

        // Correction accounting for moving the max cardinality branch last (positive = more steps executed)
        Branch maxCardinalityBranch = param.caseExpression().findMaxCardinalityBranch();
        int correction = param.considerElse()
                ? param.caseExpression().getElseValues() - maxCardinalityBranch.getCardinality()
                : 0;

        double rawBenefit;
        if (param.considerElse()) {
            executionSteps = targetSteps + elseSteps + nullHandling + correction;
            rawBenefit = (double) (param.originalSteps() - executionSteps) / param.caseExpression().getTotalSize();

            debugOutput("Original steps: %d, new steps: %d (target: %d, else: %d, correction: %d, null handling: %d)",
                    param.originalSteps(), executionSteps, targetSteps, elseSteps, correction, nullHandling);
        } else {
            // We disregard else steps in both computations
            executionSteps = targetSteps + nullHandling;
            rawBenefit = (double) (param.originalSteps() - executionSteps) / param.caseExpression().size();

            debugOutput("Original steps: %d, new steps: %d (target: %d, correction: %d, null handling: %d, disregarding else)",
                    param.originalSteps(), executionSteps, targetSteps, correction, nullHandling);
        }
        benefit = rawBenefit * param.context().totalWeight();
    }

    private @Nullable AstContext newAstContext;
    private @Nullable LogicVariable caseVariable;
    private int index;

    protected void insertInstruction(LogicInstruction instruction) {
        optimizationContext.insertInstruction(index++, instruction);
    }

    private OptimizationResult convertCaseExpression() {
        debugOutput("Fast-dispatching case expression, configuration %d.", id);

        index = optimizationContext.firstInstructionIndex(Objects.requireNonNull(param.context().findSubcontext(CONDITION)));
        AstContext elseContext = param.context().findSubcontext(ELSE);
        AstContext finalContext = elseContext != null ? elseContext : Objects.requireNonNull(param.context().lastChild());
        LogicLabel finalLabel = optimizationContext.obtainContextLabel(finalContext);

        param.caseExpression().setNullOrElseTarget(finalLabel);

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
            LogicLabel zeroLabel = param.caseExpression().get(0);
            if (zeroLabel == null && param.caseExpression().getNullTarget() != null) {
                // There's a `null` branch and no `0` branch: map zero to the null branch,
                // if the value is zero, jump to the else branch
                param.caseExpression().addBranchKey(0, param.caseExpression().getNullTarget());
                int zeroIndex = optimizationContext.getLabelInstructionIndex(param.caseExpression().getNullTarget());
                LogicInstruction zeroLabelIx = optimizationContext.instructionAt(zeroIndex);
                optimizationContext.insertInstruction(zeroIndex + 1, createJump(zeroLabelIx.getAstContext(),
                        finalLabel, Condition.STRICT_EQUAL, caseVariable, LogicNumber.ZERO));
            } else if (zeroLabel != null) {
                // There's a `0` branch. Install the null handler on it.
                // If there's no explicit `null` branch, use the `else` branch to handle nulls.
                LogicLabel nullTarget = Objects.requireNonNullElse(param.caseExpression().getNullTarget(), finalLabel);

                // We'll redirect the `0` input value here
                LogicLabel nullOrZeroTarget = instructionProcessor.nextLabel();
                param.caseExpression().addBranchKey(0, nullOrZeroTarget);

                int zeroIndex = optimizationContext.getLabelInstructionIndex(zeroLabel);
                LogicInstruction zeroLabelIx = optimizationContext.instructionAt(zeroIndex);

                // Add a label and a jump to the null target
                optimizationContext.insertInstruction(zeroIndex++, createLabel(zeroLabelIx.getAstContext(),
                        nullOrZeroTarget));
                optimizationContext.insertInstruction(zeroIndex, createJump(zeroLabelIx.getAstContext(),
                        nullTarget, Condition.STRICT_EQUAL, caseVariable, LogicNull.NULL));
            }
        }

        generateJumpTable(finalLabel);

        LogicInstruction lastContextInstruction = optimizationContext.lastInstruction(astContext());
        if (!(lastContextInstruction instanceof LabelInstruction lastInstruction)) {
            throw new MindcodeInternalError("Unexpected context structure");
        }
        moveElseBranch(lastInstruction);

        // Remove all conditions
        for (AstContext condition : param.context().findSubcontexts(CONDITION)) {
            optimizationContext.removeMatchingInstructions(ix -> ix.belongsTo(condition));
        }

        Branch lastBranch = param.caseExpression().getRegularBranches().getLast();
        Branch maxCardinalityBranch = param.caseExpression().findMaxCardinalityBranch();
        if (maxCardinalityBranch.getCardinality() > lastBranch.getCardinality()) {
            moveBranchToEnd(maxCardinalityBranch, lastInstruction);
        }

        applied = true;
        return OptimizationResult.REALIZED;
    }

    private void generateJumpTable(LogicLabel finalLabel) {
        assert newAstContext != null;
        assert caseVariable != null;

        LogicLabel marker = instructionProcessor.nextLabel();

        // The when bodies have normal labels now. We need multilabels.
        // We keep the original labels since they might be in use elsewhere.
        // Thus, we need to create new ones and maintain a map.
        List<LogicLabel> labels = new ArrayList<>();
        Map<LogicLabel, LogicLabel> labelMap = new HashMap<>();
        for (int i = 0; i <= param.caseExpression().lastKey(); i++) {
            LogicLabel target = param.caseExpression().getOrDefault(i, finalLabel);
            LogicLabel updatedTarget = i == 0 && target == finalLabel ? param.caseExpression().getNullOrElseTarget() : target;
            if (!labelMap.containsKey(updatedTarget)) {
                LabelInstruction labelInstruction = optimizationContext.getLabelInstruction(updatedTarget);
                LogicLabel jumpLabel = instructionProcessor.nextLabel();
                labelMap.put(updatedTarget, jumpLabel);
                optimizationContext.insertBefore(labelInstruction, createMultiLabel(labelInstruction.getAstContext(), jumpLabel, marker)
                        .setJumpTarget().setFixedMultilabel(false));
            }
            labels.add(labelMap.get(updatedTarget));
        }

        insertInstruction(createMultiJump(newAstContext, caseVariable, marker).setJumpTable(labels).setFallThrough(true));
        insertInstruction(createMultiLabel(newAstContext, instructionProcessor.nextLabel(), marker));
    }

    private void moveElseBranch(LabelInstruction lastInstruction) {
        LogicLabel label = param.caseExpression().getElseBranch().label;
        LabelInstruction labelInstruction = optimizationContext.getLabelInstruction(label);
        AstContext labelContext = labelInstruction.getAstContext();
        LogicList labelInstructions = optimizationContext.contextInstructions(labelContext);

        int bodyIndex = optimizationContext.firstInstructionIndex(ix -> ix == labelInstructions.getLast()) + 1;
        AstContext bodyContext = optimizationContext.instructionAt(bodyIndex).getAstContext();
        LogicList bodyInstructions = optimizationContext.contextInstructions(bodyContext);

        while (labelInstructions.getFirst() instanceof EmptyInstruction) {
            labelInstructions.removeFirst();
        }

        if (labelInstructions.getFirst() instanceof JumpInstruction jump) {
            if (!lastInstruction.getLabel().equals(jump.getTarget())) {
                throw new MindcodeInternalError("Unexpected context structure");
            }
            labelInstructions.removeFirst();
        }

        optimizationContext.removeMatchingInstructions(ix -> ix.belongsTo(labelContext));
        optimizationContext.removeMatchingInstructions(ix -> ix.belongsTo(bodyContext));

        labelInstructions.duplicate(false).stream().skip(1).forEach(this::insertInstruction);
        AstContext newBodyContext = param.context().createSubcontext(ELSE, bodyContext.weight());
        LogicList newBody = bodyInstructions.duplicateToContext(newBodyContext, false, false);
        newBody.forEach(this::insertInstruction);

        AstContext newFlowContext = param.context().createSubcontext(FLOW_CONTROL, bodyContext.weight());
        insertInstruction(createJumpUnconditional(newFlowContext, lastInstruction.getLabel()));
    }

    private void moveBranchToEnd(Branch branch, LabelInstruction lastInstruction) {
        LabelInstruction labelInstruction = optimizationContext.getLabelInstruction(branch.label);
        AstContext flowContext = Objects.requireNonNull(param.context()
                .findDirectChild(labelInstruction.getAstContext()));
        LogicList flowList = optimizationContext.contextInstructions(flowContext);

        int bodyIndex = optimizationContext.firstInstructionIndex(flowContext) + flowList.size();
        AstContext bodyContext = Objects.requireNonNull(param.context()
                .findDirectChild(optimizationContext.instructionAt(bodyIndex).getAstContext()));
        LogicList bodyList = optimizationContext.contextInstructions(bodyContext);

        optimizationContext.removeMatchingInstructions(ix -> ix.belongsTo(flowContext));
        optimizationContext.removeMatchingInstructions(ix -> ix.belongsTo(bodyContext));

        index = optimizationContext.firstInstructionIndex(lastInstruction.getAstContext());
        insertInstruction(createJumpUnconditional(flowContext, lastInstruction.getLabel()));
        flowList.forEach(this::insertInstruction);
        bodyList.forEach(this::insertInstruction);
    }

    private void debugOutput(@PrintFormat String format, Object... args) {
        if (isDebugOutput()) {
            System.out.printf(format, args);
            System.out.println();
        }
    }

    private boolean isDebugOutput() {
        return debugOutput.getAsBoolean();
    }
}
