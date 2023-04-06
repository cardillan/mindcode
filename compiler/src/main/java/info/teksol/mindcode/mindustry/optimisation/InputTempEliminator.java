package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;

import java.util.Iterator;
import java.util.List;

/**
 * Generic optimizer to remove all assignments to temporary variables that are only used as parameters
 * in subsequent instruction. The {@code set} instruction is removed, while the other instruction is updated
 * to replace the temp variable with the value used in the {@code set} instruction.
 * The optimization is performed only when the following conditions are met:
 * <ol>
 * <li>The set instruction assigns to a {@code __tmp} variable.</li>
 * <li>The {@code __tmp} variable is used in exactly one other instruction, which follows the {@code set} instruction
 * (the check is based on absolute instruction sequence in the program, not on the actual program flow).</li>
 * <li>All arguments of the other instruction referencing the {@code __tmp} variable are input ones.</li>
 * </ol>
 * Push and pop instructions are ignored by this optimizer. Push/pop instructions of any eliminated variables
 * are removed by the StackUsageOptimizer later on.
 */
class InputTempEliminator extends GlobalOptimizer {
    public InputTempEliminator(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected boolean optimizeProgram() {
        // Cannot use iterations due to modifications of the underlying list in the loop
        for (Iterator<LogicInstruction> it = program.iterator(); it.hasNext(); ) {
            LogicInstruction instruction = it.next();
            if (!instruction.isSet()) continue;

            String arg0 = instruction.getArgs().get(0);
            // Not an assignment to a temp variable
            if (!isTemporary(arg0)) continue;

            List<LogicInstruction> list = findInstructions(ix -> ix.getArgs().contains(arg0) && !ix.isPushOrPop());
            // Not exactly two instructions, or this instruction does not come first
            if (list.size() != 2 || list.get(0) != instruction) continue;

            // Make sure all arg0 arguments of the other instruction are input
            LogicInstruction other = list.get(1);
            boolean replacesInputArg = instructionProcessor.getTypedArguments(other)
                    .filter(t -> t.getValue().equals(arg0))
                    .allMatch(t -> t.getArgumentType().isInput());
            if (!replacesInputArg) continue;

            // The first instruction merely transfers a value to the input argument of the other instruction
            // Replacing instruction argument by value
            String arg1 = instruction.getArgs().get(1);
            replaceInstruction(other, replaceAllArgs(other, arg0, arg1));
            it.remove();
        }

        return false;
    }
}
