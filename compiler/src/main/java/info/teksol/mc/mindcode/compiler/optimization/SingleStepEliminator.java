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
            // Reset when it is determined lastJump cannot target the next instruction.
            LogicLabel targetLabel = null;

            // Indicates the last jump was determined to target the next effective instruction. Set to true when
            //  the targetLabel is encountered.
            boolean isJumpToNext = false;

            // There were no effective instructions since lastJump (active labels ignored)
            boolean noEffective = false;

            // The last jump is eligible for sequential remove: there were no flow control instructions
            // since lastJump and no instructions modifying lastJump's condition
            boolean sequential = false;

            while (iterator.hasNext()) {
                LogicInstruction ix = iterator.next();

                if (ix instanceof MultiLabelInstruction) {
                    sequential = false;

                    // MultiLabel instruction may be a part of jump tables that must not be affected
                     if (ix.isFixedMultilabel()) lastJump = null;
                } else if (ix instanceof LabeledInstruction il) {
                    if (il.getLabel().equals(targetLabel)) {
                        // lastJump is targeting the next executable instruction
                        isJumpToNext = true;
                    }
                    if (optimizationContext.isActive(il.getLabel())) {
                        // An active label breaks the sequential flow
                        sequential = false;
                    }
                } else if (!(ix instanceof EmptyInstruction)) {
                    if (isJumpToNext && lastJump != null) {
                        // Removing jump targeting the next instruction
                        removableJumps.add(lastJump);
                        lastJump = null;
                    }

                    isJumpToNext = false;
                    if (ix instanceof JumpInstruction jump) {
                        if (lastJump != null && supersedes(lastJump, jump)) {
                            if (noEffective) {
                                // Removing a previous jump when the next jump supersedes it, and there are no effective instructions in between
                                removableJumps.add(lastJump);

                                // This becomes the last jump, other properties remain unchanged
                                lastJump = jump;
                                continue;
                            } else if (sequential && equal(lastJump, jump)) {
                                // Removing jump identical to the previous one with effective instructions between them,
                                // but these instructions are safe.
                                // Keeping lastJump intact
                                removableJumps.add(jump);
                                continue;
                            }
                        }

                        // We got a new jump, remember it
                        lastJump = jump;
                        targetLabel = jump.getTarget();
                        noEffective = true;
                        sequential = jump.isUnconditional() || !jump.getX().isVolatile() && !jump.getY().isVolatile();
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

        if (phase.breaksContextStructure()) {
            int index = optimizationContext.getProgram().size() - 1;
            while (index >= 0) {
                LogicInstruction lastIx = optimizationContext.getProgram().get(index);
                if (isJumpToStart(lastIx) && (!activeLabel(index) || emptyLoop(lastIx))) {
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

    private boolean activeLabel(int index) {
        while (index > 0) {
            LogicInstruction ix = optimizationContext.getProgram().get(--index);
            if (ix.getRealSize() != 0) return false;
            if (ix instanceof LabeledInstruction l && optimizationContext.isActive(l.getLabel())) return true;
        }
        return true;
    }

    private boolean emptyLoop(LogicInstruction instruction) {
        if (instruction instanceof JumpInstruction jump) {
            int targetIndex = optimizationContext.getLabelInstructionIndex(jump.getTarget());
            List<LogicInstruction> program = optimizationContext.getProgram();
            while (targetIndex < program.size()) {
                if (program.get(targetIndex) == jump) return true;
                if (program.get(targetIndex).getRealSize() != 0) return false;
                targetIndex++;
            }
        }
        return false;
    }

    private boolean equal(JumpInstruction jump1, JumpInstruction jump2) {
        return equalConditions(jump1, jump2) && equalTargets(jump1, jump2);
    }

    private boolean equalConditions(JumpInstruction jump1, JumpInstruction jump2) {
        return jump1.getCondition() == jump2.getCondition() &&
                (jump1.isUnconditional() || jump1.getX().equals(jump2.getX()) && jump1.getY().equals(jump2.getY()));
    }

    private boolean equalTargets(JumpInstruction jump1, JumpInstruction jump2) {
        return jump1.getTarget().equals(jump2.getTarget())
                || labeledInstructionIndex(jump1.getTarget()) == labeledInstructionIndex(jump2.getTarget());
    }

    private boolean supersedes(JumpInstruction lastJump, JumpInstruction jump) {
        return equalTargets(lastJump, jump) && (jump.isUnconditional() || equalConditions(lastJump, jump));
    }
}
