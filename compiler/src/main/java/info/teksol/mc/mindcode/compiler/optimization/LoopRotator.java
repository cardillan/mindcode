package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.arguments.Condition;
import info.teksol.mc.mindcode.logic.arguments.LogicBoolean;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.JumpInstruction;
import info.teksol.mc.mindcode.logic.instructions.LabelInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static info.teksol.mc.mindcode.compiler.astcontext.AstContextType.EACH;
import static info.teksol.mc.mindcode.compiler.astcontext.AstContextType.LOOP;
import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;

/// The loop rotator improves loops with the condition at the beginning by performing these modifications:
/// - Replacing the closing unconditional jump to the loop condition with a jump based on the inverse of the loop
///   condition leading to the loop body. This avoids the execution of the unconditional jump. If the loop condition
///   evaluates through additional instructions apart from the jump, these will be copied to the end of the loop too
///   (assuming 'speed' optimization)
/// - If the previous modification was done, the loop condition consists of the jump only, and it can be determined
///   that the loop condition holds before the first iteration of the loop, the conditional jump at the beginning
///   of the loop is removed.
///
/// If the opening jump has a form of op followed by negation jump, the condition is still replicated at the end
/// of the body as a jump having the op condition. In this case, execution of two instructions per loop is avoided.
@NullMarked
class LoopRotator extends AbstractConditionalOptimizer {
    public LoopRotator(OptimizationContext optimizationContext) {
        super(Optimization.LOOP_ROTATION, optimizationContext);
    }

    private int fullRotations = 0;
    private int partialRotations = 0;

    @Override
    public void generateFinalMessages() {
        super.generateFinalMessages();
        outputActions("%d loop conditions were fully rotated.", fullRotations);
        outputActions("%d loop conditions were partially rotated.", partialRotations);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        forEachContext(c -> c.matches(LOOP, EACH) && c.matches(BASIC), loop -> {
            processLoop(loop, _ -> {}, true, 0);
            return null;
        });
        return false;
    }

    @Override
    public List<OptimizationAction> getPossibleOptimizations(int costLimit) {
        List<OptimizationAction> actions = new ArrayList<>();
        forEachContext(c -> c.matches(LOOP, EACH) && c.matches(BASIC), loop -> {
            processLoop(loop, actions::add, false, costLimit);
            return null;
        });
        return actions;
    }

    private void processLoop(AstContext loop, Consumer<OptimizationAction> actions, boolean optimize, int costLimit) {
        List<AstContext> conditions = loop.findSubcontexts(CONDITION);
        if (conditions.size() != 1) return;

        for (AstContext conditionContext : conditions) {
            if (optimizationContext.isShortCircuitCondition(conditionContext)) {
                // Help the optimizer a bit by removing the unreachable code within the short-circuited context
                while (removeUnreachableCode(conditionContext.children().getFirst())) ;
            }
        }

        List<AstContext> bodies = loop.findSubcontexts(BODY);
        if (bodies.size() != 1 || contextInstructions(bodies.getFirst()).realSize() == 0) return;

        AstContext conditionContext = conditions.getFirst();
        LogicList condition = contextInstructions(conditionContext);
        LogicList next = contextInstructions(loop.findSubcontext(FLOW_CONTROL));
        if (condition.isEmpty() || next.isEmpty() || !hasConditionAtFront(loop)) {
            return;
        }

        ShortCircuitAnalysis analysis = analyzeShortCircuit(condition, false);
        if (analysis == null) return;

        JumpInstruction lastJump = analysis.lastJump();

        if (condition.getFirst() instanceof LabelInstruction
                && next.getLast() instanceof LabelInstruction exitLabelIx
                && lastJump.getTarget().equals(exitLabelIx.getLabel())
                && next.getFromEnd(1) instanceof JumpInstruction backJump
                && backJump.isUnconditional()) {

            LogicLabel exitLabel = exitLabelIx.getLabel();

            LoopRotationAction partial = analysis.normal()
                    ? createOptimizationAction(loop, condition, lastJump, backJump, exitLabel, costLimit, false, false)
                    : null;

            boolean hoisting = !optimizationContext.getLoopInvariants(loop).isEmpty();
            boolean canRemoveFront = analysis.normal() && !analysis.sideEffects() &&
                    optimizationContext.evaluateLoopEntryCondition(conditionContext) == LogicBoolean.TRUE;

            LoopRotationAction full = partial == null || !partial.fullRotation && hoisting
                    ? createOptimizationAction(loop, condition, lastJump, backJump, exitLabel, costLimit, true, canRemoveFront)
                    : null;

            LoopRotationAction optimal = partial == null ? full
                    : full == null ? partial
                    : partial.isStrictlyBetterThan(full) ? partial
                    : full.isStrictlyBetterThan(partial) ? full
                    : null;

            if (optimize && optimal != null && optimal.totalImprovement()) {
                optimal.rotateCondition();
                return;
            }

            if (partial != null) actions.accept(partial);
            if (full != null) actions.accept(full);
        }
    }

    private boolean hasConditionAtFront(AstContext loop) {
        return loop.children().stream()
                .filter(c -> c.matches(CONDITION, BODY))
                .findFirst()
                .filter(c -> c.matches(CONDITION))
                .isPresent();
    }

    private @Nullable LoopRotationAction createOptimizationAction(AstContext loop, LogicList condition,
            JumpInstruction lastJump, JumpInstruction backJump, LogicLabel exitLabel, int costLimit,
            boolean fullRotation, boolean canRemoveFront) {
        AstContext lastJumpContext = lastJump.getAstContext();
        LogicLabel enterLabel = condition.getLast() instanceof LabelInstruction l ? l.getLabel() : null;
        boolean effectivelyFull = true;

        int size = 0;
        int jumps = 0;
        double additionalSteps = 0;
        boolean finalConversion = false;
        for (LogicInstruction ix : condition) {
            if (ix.getAstContext() == lastJumpContext && ix instanceof JumpInstruction jump) {
                boolean hasInverse = jump.getCondition().hasInverse(getGlobalProfile());
                if (!jump.getTarget().equals(enterLabel) && jump != lastJump) {
                    effectivelyFull = false;
                }
                if (jump.getTarget().equals(exitLabel)) {
                    if (!hasInverse) {
                        size++;
                        additionalSteps += 1.0 / (double)(1 << jumps);
                    }

                    // When not performing a full rotation, stop at the first exit jump
                    if (!fullRotation) {
                        finalConversion = hasInverse;
                        size++; // Cost of this jump
                        break;
                    }
                }
                jumps++;
            }
            size += ix.getRealSize();
        }

        // Inverting the condition wouldn't save execution time
        if (!fullRotation && !finalConversion && jumps == 0) return null;

        // If effectivelyFull is true, each jump leads to the beginning of the loop
        if (effectivelyFull && !fullRotation) {
            jumps++;
            fullRotation = true;
        }

        int cost;
        double benefit;
        if (fullRotation && !canRemoveFront) {
            cost = size - 1;
            double originalWeight = optimizationContext.getLoopInvariants(loop).stream()
                    .mapToDouble(ix -> ix.getRealSize() * ix.getAstContext().totalWeight()).sum();
            double hoistedWeight = loop.totalWeight() * optimizationContext.getLoopInvariants(loop).stream()
                    .mapToInt(LogicInstruction::getRealSize).sum();
            benefit = loop.totalWeight() + (1 - additionalSteps) * backJump.getAstContext().totalWeight()
                    + originalWeight - hoistedWeight;
        } else {
            cost = size - (fullRotation ? jumps : 0) - 1;
            benefit = (fullRotation ? loop.totalWeight() : 0.0) + (1 - additionalSteps) * backJump.getAstContext().totalWeight();
        }

        // Keeps condition instructions without the label

        final LogicList conditionEvaluation = condition.subList(1, condition.size());
        return cost <= costLimit && benefit >= 0.0
                ? new LoopRotationAction(loop, conditionEvaluation, lastJump, backJump, fullRotation, canRemoveFront, cost, benefit)
                : null;
    }

    private class LoopRotationAction extends AbstractOptimizationAction {
        private final JumpInstruction lastJump;
        private final JumpInstruction backJump;
        private final LogicList conditionEvaluation;
        private final boolean fullRotation;
        private final boolean canRemoveFront;

        public LoopRotationAction(AstContext loop, LogicList conditionEvaluation, JumpInstruction lastJump,
                JumpInstruction backJump, boolean fullRotation, boolean canRemoveFront, int cost, double benefit) {
            super(loop, cost, benefit);
            this.conditionEvaluation = conditionEvaluation;
            this.lastJump = lastJump;
            this.backJump = backJump;
            this.fullRotation = fullRotation;
            this.canRemoveFront = canRemoveFront;
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            return applyOptimization(this::rotateCondition, toString());
        }

        private OptimizationResult rotateCondition() {
            boolean hoistingRotation = fullRotation && !canRemoveFront;
            LogicLabel exitLabel = lastJump.getTarget();
            LogicLabel repeatLabel = null;
            LogicLabel enterLabel = null;

            LogicList copy = conditionEvaluation.duplicate(true);
            // Invert the label map (new -> old)
            Map<LogicLabel, LogicLabel> labelMap = CollectionUtils.invert(copy.getLabelMap());

            if (hoistingRotation) {
                // The front condition is evaluated just once
                conditionEvaluation.getExistingAstContext().updateWeight(1.0);

                // We need to create a new label at the start of the body
                // to separate the body from a loop hoisting context
                repeatLabel = instructionProcessor.nextLabel();
                AstContext body = Objects.requireNonNull(astContext.findSubcontext(BODY));
                int index = optimizationContext.firstInstructionIndex(body);
                optimizationContext.insertInstruction(index,
                        createLabel(astContext.createSubcontext(HOIST, 1.0), repeatLabel));
                labelMap.put(backJump.getTarget(), repeatLabel);

                enterLabel = copy.getLast() instanceof LabelInstruction l ? l.getLabel() : null;
            }

            LogicInstruction last = copy.last(ix -> ix instanceof JumpInstruction j && j.isConditional());
            if (!(last instanceof JumpInstruction lastJumpCopy) || !lastJumpCopy.getTarget().equals(exitLabel)) {
                throw new MindcodeInternalError("Unexpected condition structure");
            }
            AstContext conditionContext = lastJumpCopy.getAstContext();

            int index = 0;
            int frontIndex = instructionIndex(conditionEvaluation.getExisting(0));
            while (index < copy.size()) {
                LogicInstruction ix = copy.getExisting(index);
                if (ix.getAstContext() == conditionContext && ix instanceof JumpInstruction j) {
                    if (j.getTarget().equals(exitLabel)) {
                        if (hoistingRotation) {
                            if (j.getCondition().hasInverse(getGlobalProfile())) {
                                copy.replaceKeepingContext(index, j.forceInvert().withTarget(repeatLabel));
                            } else {
                                LogicVariable tmp = instructionProcessor.nextTemp();
                                copy.addKeepingContext(index++, createOp(j.getAstContext(), j.getCondition().toOperation(),
                                        tmp, j.getX(), j.getY()));
                                copy.replaceKeepingContext(index, createJump(j.getAstContext(), repeatLabel,
                                        Condition.EQUAL, tmp, LogicBoolean.FALSE));
                            }
                        } else {
                            LogicLabel label = instructionProcessor.nextLabel();
                            if (j.getCondition().hasInverse(getGlobalProfile())) {
                                frontIndex++;
                                copy.replaceKeepingContext(index, j.forceInvert().withTarget(label));
                            } else if (fullRotation || j == lastJumpCopy) {
                                LogicVariable tmp = instructionProcessor.nextTemp();
                                copy.addKeepingContext(index++, createOp(j.getAstContext(), j.getCondition().toOperation(),
                                        tmp, j.getX(), j.getY()));
                                copy.replaceKeepingContext(index, createJump(j.getAstContext(), label,
                                        Condition.EQUAL, tmp, LogicBoolean.FALSE));
                            } else {
                                copy.replaceKeepingContext(index, createJumpUnconditional(j.getAstContext(), label));
                            }
                            insertInstruction(frontIndex, createLabel(lastJump.getAstContext(), label));
                            if (!fullRotation) {
                                index++;
                                break;
                            }
                        }
                    } else if (hoistingRotation && j.getTarget().equals(enterLabel)) {
                        copy.replaceKeepingContext(index, j.withTarget(repeatLabel));
                    } else if (!hoistingRotation) {
                        // Restore the original label
                        LogicLabel target = labelMap.getOrDefault(j.getTarget(), j.getTarget());
                        copy.replaceKeepingContext(index, j.withTarget(target));
                    }
                }

                index++;
                frontIndex++;
            }

            // Remove all after the index
            while (copy.size() > index) {
                copy.remove(index);
            }

            // Replace the last jump first, then insert the rest of the code *before* it
            int targetIndex = instructionIndex(backJump);
            removeInstruction(targetIndex);
            insertInstructions(targetIndex, copy);

            if (fullRotation && canRemoveFront) {
                optimizationContext.removeMatchingInstructions(ix -> ix instanceof JumpInstruction
                        && ix.getAstContext() == lastJump.getAstContext());
                fullRotations++;
            } else {
                partialRotations++;
            }

            return OptimizationResult.REALIZED;
        }

        @Override
        public String toString() {
            assert astContext.node() != null;
            return (fullRotation ? "Full" : "Partial") + " loop rotation at " + astContext.node().sourcePosition().formatForLog();
        }
    }
}
