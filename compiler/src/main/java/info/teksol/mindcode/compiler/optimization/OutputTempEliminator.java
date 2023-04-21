package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.PushOrPopInstruction;
import info.teksol.mindcode.compiler.instructions.SetInstruction;
import info.teksol.mindcode.logic.ArgumentType;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.ParameterAssignment;

import java.util.List;

/**
 * Generic optimizer to remove all assignments to temporary variables that carry over the output value
 * of the preceding instruction. The {@code set} instruction is removed, while the preceding instruction is updated
 * to replace the temp variable with the target variable used in the {@code set} instruction.
 * The optimization is performed only when the following conditions are met:
 * <ol>
 * <li>The set instruction assigns from a {@code __tmp} variable.</li>
 * <li>The {@code __tmp} variable is used in exactly one other instruction, which immediately precedes
 * the instruction producing the {@code __tmp} variable</li>
 * <li>All arguments of the other instruction referencing the {@code __tmp} variable are output ones.</li>
 * </ol>
 * Push and pop instructions are ignored by this optimizer. Push/pop instructions of any eliminated variables
 * are removed by the StackUsageOptimizer later on.
 */
class OutputTempEliminator extends GlobalOptimizer {
    public OutputTempEliminator(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected boolean optimizeProgram() {
        // Cannot use iterations due to modifications of the underlying list in the loop
        for (int index = 1; index < program.size(); index++)  {
            if (program.get(index) instanceof SetInstruction ix && ix.getValue().getType() == ArgumentType.TMP_VARIABLE) {
                LogicArgument value = ix.getValue();
                List<LogicInstruction> list = findInstructions(
                        in -> in.getArgs().contains(value) && !(in instanceof PushOrPopInstruction));

                LogicInstruction previous = program.get(index - 1);
                // Not exactly two instructions, or the previous instruction doesn't produce the tmp variable
                if (list.size() != 2 || list.get(0) != previous) continue;

                // Make sure all arg1 arguments of the other instruction are output
                boolean replacesOutputArg = previous.assignmentsStream()
                        .filter(t -> t.argument().equals(value))
                        .allMatch(ParameterAssignment::isOutput);
                if (!replacesOutputArg) continue;

                // The current instruction merely transfers a value from the output argument of the previous instruction
                // Replacing those arguments with target of the set instruction
                program.set(index - 1, replaceAllArgs(previous, value, ix.getTarget()));
                program.remove(index);
                index--;
            }
        }

        return false;
    }
}
