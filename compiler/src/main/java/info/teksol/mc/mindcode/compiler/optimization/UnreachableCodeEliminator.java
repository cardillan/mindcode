package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.instructions.EndInstruction;
import info.teksol.mc.mindcode.logic.instructions.LabelInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;

import java.util.BitSet;
import java.util.List;

/**
 * This optimizer removes instructions that are unreachable. There are several ways unreachable instructions might
 * appear:
 * <ol>
 * <li>Jump target propagation can get unreachable jumps that are no longer targeted</li>
 * <li>User-created unreachable regions, such as {@code while false ... end}</li>
 * <li>User defined functions which are called from an unreachable region</li>
 * </ol>
 */
class UnreachableCodeEliminator extends BaseOptimizer {
    public UnreachableCodeEliminator(OptimizationContext optimizationContext) {
        super(Optimization.UNREACHABLE_CODE_ELIMINATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        // List of all instructions
        BitSet unused = optimizationContext.getUnreachableInstructions();

        List<LogicInstruction> program = optimizationContext.getProgram();
        for (int index = program.size() - 1; index >= 0; index--) {
            if (unused.get(index) && (advanced() || !(program.get(index) instanceof EndInstruction))) {
                removeInstruction(index);
            }
        }

        return false;
    }

    int findLabelIndex(LogicLabel label) {
        return firstInstructionIndex(ix -> ix instanceof LabelInstruction l && l.getLabel().equals(label));
    }
}
