package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.JumpInstruction;
import info.teksol.mc.mindcode.logic.instructions.LabelInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.OpInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.util.MutableDouble;
import org.jspecify.annotations.Nullable;

import java.util.*;

public abstract class AbstractConditionalOptimizer extends BaseOptimizer {
    public AbstractConditionalOptimizer(Optimization optimization, OptimizationContext optimizationContext) {
        super(optimization, optimizationContext);
    }

    protected boolean removeUnreachableCode(AstContext condition) {
        Set<LogicLabel> activeLabels = new HashSet<>();
        boolean reachable = true;
        boolean modified = false;

        try (OptimizationContext.LogicIterator it = createIteratorForContext(condition)) {
            while (it.hasNext()) {
                LogicInstruction ix = it.next();
                if (ix.getAstContext() == condition) {
                    if (reachable) {
                        if (ix instanceof JumpInstruction j) {
                            activeLabels.add(j.getTarget());
                            // Unconditional jump makes code unreachable
                            reachable = j.isConditional();
                        } else if (ix instanceof LabelInstruction l && !activeLabels.contains(l.getLabel())) {
                            it.remove();
                            modified = true;
                        } else if (ix.getOpcode() == Opcode.EMPTY) {
                            it.remove();
                        }
                    } else if (ix instanceof LabelInstruction l && activeLabels.contains(l.getLabel())) {
                        // An active label makes code reachable
                        reachable = true;
                    } else {
                        // Removing all unreachable instructions
                        it.remove();
                        modified = true;
                    }
                }
            }
        }

        return modified;
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

    protected @Nullable ShortCircuitAnalysis analyzeShortCircuit(LogicList conditionInstructions, boolean analyzeJumps) {
        AstContext context = conditionInstructions.getExistingAstContext();
        boolean shortCircuit = optimizationContext.isShortCircuitCondition(context);
        boolean condition = context.children().size() == 1 && context.children().getFirst().matches(AstContextType.SCBE_COND);
        List<LogicLabel> targets = new ArrayList<>();
        List<Condition> conditions = new ArrayList<>();
        List<LogicInstruction> uncertain = new ArrayList<>();
        List<LogicInstruction> controls = new ArrayList<>();
        List<Jump> jumps = List.of();

        LogicLabel lastFullLabel = null;
        JumpInstruction lastJump = null;
        LogicLabel trueLabel =  conditionInstructions.getLast() instanceof LabelInstruction lastLabel ? lastLabel.getLabel() : null;
        boolean plainOperands = true;
        boolean normal = true;
        boolean mixed = false;
        int jumpCount = 0;
        int opCount = 0;
        boolean onlyEquals;
        Operation operation;

        for (LogicInstruction ix : conditionInstructions) {
            if (ix instanceof LabelInstruction labelIx) {
                if (labelIx.getLabel().equals(lastFullLabel)) {
                    lastFullLabel = null;
                }
                if (targets.contains(labelIx.getLabel())) {
                    controls.add(labelIx);
                    // The last full jump jumps over some active code, bail out
                    if (lastFullLabel != null) mixed = true;
                }
            } else if (lastFullLabel == null) {
                if (ix instanceof JumpInstruction jump) {
                    if (jump.isUnconditional()) {
                        normal = false;
                        mixed = true;
                        lastFullLabel = jump.getTarget();
                    } else {
                        lastJump = jump;
                        controls.add(jump);
                        targets.add(jump.getTarget());
                        jumpCount++;
                        conditions.add(jump.getCondition());
                        if (!jump.isPlainComparison()) {
                            plainOperands = false;
                        }
                    }
                } else if (ix.affectsControlFlow()) {
                    mixed = true;
                } else if (ix.isReal() && jumpCount > 0) {
                    opCount++;
                    uncertain.add(ix);
                }
            }
        }

        // Non-standard condition
        if (lastFullLabel != null || lastJump == null || lastJump.isUnconditional()) {
            return null;
        }

        if (mixed || targets.getLast().equals(trueLabel)) {
            // If the last jump is to the true label, we don't understand the condition
            operation = null;
            onlyEquals = false;
        } else {
            // All other jump targets must be the same
            // If they lead to the true label, it is OR, otherwise it is AND
            LogicLabel falseLabel = targets.removeLast();
            SequencedSet<LogicLabel> targetSet = new LinkedHashSet<>(targets);
            operation = !normal || targetSet.size() != 1 || getLabelInstruction(targetSet.getFirst()).getAstContext() == conditionInstructions.getAstContext()
                    ? null : targetSet.getFirst().equals(trueLabel) ? Operation.LOGICAL_OR : Operation.LOGICAL_AND;

            Condition lastCondition = conditions.removeLast();
            SequencedSet<Condition> conditionSet = new LinkedHashSet<>(conditions);
            if (conditionSet.size() != 1 || lastCondition != Condition.EQUAL) {
                onlyEquals = false;
            } else {
                onlyEquals = conditionSet.getFirst().equals(operation == Operation.LOGICAL_AND ? Condition.EQUAL : Condition.NOT_EQUAL);
            }

            jumps = analyzeJumps ? analyzeJumps(controls, trueLabel, falseLabel) : List.of();
        }

        List<LogicInstruction> sideEffects = uncertain.stream()
                .filter(ix -> !ix.isSafe() || ix.getAllWrites().anyMatch(arg -> outsideUse(context, arg)))
                .toList();

        if (false) {
            sideEffects.forEach(ix -> {
                System.out.println(ix + ":");
                System.out.println("    safe: " + ix.isSafe());
                System.out.println("    hasWrites: " + ix.getSideEffects().hasWrites());
                System.out.println("    outsideUse: " + ix.getAllWrites().anyMatch(arg -> outsideUse(context, arg)));
                System.out.println();
            });
        }
        return new ShortCircuitAnalysis(shortCircuit, normal, condition, lastJump, !sideEffects.isEmpty(), operation,
                onlyEquals, plainOperands, jumpCount, opCount, List.copyOf(jumps));
    }

    private boolean outsideUse(AstContext localContext, LogicArgument argument) {
        return argument instanceof LogicVariable variable &&
                optimizationContext.getVariableReferences(variable).stream().anyMatch(ix -> !ix.belongsTo(localContext));
    }

    private List<Jump> analyzeJumps(List<LogicInstruction> controls, LogicLabel trueLabel, LogicLabel falseLabel) {
        // Normalize
        if (controls.getLast() instanceof LabelInstruction ix && ix.getLabel().equals(trueLabel)) {
            controls.removeLast();
        }

        LogicLabel nextLabel;
        if (controls.size() == 4 && controls.get(2) instanceof LabelInstruction ix) {
            nextLabel = ix.getLabel();
            controls.remove(2);
        } else {
            nextLabel = LogicLabel.INVALID;
        }

        // We're okay here
        if (controls.size() != 3 || !controls.stream().allMatch(ix -> ix instanceof JumpInstruction j && j.isPlainComparison())) {
            return List.of();
        }

        // So, we have three jumps and their targets
        return controls.stream()
                .map(JumpInstruction.class::cast)
                .map(jump -> new Jump(jump, findJumpTarget(jump.getTarget(), trueLabel, falseLabel, nextLabel)))
                .toList();
    }

    private JumpTarget findJumpTarget(LogicLabel target, LogicLabel trueLabel, LogicLabel falseLabel, LogicLabel nextLabel) {
        if (target.equals(trueLabel)) return JumpTarget.TRUE;
        if (target.equals(falseLabel)) return JumpTarget.FALSE;
        if (target.equals(nextLabel)) return JumpTarget.NEXT;
        return JumpTarget.INVALID;
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
            boolean normal,                     // This is a normalized condition
            boolean condition,                  // This is a short-circuited condition (not an operation)
            JumpInstruction lastJump,           // Last jump in the condition
            boolean sideEffects,                // Short-circuited code has side effects
            Operation operation,                // Expression type of the condition
            boolean onlyEquals,                 // All conditional relations are equal or notEqual
            boolean plainOperands,              // All operands are plain variables or constants
            int jumpCount,                      // Number of jumps in total
            int shortedOpCount,                 // Number of short-circuited operations
            List<Jump> jumps) {                 // Analyzed jumps

        public boolean canPrecomputeTrueBranch() {
            return !shortCircuit || operation == Operation.LOGICAL_OR;
        }

        public boolean canPrecomputeFalseBranch() {
            return !shortCircuit || operation == Operation.LOGICAL_AND;
        }
    }

    protected enum JumpTarget {
        TRUE, FALSE, NEXT, INVALID
    }

    protected record Jump(
            Condition condition,
            LogicValue x,
            LogicValue y,
            JumpTarget target) {

        private Jump(JumpInstruction jump, JumpTarget target) {
            this(jump.getCondition(), jump.getX(), jump.getY(), target);
            if (!jump.isPlainComparison()) {
                throw new IllegalArgumentException("Jump condition is not a plain comparison");
            }
        }

        public LogicValue variable() {
            return x == LogicBoolean.FALSE ? y : x;
        }

        public boolean comparesToTrue() {
            // We already know it's a plain comparison jump
            return condition == Condition.NOT_EQUAL;
        }

        public boolean comparesToFalse() {
            // We already know it's a plain comparison jump
            return condition == Condition.EQUAL;
        }
    }
}
