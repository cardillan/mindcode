package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    public InaccesibleCodeEliminator(LogicInstructionPipeline next) {
        super(next);
    }

    @Override
    protected void optimizeProgram() {
        do {
            findActiveLabels();
        } while (removeInaccessibleInstructions());
    }
    
    private void findActiveLabels() {
        activeLabels = program.stream()
                .map(this::extractLabelReference)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
    
    private String extractLabelReference(LogicInstruction instruction) {
        if (instruction.isJump()) {
            return instruction.getArgs().get(0);
        } else if (instruction.isSet()) {
            return instruction.getArgs().get(1);
        } else if (instruction.isWrite()) {
            return instruction.getArgs().get(1);
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
