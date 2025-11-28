package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.JumpInstruction;
import info.teksol.mc.mindcode.logic.instructions.LabelInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.OpInstruction;
import info.teksol.mc.util.MutableDouble;
import org.jspecify.annotations.Nullable;

import java.util.*;

public abstract class AbstractConditionalOptimizer extends BaseOptimizer {
    public AbstractConditionalOptimizer(Optimization optimization, OptimizationContext optimizationContext) {
        super(optimization, optimizationContext);
    }

    protected @Nullable JumpInstruction negateCompoundCondition(LogicList condition) {
        if (condition.getRealFromEnd(0) instanceof JumpInstruction jump
                && condition.getRealFromEnd(1) instanceof OpInstruction op
                && op.getOperation().isCondition(getGlobalProfile()) && op.getResult().isTemporaryVariable()
                && jump.getCondition() == Condition.EQUAL && jump.getX().equals(op.getResult())
                && jump.getY().equals(LogicBoolean.FALSE)
                && instructionCount(ix -> ix.usesAsInput(op.getResult())) == 1) {

            return createJump(jump.getAstContext(), jump.getTarget(), op.getOperation().toExistingCondition(getGlobalProfile()), op.getX(), op.getY());
        } else {
            return null;
        }
    }

    protected @Nullable ShortCircuitAnalysis analyzeShortCircuit(LogicList conditionInstructions) {
        AstContext context = conditionInstructions.getExistingAstContext();
        boolean shortCircuit = context.children().size() == 1 && context.children().getFirst().matches(
                AstContextType.SCBE_COND, AstContextType.SCBE_OPER);
        boolean condition = context.children().size() == 1 && context.children().getFirst().matches(AstContextType.SCBE_COND);
        List<LogicLabel> targets = new ArrayList<>();
        List<Condition> conditions = new ArrayList<>();
        Set<LogicArgument> arguments = new HashSet<>();

        JumpInstruction lastJump = null;
        LogicLabel trueLabel =  conditionInstructions.getLast() instanceof LabelInstruction lastLabel ? lastLabel.getLabel() : null;
        boolean mixed = false;
        int jumpCount = 0;
        int opCount = 0;
        boolean onlyEquals;
        Operation operation;

        for (LogicInstruction ix : conditionInstructions) {
            if (ix instanceof JumpInstruction jump) {
                lastJump = jump;
                if (jump.isUnconditional()) {
                    mixed = true;
                } else {
                    targets.add(jump.getTarget());
                    jumpCount++;
                    conditions.add(jump.getCondition());
                }
            } else if (ix.affectsControlFlow() && !(ix instanceof LabelInstruction)) {
                mixed = true;
            } else if (ix.isReal() && jumpCount > 0) {
                opCount++;
                ix.processAllModifications(arguments::add);
            }
        }

        // Non-standard condition
        if (lastJump == null || lastJump.isUnconditional() || !shortCircuit && conditionInstructions.getLast() != lastJump) {
            return null;
        }

        if (mixed || targets.getLast().equals(trueLabel)) {
            // If the last jump is to the true label, we don't understand the condition
            operation = null;
            onlyEquals = false;
        } else {
            // All other jump targets must be the same
            // If they lead to the true label, it is OR, otherwise it is AND
            targets.removeLast();
            SequencedSet<LogicLabel> targetSet = new LinkedHashSet<>(targets);
            operation = targetSet.size() != 1 || getLabelInstruction(targetSet.getFirst()).getAstContext() == conditionInstructions.getAstContext()
                    ? null : targetSet.getFirst().equals(trueLabel) ? Operation.LOGICAL_OR : Operation.LOGICAL_AND;

            Condition lastCondition = conditions.removeLast();
            SequencedSet<Condition> conditionSet = new LinkedHashSet<>(conditions);
            if (conditionSet.size() != 1 || lastCondition != Condition.EQUAL) {
                onlyEquals = false;
            } else {
                onlyEquals = conditionSet.getFirst().equals(operation == Operation.LOGICAL_AND ? Condition.EQUAL : Condition.NOT_EQUAL);
            }
        }

        boolean sideEffects = optimizationContext.getProgram().stream().anyMatch(ix ->
                !ix.belongsTo(conditionInstructions.getAstContext()) &&
                        (ix.getAllReads().anyMatch(arguments::contains) || ix.getSideEffects().hasWrites()));

        return new ShortCircuitAnalysis(shortCircuit, condition, lastJump, sideEffects, operation, onlyEquals, jumpCount, opCount);
    }

    protected double executionSteps(AstContext astContext) {
        return executionSteps(contextInstructions(astContext));
    }

    protected double executionSteps(LogicList instructions) {
        Map<LogicLabel, MutableDouble> targetWeights = new HashMap<>();
        double weight = 1.0;
        double total = 0;

        for (LogicInstruction instruction : instructions) {
            total += instruction.getRealSize(null) * weight;
            switch (instruction) {
                case JumpInstruction jump -> {
                    targetWeights.computeIfAbsent(jump.getTarget(), _ -> MutableDouble.zero())
                            .add(jump.isConditional() ? weight / 2.0 : weight);
                    weight = jump.isConditional() ? weight / 2.0 : 0.0;
                }
                case LabelInstruction label -> {
                    MutableDouble labelWeight = targetWeights.remove(label.getLabel());
                    if (labelWeight != null) {
                        weight += labelWeight.get();
                    }
                }
                default -> {}
            }
        }

        return total;
    }

    protected record ShortCircuitAnalysis(
            boolean shortCircuit,               // This is a short-circuit condition
            boolean condition,                  // This is a short-circuited condition (not an operation)
            JumpInstruction lastJump,           // Last jump in the condition
            boolean sideEffects,                // Short-circuited code has side effects
            Operation operation,                // Expression type of the condition
            boolean onlyEquals,                 // All conditional relations are equal or notEqual
            int jumpCount,                      // Number of jumps in total
            int shortedOpCount) {               // Number of short-circuited operations

        public boolean canPrecomputeTrueBranch() {
            return !shortCircuit || operation == Operation.LOGICAL_OR;
        }

        public boolean canPrecomputeFalseBranch() {
            return !shortCircuit || operation == Operation.LOGICAL_AND;
        }
    }
}
