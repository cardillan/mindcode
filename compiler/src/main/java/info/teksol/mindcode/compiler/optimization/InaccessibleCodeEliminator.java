package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This optimizer removes instructions that are inaccessible.
 * There are several ways inaccessible instructions might appear:
 * <ol>
 * <li>Jump target propagation can create inaccessible jumps that are no longer targeted</li>
 * <li>User-created inaccessible regions, such as {@code while false ... end}</li>
 * <li>User defined functions which are called from an inaccessible region</li>
 * </ol>
 * Instruction removal is done in loops until no instructions are removed. This way entire branches
 * of inaccessible code (i.e. code inside the {@code while false ... end} statement) should be eliminated,
 * assuming the unconditional jump normalization optimizer was on the pipeline.
 * Labels - even inactive ones - are never removed.
 */
class InaccessibleCodeEliminator extends GlobalOptimizer {
    private Set<String> activeLabels = new HashSet<>();

    public InaccessibleCodeEliminator(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected boolean optimizeProgram() {
        findActiveLabels();
        removeInaccessibleInstructions();
        return true;
    }

    private void findActiveLabels() {
        activeLabels = program.stream()
                .flatMap(this::extractLabelReference)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Stream<String> extractLabelReference(LogicInstruction instruction) {
        if (instruction.isJump()) {
            return Stream.of(instruction.asJump().getTarget());
        } else if (instruction.isSet()) {
            return Stream.of(instruction.asSet().getValue());
        } else if (instruction.isCall()) {
            return instruction.asCall().getAddresses().stream();
        }

        return null;
    }

    private void removeInaccessibleInstructions() {
        boolean accessible = true;

        for (Iterator<LogicInstruction> it = program.iterator(); it.hasNext(); ) {
            LogicInstruction instruction = it.next();
            if (accessible) {
                // Unconditional jump makes the next instruction inaccessible
                if (instruction.isJump() && instruction.asJump().getCondition().equals("always")) {
                    accessible = false;
                } else if (instruction.isReturn()) {
                    accessible = false;
                } else if (instruction.isEnd()) {
                    accessible = false;
                }
            } else {
                if (instruction.isLabel()) {
                    // An active jump to here makes next instruction accessible
                    accessible = activeLabels.contains(instruction.getArgs().get(0));
                } else if (!instruction.isEnd() || level == OptimizationLevel.AGGRESSIVE) {
                    // Remove inaccessible
                    // Preserve end unless aggressive mode
                    it.remove();
                }
            }
        }
    }
}
