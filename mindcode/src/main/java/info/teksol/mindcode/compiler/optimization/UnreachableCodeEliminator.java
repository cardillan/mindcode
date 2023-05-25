package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.CallInstruction;
import info.teksol.mindcode.compiler.instructions.CallRecInstruction;
import info.teksol.mindcode.compiler.instructions.EndInstruction;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.LabelInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.ReturnInstruction;
import info.teksol.mindcode.compiler.instructions.SetAddressInstruction;
import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicLabel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This optimizer removes instructions that are unreachable.
 * There are several ways unreachable instructions might appear:
 * <ol>
 * <li>Jump target propagation can get unreachable jumps that are no longer targeted</li>
 * <li>User-created unreachable regions, such as {@code while false ... end}</li>
 * <li>User defined functions which are called from an unreachable region</li>
 * </ol>
 * Instruction removal is done in loops until no instructions are removed. This way entire branches
 * of unreachable code (i.e. code inside the {@code while false ... end} statement) should be eliminated,
 * assuming the unconditional jump normalization optimizer was on the pipeline.
 * Labels - even inactive ones - are never removed.
 */
class UnreachableCodeEliminator extends BaseOptimizer {
    private Set<LogicLabel> activeLabels = new HashSet<>();

    public UnreachableCodeEliminator(InstructionProcessor instructionProcessor) {
        super(instructionProcessor);
    }

    @Override
    protected boolean optimizeProgram() {
        findActiveLabels();
        removeUnreachableInstructions();
        return true;
    }

    private void findActiveLabels() {
        activeLabels = instructionStream()
                .flatMap(this::extractLabelReference)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Stream<LogicLabel> extractLabelReference(LogicInstruction instruction) {
        return switch(instruction) {
            case JumpInstruction       ix -> Stream.of(ix.getTarget());
            case SetAddressInstruction ix -> Stream.of(ix.getLabel());
            case CallInstruction       ix -> Stream.of(ix.getCallAddr());
            case CallRecInstruction    ix -> ix.getAddresses().stream();
            default -> null;
        };
    }

    private void removeUnreachableInstructions() {
        boolean accessible = true;

        try (LogicIterator it = createIterator()) {
            while(it.hasNext()) {
                LogicInstruction instruction = it.next();
                if (accessible) {
                    // Unconditional jump makes the next instruction unreachable
                    if (instruction instanceof JumpInstruction ix && ix.getCondition() == Condition.ALWAYS) {
                        accessible = false;
                    } else if (instruction instanceof ReturnInstruction) {
                        accessible = false;
                    } else if (instruction instanceof EndInstruction) {
                        accessible = false;
                    }
                } else {
                    if (instruction instanceof LabelInstruction ix) {
                        // An active jump to here makes next instruction accessible
                        accessible = activeLabels.contains(ix.getLabel());
                    } else if (!(instruction instanceof EndInstruction) || aggressive()) {
                        // Remove unreachable
                        // Preserve end unless aggressive mode
                        it.remove();
                    }
                }
            }
        }
    }
}
