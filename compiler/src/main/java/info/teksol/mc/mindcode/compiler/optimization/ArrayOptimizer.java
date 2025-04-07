package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.ArrayOrganization;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@NullMarked
class ArrayOptimizer extends BaseOptimizer {
    private int invocations = 0;
    private int count = 0;

    public ArrayOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.ARRAY_OPTIMIZATION, optimizationContext);
    }

    @Override
    public void generateFinalMessages() {
        iterations = invocations;
        super.generateFinalMessages();
        if (count > 0) {
            emitMessage(MessageLevel.INFO, "%6d jump tables inlined by %s.", count, getName());
        }
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        try (OptimizationContext.LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                LogicInstruction instruction = iterator.next();
                if (instruction instanceof ArrayAccessInstruction ix && ix.getArrayOrganization() == ArrayOrganization.INTERNAL_REGULAR) {
                    ArrayOrganization organization = computeArrayOrganization(ix);
                    if (ix.getArrayOrganization() != organization) {
                        ArrayAccessInstruction copy = instructionProcessor.copy(ix);
                        copy.setArrayOrganization(organization);
                        iterator.set(copy);
                    }
                }
            }
        }

        return false;
    }

    private static @NotNull ArrayOrganization computeArrayOrganization(ArrayAccessInstruction ix) {
        ArrayOrganization current = ix.getArrayOrganization();
        return switch (ix.getArray().getArrayStore().getSize()) {
            case 1 -> ArrayOrganization.INTERNAL_SIZE1;
            case 2 -> ArrayOrganization.INTERNAL_SIZE2;
            default -> current;
        };
    }

    @Override
    public List<OptimizationAction> getPossibleOptimizations(int costLimit) {
        invocations++;

        Map<String, List<ArrayAccessInstruction>> jumpTables = instructionStream()
                .filter(ArrayAccessInstruction.class::isInstance)
                .map(ArrayAccessInstruction.class::cast)
                .filter(ix -> !ix.getJumpTableId().isEmpty())
                .collect(Collectors.groupingBy(ArrayAccessInstruction::getJumpTableId, TreeMap::new, Collectors.toList()));

        List<OptimizationAction> actions = new ArrayList<>();

        jumpTables.values().stream()
                .map(instructions -> findPossibleJumpTableInlining(instructions, costLimit))
                .filter(Objects::nonNull)
                .forEach(actions::add);

        jumpTables.values().stream()
                .flatMap(Collection::stream)
                .map(instruction -> findPossibleArrayAccessInlining(instruction, costLimit))
                .filter(Objects::nonNull)
                .forEach(actions::add);

        return actions;
    }

    private @Nullable OptimizationAction findPossibleJumpTableInlining(List<ArrayAccessInstruction> instructions, int costLimit) {
        int k = instructions.size();                            // Number of instructions
        int n = instructions.getFirst().getArray().getSize();   // Array size

        // Costs:
        int addedInstructions = k * (2 * n + 1);                // k times [ size of jump table - 1 + 2 ]
        int removedInstructions =
                2 * n + (getProfile().isSymbolicLabels() ? 1 : 0)   // Central jump table
                        + 4 * k;                                    // Call size
        int cost = addedInstructions - removedInstructions;

        // Benefit: 2 per call
        double benefit = instructions.stream().mapToDouble(ix -> ix.getAstContext().totalWeight()).sum() * 2;

        return cost <= costLimit ? new InlineJumpTableAction(instructions, cost, benefit) : null;
    }

    private @Nullable OptimizationAction findPossibleArrayAccessInlining(ArrayAccessInstruction instruction, int costLimit) {
        int n = instruction.getArray().getSize();   // Array size

        // Costs:
        int addedInstructions = 2 * n + 1;
        int removedInstructions = 4;
        int cost = addedInstructions - removedInstructions;

        // Benefit: 2 per call
        double benefit = instruction.getAstContext().totalWeight() * 2;

        return cost <= costLimit ? new InlineArrayAccessAction(instruction, cost, benefit) : null;
    }

    private class InlineJumpTableAction extends AbstractOptimizationAction {
        private final List<ArrayAccessInstruction> instructions;

        public InlineJumpTableAction(List<ArrayAccessInstruction> instructions, int cost, double benefit) {
            super(instructions.getFirst().getAstContext(), cost, benefit);
            this.instructions = instructions;
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            int[] indexes = instructions.stream()
                    .filter(ix -> ix.getArrayOrganization() == ArrayOrganization.INTERNAL_REGULAR)
                    .mapToInt(ArrayOptimizer.this::instructionIndex)
                    .filter(index -> index >= 0)
                    .toArray();

            if (indexes.length == instructions.size()) {
                for (int i = 0; i < indexes.length; i++) {
                    ArrayOrganization organization = instructions.get(i).getArray().getSize() == 3
                            ? ArrayOrganization.INTERNAL_SIZE3 : ArrayOrganization.INTERNAL_INLINED;
                    LogicInstruction copy = instructionProcessor.copy(instructions.get(i))
                            .setArrayOrganization(organization);
                    replaceInstruction(indexes[i], copy);
                }
                count += indexes.length;
                return OptimizationResult.REALIZED;
            } else {
                return OptimizationResult.INVALID;
            }
        }

        @Override
        public String toString() {
            return String.format("Inline %s jump table of array '%s'",
                    instructions.getFirst().getAccessType().toString().toLowerCase(),
                    instructions.getFirst().getArray().getArrayStore().getName().substring(1));
        }
    }

    private class InlineArrayAccessAction extends AbstractOptimizationAction {
        private final ArrayAccessInstruction instruction;

        public InlineArrayAccessAction(ArrayAccessInstruction instruction, int cost, double benefit) {
            super(instruction.getAstContext(), cost, benefit);
            this.instruction = instruction;
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            int index = instructionIndex(instruction);
            if (index >= 0 && instruction.getArrayOrganization() == ArrayOrganization.INTERNAL_REGULAR) {
                ArrayOrganization organization = instruction.getArray().getSize() == 3
                        ? ArrayOrganization.INTERNAL_SIZE3 : ArrayOrganization.INTERNAL_INLINED;
                LogicInstruction newInstruction = instructionProcessor.copy(instruction).setArrayOrganization(organization);
                replaceInstruction(index, newInstruction);
                count++;
                return OptimizationResult.REALIZED;
            } else {
                return OptimizationResult.INVALID;
            }
        }

        @Override
        public String toString() {
            assert astContext.node() != null;
            return String.format("Inline '%s' %s access at %s",
                    instruction.getArray().getArrayStore().getName().substring(1),
                    instruction.getAccessType().toString().toLowerCase(),
                    astContext.node().sourcePosition().formatForLog());
        }
    }
}
