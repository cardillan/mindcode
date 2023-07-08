package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.LogicLabel;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.List;
import java.util.Queue;

/**
 * This optimizer removes instructions that are unreachable. There are several ways unreachable instructions might
 * appear:
 * <ol>
 * <li>Jump target propagation can get unreachable jumps that are no longer targeted</li>
 * <li>User-created unreachable regions, such as {@code while false ... end}</li>
 * <li>User defined functions which are called from an unreachable region</li>
 * </ol>
 * The optimizer analyses the control flow of the program. It starts at the first instruction and visits
 * every instruction reachable from there, either by advancing to the next instruction, or by a jump/goto/call.
 * Visited instruction are marked as active and aren't inspected again. When all code paths have reached an
 * end or an already visited instruction, the analysis stops and all unvisited instructions are removed.
 * Only one iteration is performed.
 */
class UnreachableCodeEliminator extends BaseOptimizer {
    public UnreachableCodeEliminator(OptimizationContext optimizationContext) {
        super(Optimization.UNREACHABLE_CODE_ELIMINATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase, int pass, int iteration) {
        // List of all instructions
        List<LogicInstruction> program = optimizationContext.getProgram();
        BitSet unused = new BitSet(program.size());
        // Also serves as a data stop for reaching the end of instruction list naturally.
        unused.set(0, program.size());
        Queue<Integer> heads = new ArrayDeque<>();
        heads.offer(0);

        MainLoop:
        while (!heads.isEmpty()) {
            int index = heads.poll();
            while (unused.get(index)) {
                unused.clear(index);
                switch (program.get(index)) {
                    case EndInstruction end -> {
                        continue MainLoop;
                    }
                    case ReturnInstruction ret -> {
                        continue MainLoop;
                    }
                    case JumpInstruction jump -> {
                        heads.offer(findLabelIndex(jump.getTarget()));
                        if (jump.isUnconditional()) {
                            continue MainLoop;
                        }
                    }
                    case CallInstruction call -> {
                        heads.offer(findLabelIndex(call.getCallAddr()));
                    }
                    case CallRecInstruction call -> {
                        heads.offer(findLabelIndex(call.getCallAddr()));
                        heads.offer(findLabelIndex(call.getRetAddr()));
                        continue MainLoop;
                    }
                    case GotoInstruction gotoIx -> {
                        for (int i = 0; i < program.size(); i++) {
                            if (program.get(i) instanceof GotoLabelInstruction ix && ix.getMarker().equals(gotoIx.getMarker())) {
                                heads.offer(i);
                            }
                        }
                        continue MainLoop;
                    }
                    default -> {}
                }

                index++;
            }
        }

        for (int index = program.size() - 1; index >= 0; index--) {
            if (unused.get(index) && (aggressive() || !(program.get(index) instanceof EndInstruction))) {
                removeInstruction(index);
            }
        }

        return false;
    }

    int findLabelIndex(LogicLabel label) {
        return firstInstructionIndex(ix -> ix instanceof LabelInstruction l && l.getLabel().equals(label));
    }
}
