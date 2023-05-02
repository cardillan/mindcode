package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Optimizes passing return values to callers.
 * <p>
 * Function return values are carried by {@code __retval} variables instead of {@code __tmp} ones, because the original
 * variable  providing the return value -- {@code __fnXretval} -- might get overwritten during another function call
 * before the return value is used. Optimizations of regular temporary variables are not applied to {@code __retval}s.
 * <p>
 * This optimizer looks for a set instruction in the form {@code set __retvalX variable}. The {@code __retvalX}
 * is expected to be used by one other instruction. The optimizer removes the {@code set} instruction and replaces the
 * {@code __retvalX} by {@code variable} in the other instruction if the following conditions are met:
 * <ol>
 * <li>The {@code __retval} variable is used in exactly one other instruction, which follows the {@code set} instruction
 * (the check is based on absolute instruction sequence in the program, not on the actual program flow). Push and pop
 * instructions aren't considered.</li>
 * <li>The block of code between the {@code set} instruction and the other instruction is linear (doesn't contain jumps
 * into the code block from the outside -- function calls aren't considered). (Range iteration loop may produce such
 * code.)</li>
 * <li>The other variable is not modified in the code block.</li>
 * <li>If the variable is a {@code __fnXretval}: the code block must not contain any function calls - not just calls to
 * the {@code __fnX} function, but calls to any function - we don't know what may happen inside a function call. Call
 * graph might be inspected to see whether a particular function call indirectly calls the {@code __fnX} function.</li>
 * <li>If the variable is not a {@code __fnXretval}: the variable must not be volatile, and if it is global,
 * the code block must not contain any function calls.</li>
 * </ol>
 */
public class ReturnValueOptimizer extends BaseFunctionOptimizer {
    public ReturnValueOptimizer(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected boolean optimizeProgram() {
        List<SetInstruction> unneededSets = new ArrayList<>();

        for (int i = 0; i < program.size(); i++) {
            // Find fnRetVal creation and check is it well-formed
            if (program.get(i) instanceof SetInstruction instruction
                    && instruction.getTarget().getType() == ArgumentType.STORED_RETVAL) {

                LogicArgument retval = instruction.getTarget();
                LogicArgument value = instruction.getValue();

                // Get the other uses of fnRetVal
                List<LogicInstruction> list = findInstructions(ix -> ix.getArgs().contains(retval)
                        && !(ix instanceof PushOrPopInstruction));

                // Precondition 1
                if (list.size() != 2 || list.get(0) != instruction) continue;

                LogicInstruction other = list.get(1);

                // Precondition 2
                int endIndex = findInstructionIndex(i, ix -> ix == other);
                List<LogicInstruction> codeBlock = program.subList(i + 1, endIndex);
                if (!isLinear(codeBlock)) continue;

                // Precondition 3
                boolean isModified = codeBlock.stream()
                        .flatMap(LogicInstruction::outputArgumentsStream)
                        .anyMatch(value::equals);
                if (isModified) continue;

                boolean hasFunctionCall = codeBlock.stream().anyMatch(this::containsFunctionCall);
                if (value.getType() == ArgumentType.FUNCTION_RETVAL) {
                    // Precondition 4
                    if (hasFunctionCall) continue;
                } else {
                    // Precondition 5
                    if (hasFunctionCall && value.isGlobalVariable() || value.isVolatile()) continue;
                }

                removeInstruction(instruction);
                replaceInstruction(other, replaceAllArgs(other, retval, value));
                i--;
            }
        }

        return false;
    }

    private boolean containsFunctionCall(LogicInstruction ix) {
        // We're assuming any call might modify a fnRetVal
        // Need to identify all calls including stackless ones
        return ix instanceof CallRecInstruction || ix instanceof CallInstruction;
    }
}
