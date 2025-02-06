package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
class SingleStepEliminator extends BaseOptimizer {

    public SingleStepEliminator(OptimizationContext optimizationContext) {
        super(Optimization.SINGLE_STEP_ELIMINATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        List<LogicInstruction> removableJumps = new ArrayList<>();

        try (LogicIterator iterator = createIterator()) {
            // Last encountered jump instruction
            // Reset when it is determined the last jump must not be considered
            JumpInstruction lastJump = null;

            // Target of the last encountered jump instruction.
            // Reset when it is determined lastJump cannot target next instruction.
            LogicLabel targetLabel = null;

            // Indicates last jump was determined to target next effective instruction. Set to true when
            // targetLabel is encountered.
            boolean isJumpToNext = false;

            // There were no effective instructions since lastJump (active labels ignored)
            boolean noEffective = false;

            // Last jump is eligible for sequential remove: there were no flow control instructions
            // since lastJump and no instructions modifying lastJump's condition
            boolean sequential = false;

            while (iterator.hasNext()) {
                LogicInstruction ix = iterator.next();

                if (ix instanceof MultiLabelInstruction) {
                    // MultiLabel instruction may be a part of jump tables which must not be affected
                    lastJump = null;
                } else if (ix instanceof LabeledInstruction il) {
                    if (il.getLabel().equals(targetLabel)) {
                        // lastJump is targeting the next executable instruction
                        isJumpToNext = true;
                    }
                    if (optimizationContext.isActive(il.getLabel())) {
                        // An active label breaks sequential flow
                        sequential = false;
                    }
                } else if (!(ix instanceof NoOpInstruction)) {
                    if (isJumpToNext && lastJump != null) {
                        // Removing jump targeting the next instruction
                        removableJumps.add(lastJump);
                        lastJump = null;
                    }

                    isJumpToNext = false;
                    if (ix instanceof JumpInstruction jump) {
                        if (jump.equals(lastJump)) {
                            if (noEffective) {
                                // Removing previous jump when  identical to this one and no effective instructions between them
                                removableJumps.add(lastJump);

                                // This becomes the last jump, other properties remain unchanged
                                lastJump = jump;
                            } else if (sequential) {
                                // Removing jump identical to the previous one with effective instructions between them,
                                // but these instructions are safe.
                                // Keeping lastJump intact
                                removableJumps.add(jump);
                            }
                        } else {
                            // We got a new jump, remember it
                            lastJump = jump;
                            targetLabel = jump.getTarget();
                            noEffective = true;
                            sequential = jump.isUnconditional() || !jump.getX().isVolatile() && !jump.getY().isVolatile();
                        }
                    } else {
                        targetLabel = null;
                        noEffective = false;

                        if (lastJump != null && sequential) {
                            // Update the sequential condition if met
                            if (ix.affectsControlFlow()) {
                                sequential = false;
                            } else if (lastJump.isConditional()) {
                                LogicValue x = lastJump.getX();
                                LogicValue y = lastJump.getY();
                                if (ix.outputArgumentsStream().anyMatch(a -> a.equals(x) || a.equals(y))) {
                                    sequential = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        removableJumps.forEach(this::invalidateInstruction);

        if (phase == OptimizationPhase.FINAL) {
            int index = optimizationContext.getProgram().size() - 1;
            while (index >= 0) {
                LogicInstruction lastIx = optimizationContext.getProgram().get(index);
                if (isJumpToStart(lastIx)) {
                    optimizationContext.removeInstruction(index);
                } else if (lastIx.isReal()) {
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
