package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import java.util.HashSet;
import java.util.Set;

/**
 * Eliminates push/pop instruction for variables that are not used anywhere else (after being eliminated
 * by other optimizers).
 */
public class StackUsageOptimizer extends GlobalOptimizer {
    private final Set<String> variables = new HashSet<>();

    StackUsageOptimizer(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected boolean optimizeProgram() {
        program.stream().filter(ix -> !ix.isPushOrPop()).forEach(this::collectVariables);
        program.removeIf(this::uselessStackOperation);
        return false;
    }

    private void collectVariables(LogicInstruction instruction) {
        variables.addAll(instructionProcessor.getInputOutputValues(instruction));
    }
    
    private boolean uselessStackOperation(LogicInstruction instruction) {
        return instruction.isPushOrPop() && !variables.contains(instruction.getArg(1));
    }
}
