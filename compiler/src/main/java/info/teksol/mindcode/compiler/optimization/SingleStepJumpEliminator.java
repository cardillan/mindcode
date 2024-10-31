package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mindcode.logic.LogicLabel;

import java.util.ArrayList;
import java.util.List;

class SingleStepJumpEliminator extends BaseOptimizer {

    public SingleStepJumpEliminator(OptimizationContext optimizationContext) {
        super(Optimization.SINGLE_STEP_JUMP_ELIMINATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        List<LogicInstruction> removableJumps = new ArrayList<>();

        try (LogicIterator iterator = createIterator()) {
            JumpInstruction lastJump = null;
            LogicLabel targetLabel = null;
            boolean isJumpToNext = false;
            boolean wasGotoLabel = false;
            boolean wasLabel = false;
            boolean wasJump = false;

            while (iterator.hasNext()) {
                LogicInstruction ix = iterator.next();

                if (ix instanceof LabeledInstruction il) {
                    isJumpToNext |= il.getLabel().equals(targetLabel);
                    wasGotoLabel |= il instanceof GotoLabelInstruction;
                    if (wasGotoLabel || optimizationContext.isActive(il.getLabel())) wasLabel = true;
                } else if (!(ix instanceof NoOpInstruction)) {
                    if (isJumpToNext) {
                        if (wasJump) {
                            removableJumps.add(lastJump);
                        }
                        isJumpToNext = false;
                    } else if (wasJump && ix instanceof JumpInstruction jump && jump.equals(lastJump) && !wasGotoLabel) {
                        removableJumps.add(lastJump);
                    }

                    if (ix instanceof JumpInstruction jump) {
                        if (experimental() && !wasLabel && jump.equals(lastJump)) {
                            removableJumps.add(jump);
                        } else {
                            lastJump = jump;
                            targetLabel = jump.getTarget();
                        }
                        wasJump = true;
                        wasLabel = false;
                    } else {
                        targetLabel = null;
                        wasJump = false;
                    }

                    wasGotoLabel = false;
                }
            }
        }

        removableJumps.forEach(this::invalidateInstruction);

        if (phase == OptimizationPhase.FINAL && advanced()) {
            int index = optimizationContext.getProgram().size() - 1;
            while (index >= 0) {
                LogicInstruction lastIx = optimizationContext.getProgram().get(index);
                if (isJumpToStart(lastIx)) {
                    optimizationContext.removeInstruction(index);
                } else if (lastIx.getRealSize() > 0) {
                    break;
                }
                index--;
            }
        }

        return wasUpdated();
    }

    private boolean isJumpToStart(LogicInstruction instruction) {
        if (instruction instanceof JumpInstruction jump && jump.isUnconditional()) {
            int firstIndex = optimizationContext.firstInstructionIndex(ix -> !(ix instanceof LabeledInstruction));
            int targetIndex = optimizationContext.getLabelInstructionIndex(jump.getTarget());
            return targetIndex <= firstIndex;
        }

        return instruction instanceof EndInstruction;
    }
}
