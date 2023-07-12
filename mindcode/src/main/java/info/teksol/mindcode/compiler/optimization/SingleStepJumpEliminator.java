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
    protected boolean optimizeProgram(OptimizationPhase phase, int pass, int iteration) {
        List<JumpInstruction> removableJumps = new ArrayList<>();

        try (LogicIterator iterator = createIterator()) {
            JumpInstruction lastJump = null;
            LogicLabel targetLabel = null;
            boolean isJumpToNext = false;
            boolean wasGotoLabel = false;

            while (iterator.hasNext()) {
                LogicInstruction ix = iterator.next();

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
}
