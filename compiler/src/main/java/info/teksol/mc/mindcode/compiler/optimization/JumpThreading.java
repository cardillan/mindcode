package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NullMarked
class JumpThreading extends BaseOptimizer {
    private static final LogicLabel FIRST_LABEL = LogicLabel.symbolic("__start__");
    private boolean startLabelUsed = false;
    int count = 0;
    
    public JumpThreading(OptimizationContext optimizationContext) {
        super(Optimization.JUMP_THREADING, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        try (LogicIterator it = createIterator()) {
            if (!it.hasNext()) return false;

            it.add(createLabel(instructionAt(0).getAstContext(), FIRST_LABEL));
            while (it.hasNext()) {
                LogicInstruction instruction = it.next();
                if (instruction instanceof JumpInstruction jump) {
                    // Target of the jump
                    LogicLabel label = findJumpRedirection(jump);
                    LogicInstruction target = labeledInstruction(label);
                    boolean replaceAdvanced = jump.isUnconditional() && (target instanceof ReturnInstruction
                            || target instanceof MultiJumpInstruction || target instanceof MultiCallInstruction);

                    if (replaceAdvanced) {
                        it.set(target.withContext(jump.getAstContext()));
                        count++;
                    } else if (!label.equals(jump.getTarget())){
                        startLabelUsed |= label.equals(FIRST_LABEL);
                        // Update target of the original jump
                        it.set(jump.withTarget(label));
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
    private @Nullable LogicLabel evaluateJumpRedirection(JumpInstruction firstJump, LogicLabel label) {
        int target = firstInstructionIndex(in -> in instanceof LabelInstruction ix && ix.getLabel().equals(label));
        if (target < 0) {
            throw new MindcodeInternalError("Could not find label " + label);
        }

        // Find next real instruction
        // If null, it means the jump leads to a label which doesn't have a valid instruction after
        LogicInstruction next = firstInstruction(target + 1, LogicInstruction::isReal);
        
        // Redirect compatible jumps
        if (next instanceof JumpInstruction ix && (ix.isUnconditional() || isIdenticalJump(firstJump, ix))) {
            return ix.getTarget();
        } 

        // Handle end instruction only in advanced mode
        if (next == null || (next instanceof EndInstruction)) {
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
