package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.instructions.LabelInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import org.jspecify.annotations.NullMarked;

import java.util.BitSet;
import java.util.List;

/// This optimizer removes instructions that are unreachable. There are several ways unreachable instructions might
/// appear:
/// - Jump target propagation can get unreachable jumps that are no longer targeted
/// - User-created unreachable regions, such as `while false ... end`
/// - User defined functions which are called from an unreachable region
@NullMarked
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
            if (unused.get(index)) {
                removeInstruction(index);
            }
        }

        return false;
    }

    int findLabelIndex(LogicLabel label) {
        return firstInstructionIndex(ix -> ix instanceof LabelInstruction l && l.getLabel().equals(label));
    }
}
