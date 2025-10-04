package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.logic.arguments.arrays.ArrayConstructor.AccessType;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.ArrayConstruction;
import info.teksol.mc.mindcode.logic.instructions.ArrayOrganization;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
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
        if (!experimentalGlobal()) return false;

        try (OptimizationContext.LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                LogicInstruction instruction = iterator.next();
                if (instruction instanceof ArrayAccessInstruction ix) {
                    ArrayConstruction construction = computeArrayConstruction(ix);
                    ArrayOrganization organization = computeArrayOrganization(ix);
                    if (ix.getArrayConstruction() != construction || ix.getArrayOrganization() != organization) {
                        ArrayAccessInstruction copy = instructionProcessor.copy(ix);
                        copy.setArrayConstruction(construction).setArrayOrganization(organization);
                        iterator.set(copy);
                    }
                }
            }
        }

        return false;
    }

    private ArrayConstruction computeArrayConstruction(ArrayAccessInstruction ix) {
        return ix.getArray().getArrayStore().getSize() == 1 ||
               (ix.getArrayOrganization().isInlined() && !ix.isCompactAccessSource() && !ix.isCompactAccessTarget())
                ? ArrayConstruction.REGULAR
                : ix.getArrayConstruction();
    }

    private ArrayOrganization computeArrayOrganization(ArrayAccessInstruction ix) {
        ArrayOrganization current = ix.getArrayOrganization();

        return switch (ix.getArray().getArrayStore().getSize()) {
            case 1 -> ArrayOrganization.SIZE1;
            case 2 -> ArrayOrganization.SIZE2;
            case 3 -> ArrayOrganization.SIZE3;
            //case 3 -> experimental(ix) && instructionProcessor.isSupported(Opcode.SELECT) ? ArrayOrganization.SIZE3 : current;
            default -> current;
        };
    }

    @Override
    public List<OptimizationAction> getPossibleOptimizations(int costLimit) {
        invocations++;

        List<ArrayAccessInstruction> arrayAccesses = instructionStream()
                .filter(ArrayAccessInstruction.class::isInstance)
                .map(ArrayAccessInstruction.class::cast)
                .toList();

        // Excludes inlined jump tables
        Map<String, List<ArrayAccessInstruction>> jumpTables = arrayAccesses.stream()
                .filter(ix -> !ix.getJumpTableId().isEmpty())
                .collect(Collectors.groupingBy(ArrayAccessInstruction::getJumpTableId, TreeMap::new, Collectors.toList()));

        List<OptimizationAction> actions = new ArrayList<>();

        // All eligible out-of-line accesses are promoted at once
        // Promotion requires shared jump table(s), and once they're created, it's best to promote everything
        // Note: we might want to promote
        arrayAccesses.stream()
                .collect(Collectors.groupingBy(ix -> ix.getArray().getArrayStore().getName()))
                        .values().forEach(instructions -> findPossibleFullPromotions(instructions, actions, costLimit));

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

    private int b(boolean flag) {
        return flag ? 1 : 0;
    }

    private void findPossibleFullPromotions(List<ArrayAccessInstruction> instructions, List<OptimizationAction> actions,
            int costLimit) {
        ArrayStore arrayStore = instructions.getFirst().getArray().getArrayStore();
        List<ArrayAccessInstruction> compact = instructions.stream()
                .filter(ix -> ix.getArrayConstruction() == ArrayConstruction.COMPACT)
                .filter(ix -> !ix.isCompactAccessSource() && !ix.isCompactAccessTarget())
                .toList();
        List<ArrayAccessInstruction> fixed = instructions.stream()
                .filter(ix -> ix.getArrayConstruction() == ArrayConstruction.COMPACT)
                .filter(ix -> ix.isCompactAccessSource() || ix.isCompactAccessTarget())
                .toList();
        List<ArrayAccessInstruction> regular = instructions.stream().filter(ix -> ix.getArrayConstruction() == ArrayConstruction.REGULAR).toList();

        // Only process non-inlined compact representations
        if (compact.isEmpty()) return;

        boolean shared = arrayStore.getArrayType() == ArrayStore.ArrayType.REMOTE_SHARED;

        // Find how many read and write accesses would get promoted
        Map<AccessType, List<ArrayAccessInstruction>> byAccessType = compact.stream()
                .collect(Collectors.groupingBy(ArrayAccessInstruction::getAccessType));

        boolean needsReadTable = byAccessType.containsKey(AccessType.READ) && regular.stream().noneMatch(ix -> ix.getAccessType() == AccessType.READ);
        boolean needsWriteTable = byAccessType.containsKey(AccessType.WRITE) && regular.stream().noneMatch(ix -> ix.getAccessType() == AccessType.WRITE);

        int saved = shared ? 0 : 1;
        int callSize = b(getGlobalProfile().isSymbolicLabels());
        int droppedCompactTables = fixed.isEmpty() ? 1 : 0;
        int jumpTableSize = arrayStore.getSize() * 2 + callSize;
        int cost = (b(needsReadTable) + b(needsWriteTable) - droppedCompactTables) * jumpTableSize - compact.size() * saved;
        double benefit = instructions.stream().mapToDouble(ix -> ix.getAstContext().totalWeight()).sum() * saved;

        if (cost <= costLimit) {
            actions.add(new PromoteArrayAction(compact, cost, benefit));
        }
    }

    // Computes savings from inlining an instruction (disregarding inlined jump table size)
    // Is therefore also equal to the number of instruction executions avoided
    private int inlineSavings(ArrayAccessInstruction instruction) {
        int n = instruction.getArray().getSize();           // Array size

        // If the array is shared and the instruction is regular, we'll save one instruction
        int saveFromShared = b(instruction.getArray().getArrayStore().getArrayType() == ArrayStore.ArrayType.REMOTE_SHARED
                && instruction.getArrayConstruction() == ArrayConstruction.REGULAR);

        // Can promote compact to regular
        int saveFromCompact = b(instruction.getArrayConstruction() == ArrayConstruction.COMPACT
                && !instruction.isCompactAccessSource() && !instruction.isCompactAccessTarget());

        // Savings from inlining
        // Savings from shared
        // Savings from promoting compact to regular
        return 2 + saveFromShared + saveFromCompact;
    }

    // Inlines all instructions using the same out-of-line jump table, which is therefore removed
    // May be read, write or compact.
    private @Nullable OptimizationAction findPossibleJumpTableInlining(List<ArrayAccessInstruction> instructions, int costLimit) {
        int k = instructions.size();                            // Number of instructions
        int n = instructions.getFirst().getArray().getSize();   // Array size

        // Jump table costs
        int addedInstructions = k * (2 * n - 1);
        int removedInstructions = 2 * n;
        int cost = addedInstructions - removedInstructions;
        double benefit = 0;

        for (ArrayAccessInstruction instruction : instructions) {
            int savings = inlineSavings(instruction);
            cost -= savings;
            // 1/n: we also save the return when accessing the last element of the array
            benefit += instruction.getAstContext().totalWeight() * (1.0 / n + savings);
        }

        return cost <= costLimit ? new InlineJumpTableAction(instructions, cost, benefit) : null;
    }

    private @Nullable OptimizationAction findPossibleArrayAccessInlining(ArrayAccessInstruction instruction, int costLimit) {
        int n = instruction.getArray().getSize();   // Array size
        int savings = inlineSavings(instruction);
        int cost = 2 * n - 1 - savings;
        // 1/n: we also save the return when accessing the last element of the array
        double benefit = instruction.getAstContext().totalWeight() * (1.0 / n + savings);

        return cost <= costLimit ? new InlineArrayAccessAction(instruction, cost, benefit) : null;
    }

    private class PromoteArrayAction extends AbstractOptimizationAction {
        private final List<ArrayAccessInstruction> instructions;

        public PromoteArrayAction(List<ArrayAccessInstruction> instructions, int cost, double benefit) {
            super(instructions.getFirst().getAstContext(), cost, benefit);
            this.instructions = instructions;
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            return applyOptimization(() -> promoteArray(costLimit), toString());
        }

        private OptimizationResult promoteArray(int costLimit) {
            int[] indexes = instructions.stream()
                    .filter(ix -> ix.getArrayConstruction() == ArrayConstruction.COMPACT)
                    .mapToInt(ArrayOptimizer.this::instructionIndex)
                    .filter(index -> index >= 0)
                    .toArray();

            if (indexes.length == instructions.size()) {
                for (int i = 0; i < indexes.length; i++) {
                    LogicInstruction copy = instructionProcessor.copy(instructions.get(i))
                            .setArrayConstruction(ArrayConstruction.REGULAR);
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
            ArrayAccessInstruction instruction = instructions.getFirst();
            return String.format("Promote compact array '%s'",
                    instruction.getArray().getArrayStore().getName().substring(1));
        }
    }

    private void inlineInstruction(ArrayAccessInstruction instruction, int index) {
        LogicInstruction copy = instructionProcessor.copy(instruction).setArrayOrganization(ArrayOrganization.INLINED);
        replaceInstruction(index, copy);
    }

    private class InlineJumpTableAction extends AbstractOptimizationAction {
        private final List<ArrayAccessInstruction> instructions;

        public InlineJumpTableAction(List<ArrayAccessInstruction> instructions, int cost, double benefit) {
            super(instructions.getFirst().getAstContext(), cost, benefit);
            this.instructions = instructions;
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            return applyOptimization(() -> inlineJumpTable(costLimit), toString());
        }

        private OptimizationResult inlineJumpTable(int costLimit) {
            int[] indexes = instructions.stream()
                    .filter(ix -> ix.getArrayOrganization().canInline())
                    .mapToInt(ArrayOptimizer.this::instructionIndex)
                    .filter(index -> index >= 0)
                    .toArray();

            if (indexes.length == instructions.size()) {
                for (int i = 0; i < indexes.length; i++) {
                    inlineInstruction(instructions.get(i), indexes[i]);
                }
                count += indexes.length;
                return OptimizationResult.REALIZED;
            } else {
                return OptimizationResult.INVALID;
            }
        }

        @Override
        public String toString() {
            ArrayAccessInstruction instruction = instructions.getFirst();
            String tableType = instruction.getArrayConstruction() == ArrayConstruction.COMPACT ? "compact"
                    : instruction.getAccessType().toString().toLowerCase();

            return String.format("Inline %s jump table of array '%s'", tableType,
                    instruction.getArray().getArrayStore().getName().substring(1));
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
            return applyOptimization(() -> inlineArrayAccess(costLimit), toString());
        }

        private OptimizationResult inlineArrayAccess(int costLimit) {
            int index = instructionIndex(instruction);
            if (index >= 0 && instruction.getArrayOrganization().canInline()) {
                inlineInstruction(instruction, index);
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
