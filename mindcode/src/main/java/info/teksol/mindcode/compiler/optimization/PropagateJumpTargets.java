package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MessageLevel;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class PropagateJumpTargets extends BaseOptimizer {
    private static final LogicLabel FIRST_LABEL = LogicLabel.symbolic("__start__");
    private boolean startLabelUsed = false;
    int count = 0;
    
    public PropagateJumpTargets(OptimizationContext optimizationContext) {
        super(Optimization.JUMP_TARGET_PROPAGATION, optimizationContext);
    }
    
    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        try (LogicIterator it = createIterator()) {
            it.add(createLabel(instructionAt(0).getAstContext(), FIRST_LABEL));
            while (it.hasNext()) {
                LogicInstruction instruction = it.next();
                if (instruction instanceof JumpInstruction jump) {
                    LogicLabel label = findJumpRedirection(jump);
                    GotoInstruction labeledGoto = advanced() && labeledInstruction(label) instanceof GotoInstruction ix ? ix : null;
                    if (jump.isUnconditional() && labeledGoto != null || !label.equals(jump.getTarget())) {
                        if (labeledGoto != null) {
                            // An unconditional jump targets a goto: replace it with the goto itself
                            it.set(labeledGoto.withContext(jump.getAstContext()));
                        } else {
                            startLabelUsed |= label.equals(FIRST_LABEL);
                            // Update target of the original jump
                            it.set(jump.withTarget(label));
                        }
                        count++;
                    }
                }
            }
        }

        if (!startLabelUsed) {
            // Keep start label only if used -- easier on some unit tests
            removeInstruction(0);
        }

        return false;
    }

    @Override
    public void generateFinalMessages() {
        if (count > 0) {
            emitMessage(MessageLevel.INFO, "%6d instructions updated by %s.", count, getClass().getSimpleName());
        }
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
        int target = firstInstructionIndex(in -> in instanceof LabelInstruction ix && ix.getLabel().equals(label));
        if (target < 0) {
            throw new MindcodeInternalError("Could not find label " + label);
        }

        // Find next real instruction
        // If null, it means the jump leads to a label which doesn't have a valid instruction after
        LogicInstruction next = firstInstruction(target + 1, ix -> ix.getRealSize() > 0);
        
        // Redirect compatible jumps
        if (next instanceof JumpInstruction ix && (ix.isUnconditional() || isIdenticalJump(firstJump, ix))) {
            return ix.getTarget();
        } 

        // Handle end instruction only in advanced mode
        if (next == null || (next instanceof EndInstruction && advanced())) {
            return FIRST_LABEL;
        }

        // Not an unconditional or compatible jump: no redirection
        return null;
    }
    
    // Returns true if the next jump is semantically identical to the first jump
    private boolean isIdenticalJump(JumpInstruction firstJump, JumpInstruction nextJump) {
        if (advanced()) {
            List<LogicArgument> args1 = firstJump.getArgs();
            List<LogicArgument> args2 = nextJump.getArgs();

            // Compare everything but labels; exclude volatile variables
            return args1.subList(1, args1.size()).equals(args2.subList(1, args2.size()))
                    && args1.stream().noneMatch(LogicArgument::isVolatile);
        } else {
            return false;
        }
    }
}
