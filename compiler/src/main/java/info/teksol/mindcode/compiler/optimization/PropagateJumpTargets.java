package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * If a jump (conditional or unconditional) targets an unconditional jump, the target of the first jump is redirected
 * to the target of the second jump, repeated until the end of jump chain is reached. Moreover:
 * <ul>
 * <li>end instruction is handled identically to jump 0 always,</li>
 * <li>conditional jumps in the jump chain are followed if:</li>
 *     <ul>
 *         <li>their condition is identical to the condition the first jump, and</li>
 *         <li>the condition arguments do not contain a volatile variable ({@code @time}, {@code @tick},
 *         {@code @counter} etc.)</li>
 *     </ul>
 * </ul>
 * No instructions are removed or added except possibly a label at the start of the program.
*/
class PropagateJumpTargets extends GlobalOptimizer {
    private static final LogicLabel FIRST_LABEL = LogicLabel.symbolic("__start__");
    private boolean startLabelUsed = false;
    
    public PropagateJumpTargets(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }
    
    @Override
    protected boolean optimizeProgram() {
        int count = 0;
        program.add(0, createLabel(FIRST_LABEL));
        for (int index = 0; index < program.size(); index++) {
            LogicInstruction instruction = program.get(index);
            if (instruction instanceof JumpInstruction ix) {
                LogicLabel label = findJumpRedirection(ix);
                if (!label.equals(ix.getTarget())) {
                    startLabelUsed |= label.equals(FIRST_LABEL);
                    // Update target of the original jump
                    program.set(index, ix.withLabel(label));
                    count++;
                }
            }
        }
        
        if (!startLabelUsed) {
            // Keep start label only if used -- easier on some unit tests
            program.remove(0);
        }

        if (count > 0) {
            emitMessage(MessageLevel.INFO, "%6d instructions updated by %s.", count, getClass().getSimpleName());
        }

        return false;
    }

    // Determines the final target of given jump
    private LogicLabel findJumpRedirection(JumpInstruction firstJump) {
        LogicLabel label = firstJump.getTarget();
        Set<LogicLabel> labels = new HashSet<>();       // Cycle detection
        labels.add(label);
        while (true) {
            LogicLabel redirected = evaluateJumpRedirection(firstJump, label);
            if (redirected == null || !labels.add(redirected)) {
                return label;
            }
            label = redirected;
        }
    }
    
    // Determines the jump redirection (one level only)
    private LogicLabel evaluateJumpRedirection(JumpInstruction firstJump, LogicLabel label) {
        int target = findInstructionIndex(0, in -> in instanceof LabelInstruction ix && ix.getLabel().equals(label));
        if (target < 0) {
            throw new OptimizationException("Could not find label " + label);
        }

        // Find next real instruction
        LogicInstruction next = findInstruction(target + 1, ix -> !(ix instanceof LabelInstruction));
        
        // Redirect compatible jumps
        if (next instanceof JumpInstruction ix && (ix.isUnconditional() || isIdenticalJump(firstJump, ix))) {
            return ix.getTarget();
        } 

        // Handle end instruction only in aggressive mode
        if (next instanceof EndInstruction && aggressive()) {
            return FIRST_LABEL;
        }

        // Not an unconditional or compatible jump: no redirection
        return null;
    }
    
    // Returns true if the next jump is semantically identical to the first jump
    private boolean isIdenticalJump(JumpInstruction firstJump, JumpInstruction nextJump) {
        List<LogicArgument> args1 = firstJump.getArgs();
        List<LogicArgument> args2 = nextJump.getArgs();
        
        // Compare everything but labels; exclude volatile variables
        return args1.subList(1, args1.size()).equals(args2.subList(1, args2.size()))
                && args1.stream().noneMatch(LogicArgument::isVolatile);
    }
}
