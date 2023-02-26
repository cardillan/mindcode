package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Removes instructions that became inaccessible.
// There are several ways to obrain inaccessible instructions:
// 1. Jump retargeting can create inaccesible jumps that are no longer targeted
// 2. User defined functions which are never called
// 3. User code like if false ... end or while false ... end
//
// Instruction removal is done in loops until no instructions are removed. This way entire branches
// of inacccessible code (ie. code inside the if false ... end statement) should be eliminated,
// assuming the unconditional jump normalization optimizer was on the pipeline.
// Labels - even inactive ones - are never removed.
class InaccesibleCodeEliminator extends GlobalOptimizer {
    private Set<String> activeLabels = new HashSet<>();

    public InaccesibleCodeEliminator(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected boolean optimizeProgram() {
        findActiveLabels();
        return removeInaccessibleInstructions();
    }
    
    private void findActiveLabels() {
        activeLabels = program.stream()
                .flatMap(this::extractLabelReference)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
    
    private Stream<String> extractLabelReference(LogicInstruction instruction) {
        if (instruction.isJump()) {
            return Stream.of(instruction.getArg(0));
        } else if (instruction.isSet()) {
            return Stream.of(instruction.getArg(1));
        } else if (instruction.isWrite()) {
            return Stream.of(instruction.getArg(1));
        } else if (instruction.isCall()) {
            return Stream.of(instruction.getArg(1), instruction.getArg(2));
        }
        
        return null;
    }
    
    private boolean removeInaccessibleInstructions() {
        boolean modified = false;
        boolean accessible = true;

        for (Iterator<LogicInstruction> it = program.iterator(); it.hasNext(); ) {
            LogicInstruction instruction = it.next();
            if (accessible) {
                // Unconditional jump makes the next instruction inaccessible
                if (instruction.isJump() && instruction.getArgs().get(1).equals("always")) {
                    accessible = false;
                } else if (instruction.isReturn()) {
                    accessible = false;
                } else if (instruction.isEnd()) {
                    accessible = false;
                }
            }
            else {
                if (instruction.isLabel()) {
                    // An active jump to here makes next instruction accessible
                    accessible = activeLabels.contains(instruction.getArgs().get(0));
                } else {
                    // Remove inaccessible
                    it.remove();
                    modified = true;
                }
            }
        }

        return modified;
    }

}
