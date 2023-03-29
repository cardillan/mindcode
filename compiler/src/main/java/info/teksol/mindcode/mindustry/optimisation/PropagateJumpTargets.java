package info.teksol.mindcode.mindustry.optimisation;

import edu.emory.mathcs.backport.java.util.Collections;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// If a jump (conditional or unconditional) targets an unconditional jump, the target of the first jump is redirected
// to the target of the second jump, repeated until the end of jump chain is reached. Moreover:
// - end instruction is handled identically to jump 0 always
// - conditional jumps in the jump chain are followed if
//    (i) their condition is equal the the condition the first jump, and
//   (ii) the condition arguments do not contain a volatile variable (@time, @tick or @counter)
//
// Jump retargeting can move targets forward or backward over long distances; therefore the optimizer is global.
// No instructions are removed or added except a label at the start of the program.
public class PropagateJumpTargets extends GlobalOptimizer {
    private static final String FIRST_LABEL = "__start__";
    private static final Set<String> EXCLUDED_ARGS = Set.of("@time", "@tick", "@counter");
    private boolean startLabelUsed = false;
    
    public PropagateJumpTargets(LogicInstructionPipeline next) {
        super(next);
    }
    
    @Override
    protected void optimizeProgram() {
        program.add(0, new LogicInstruction("label", FIRST_LABEL));
        for (int index = 0; index < program.size(); index++) {
            LogicInstruction instruction = program.get(index);
            if (instruction.isJump()) {
                String label = findJumpRedirection(instruction);
                if (!label.equals(instruction.getArgs().get(0))) {
                    startLabelUsed |= label.equals(FIRST_LABEL);
                    // Update target of the original jump
                    program.set(index, replaceArg(instruction, 0, label));
                }
            }
        }
        
        if (!startLabelUsed) {
            // Keep start label only if used -- easier on some unit tests
            program.remove(0);
        }
    }

    // Determines the final target of given jump
    private String findJumpRedirection(LogicInstruction firstJump) {
        String label = firstJump.getArgs().get(0);
        Set<String> labels = new HashSet<>();       // Cycle detection
        labels.add(label);
        while (true) {
            String redirected = evaluateJumpRedirection(firstJump, label);
            if (redirected == null || !labels.add(redirected)) {
                return label;
            }
            label = redirected;
        }
    }
    
    // Determines the jump redirection (one level only)
    private String evaluateJumpRedirection(LogicInstruction firstJump, String label) {
        int target = findInstructionIndex(0, ix -> ix.isLabel() && ix.getArgs().get(0).equals(label));
        if (target < 0) {
            throw new IllegalStateException("Could not find label " + label);
        }

        // Find next real instruction
        LogicInstruction next = findInstruction(target + 1, ix -> !ix.isLabel());
        
        // Redirect compatible jumps
        if (next.isJump() && (next.getArgs().get(1).equals("always") || isIdenticalJump(firstJump, next))) {
            return next.getArgs().get(0);
        } 

        // end() is always compatible
        if (next.isEnd()) {
            return FIRST_LABEL;
        }

        // Not an unconditional or compatible jump: no redirection
        return null;
    }
    
    // Returns true if the next jump is semantically identical to the first jump
    private boolean isIdenticalJump(LogicInstruction firstJump, LogicInstruction nextJump) {
        List<String> args1 = firstJump.getArgs();
        List<String> args2 = nextJump.getArgs();
        
        // Compare everything but labels; exclude volatile variables
        return args1.subList(1, args1.size()).equals(args2.subList(1, args2.size())) &&
                Collections.disjoint(args1, EXCLUDED_ARGS);
    }
}
