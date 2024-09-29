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

            while (iterator.hasNext()) {
                LogicInstruction ix = iterator.next();
                if (phase == OptimizationPhase.FINAL && !iterator.hasNext() && advanced() && isJumpToStart(ix)) {
                    removableJumps.add(ix);
                    continue;
                }

                if (ix instanceof LabeledInstruction il) {
                    isJumpToNext |= il.getLabel().equals(targetLabel);
                    wasGotoLabel |= il instanceof GotoLabelInstruction;
                } else if (!(ix instanceof NoOpInstruction)) {
                    if (isJumpToNext) {
                        removableJumps.add(lastJump);
                        isJumpToNext = false;
                    } else if (ix instanceof JumpInstruction jump && jump.equals(lastJump) && !wasGotoLabel) {
                        removableJumps.add(lastJump);
                    }

                    if (ix instanceof JumpInstruction jump) {
                        lastJump = jump;
                        targetLabel = jump.getTarget();
                    } else {
                        lastJump = null;
                        targetLabel = null;
                    }

                    wasGotoLabel = false;
                }
            }
        }

        removableJumps.forEach(this::invalidateInstruction);
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
