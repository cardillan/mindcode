package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.Iterator;
import java.util.List;

// Generic optimizer to remove all assignments to temporary variables that are only used as parameters
// in subsequent instruction. The set instruction is removed, while the other instruction is updated
// to replace the temp variable with the value used in the set statement.
// The optimization is performed only when the following conditions are met:
// 1. The set instruction assigns to a __tmp variable.
// 2. The __tmp variable is used in exactly one other instruction, *which is not a set to the same variable*
//    (this can happen when this optimizer is run without dead code removal)
// 3. The set instruction precedes the instruction using the __tmp variable (the check is based on absolute
//    instruction sequence in the program, not on the actual program flow).
// 
// An additional check could try to verify that the __tmp variable is passed into an input argument of the
// instruction. However, Mindcode compiler shouldn't generate code that would met all of the above conditions
// while using the __tmp variable as an output argument of the instruction.
class InputTempEliminator extends GlobalOptimizer {
    public InputTempEliminator(LogicInstructionPipeline next) {
        super(next);
    }

    @Override
    protected void optimizeProgram() {
        // Cannot uswe iterations due to modifications of the the underlying list in the loop
        for (Iterator<LogicInstruction> it = program.iterator(); it.hasNext(); ) {
            LogicInstruction instruction = it.next();
            if (!instruction.isSet()) continue;
            
            String arg0 = instruction.getArgs().get(0);
            // Not an assignment to a temp variable
            if (!arg0.startsWith(LogicInstructionGenerator.TMP_PREFIX)) continue;

            List<LogicInstruction> list = findInstructions(ix -> ix.getArgs().contains(arg0));
            // Not exactly two instructions, or this instruction does not come first
            if (list.size() != 2 || list.get(0) != instruction) continue;
            
            LogicInstruction other = list.get(1);
            // The other is also an op set to the same variable
            if (other.isSet() && other.getArgs().get(0).equals(arg0)) continue;
            
            // The first instruction merely transfers a value to the input argument of the other instruction
            // Replacing instruction argument by value
            String arg1 = instruction.getArgs().get(1);
            replaceInstruction(other, replaceAllArgs(other, arg0, arg1));
            it.remove();
        }
    }
}
