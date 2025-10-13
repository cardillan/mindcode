package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.InternalArray;
import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction.AccessType;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.mindcode.logic.opcodes.TypedArgument;
import info.teksol.mc.profile.BuiltinEvaluation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.logic.arguments.LogicKeyword.*;

@NullMarked
class ArrayOptimizer extends BaseOptimizer {
    public static final int MIN_LOOKUP_CAPACITY = 3;

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
        if (experimentalGlobal() && getGlobalProfile().getBuiltinEvaluation() != BuiltinEvaluation.NONE
            && getGlobalProfile().getTarget().atLeast(ProcessorVersion.V8A)) {
            selectLookupArrays();
        }

        try (OptimizationContext.LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                LogicInstruction instruction = iterator.next();
                if (instruction instanceof ArrayAccessInstruction ix) {
                    ArrayConstruction construction = computeArrayConstruction(ix);
                    ArrayOrganization organization = computeArrayOrganization(ix);
                    if (ix.getArrayConstruction() != construction || ix.getArrayOrganization() != organization) {
                        ArrayAccessInstruction copy = instructionProcessor.copy(ix);
                        copy.setArrayOrganization(organization, construction);
                        if (copy.getRealSize(null) <= ix.getRealSize(null)
                            && copy.getExecutionSteps() <= ix.getExecutionSteps()) {
                            iterator.set(copy);
                        }
                    }
                }
            }
        }

        return false;
    }

    private int lookupCapacity(LogicKeyword lookupType, Set<String> usedVariables) {
        boolean full = getGlobalProfile().getBuiltinEvaluation() == BuiltinEvaluation.FULL;
        Map<Integer, ? extends MindustryContent> lookupMap = Objects.requireNonNull(metadata.getLookupMap(lookupType.getKeyword()));
        int index = 0;
        while (index < lookupMap.size()) {
            MindustryContent content = lookupMap.get(index);
            if (content == null) break;
            if (content.name().contains("#")) break;
            if (!full && !metadata.isStableBuiltin(content.name())) break;
            if (usedVariables.contains(content.contentName())) break;
            index++;
        }

        return index;
    }

    private Map<LogicKeyword, Integer> inspectAvailableLookups() {
        // Create a set of all variables
        Set<String> usedVariables = instructionStream().flatMap(MlogInstruction::typedArgumentsStream)
                .map(TypedArgument::argument)
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .map(LogicVariable::toMlog)
                .collect(Collectors.toSet());

        Map<LogicKeyword, Integer> map = new HashMap<>();
        for (LogicKeyword l : List.of(BLOCK, UNIT, ITEM, LIQUID, TEAM)) {
            int capacity = lookupCapacity(l, usedVariables);
            if (capacity >= MIN_LOOKUP_CAPACITY) {
                map.put(l, capacity);
            }
        }
        return map;
    }

    private LogicKeyword findSmallestLookupType(Map<LogicKeyword, Integer> type) {
        return type.entrySet().stream()
                .min(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElseThrow();
    }

    private void selectLookupArrays() {
        List<ArrayAccessInstruction> arrayInstructions = instructionStream()
                .filter(ArrayAccessInstruction.class::isInstance)
                .map(ArrayAccessInstruction.class::cast)
                .filter(ix -> ix.getArrayOrganization().supportsLookup() && !ix.getArray().isDeclaredRemote())
                .toList();

        if (arrayInstructions.isEmpty()) return;

        Map<LogicKeyword, Integer> lookupCapacity = inspectAvailableLookups();
        if (lookupCapacity.isEmpty()) return;

        Map<String, List<ArrayAccessInstruction>> arrays = arrayInstructions.stream()
                .collect(Collectors.groupingBy(ix -> ix.getArray().getArrayStore().getName(),
                        HashMap::new, Collectors.toList()));

        // Remove arrays that have already been assigned a lookup type from both maps
        for (String arrayName : Set.copyOf(arrays.keySet())) {
            LogicKeyword lookupType = arrays.get(arrayName).getFirst().getArrayLookupType();
            if (lookupType != INVALID) {
                lookupCapacity.remove(lookupType);
                arrays.remove(arrayName);
            }
        }

        Map<String, Double> arrayWeights = arrays.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                e -> e.getValue().stream().mapToDouble(ix -> ix.getAstContext().totalWeight()).sum()));

        while (!lookupCapacity.isEmpty() && !arrays.isEmpty()) {
            // Starting with the smallest lookup type, try to find arrays that fit
            LogicKeyword lookupType = findSmallestLookupType(lookupCapacity);
            int capacity = lookupCapacity.remove(lookupType);

            String selected = arrays.entrySet().stream()
                    .filter(e -> e.getValue().getFirst().getArray().getArrayStore().getSize() <= capacity)
                    .max(Comparator.comparingInt(
                            (Map.Entry<String, List<ArrayAccessInstruction>> e) -> e.getValue().getFirst().getArray().getSize())
                            .thenComparingDouble(e -> arrayWeights.get(e.getKey())))
                    .map(Map.Entry::getKey)
                    .orElse(null);

            if (selected != null) {
                List<ArrayAccessInstruction> instructions = arrays.remove(selected);
                for (var ix : instructions) {
                    ArrayAccessInstruction copy = instructionProcessor.copy(ix)
                            .setArrayOrganization(ArrayOrganization.LOOKUP, ArrayConstruction.COMPACT)
                            .setArrayLookupType(lookupType);
                    replaceInstruction(instructionIndex(ix), copy);
                }
            }
        }
    }

    private LogicKeyword getArrayLookupType(ArrayAccessInstruction instruction) {
        if (instruction.getArray().getArrayStore() instanceof InternalArray array && array.getLookupType() != INVALID) {
            return array.getLookupType();
        } else {
            return instruction.getArrayLookupType();
        }
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
            case 1 -> ArrayOrganization.SINGLE;
            case 2, 3 -> ArrayOrganization.SHORT;
            case 4 -> instructionProcessor.isSupported(Opcode.SELECT) ? ArrayOrganization.SHORT : current;
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

        // Excludes inlined instructions, as these have no shared jump tables
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
                .filter(ix -> !ix.getArrayOrganization().isInlined() && ix.getArrayOrganization() != ArrayOrganization.LOOKUP)
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
        List<ArrayAccessInstruction> candidates = instructions.stream()
                .filter(ix -> ix.getArrayConstruction() == ArrayConstruction.COMPACT)
                .filter(ix -> !ix.isCompactAccessSource() && !ix.isCompactAccessTarget())
                .toList();
        List<ArrayAccessInstruction> compact = instructions.stream()
                .filter(ix -> ix.getArrayConstruction() == ArrayConstruction.COMPACT)
                .filter(ix -> ix.isCompactAccessSource() || ix.isCompactAccessTarget())
                .toList();
        List<ArrayAccessInstruction> regular = instructions.stream().filter(ix -> ix.getArrayConstruction() == ArrayConstruction.REGULAR).toList();

        // Only process non-inlined compact representations
        if (candidates.isEmpty()) return;

        // Find how many read and write accesses would get promoted
        Map<AccessType, List<ArrayAccessInstruction>> access = candidates.stream()
                .collect(Collectors.groupingBy(ArrayAccessInstruction::getAccessType));

        boolean needsReadTable = access.containsKey(AccessType.READ) && regular.stream().noneMatch(ix -> ix.getAccessType() == AccessType.READ);
        boolean needsWriteTable = access.containsKey(AccessType.WRITE) && regular.stream().noneMatch(ix -> ix.getAccessType() == AccessType.WRITE);

        int jumpTableSize = arrayStore.getSize() * 2 + b(getGlobalProfile().isSymbolicLabels());
        int jumpTableCost = jumpTableSize * (b(needsReadTable) + b(needsWriteTable) - b(compact.isEmpty()));

        OptimizationEffect effect = candidates.stream()
                .map(this::promotionEffect)
                .reduce(new OptimizationEffect(jumpTableCost), OptimizationEffect::sum);

        if (effect.cost() <= costLimit) {
            actions.add(new PromoteArrayAction(candidates, effect));
        }
    }

    // Inlines all instructions using the same out-of-line jump table, which is therefore removed
    // May be read, write or compact.
    private @Nullable OptimizationAction findPossibleJumpTableInlining(List<ArrayAccessInstruction> instructions, int costLimit) {
        int k = instructions.size();                            // Number of instructions
        int n = instructions.getFirst().getArray().getSize();   // Array size

        int removedInstructions = 2 * n;

        OptimizationEffect effect = instructions.stream()
                .filter(ix -> !ix.getArrayOrganization().isInlined() && ix.getArrayOrganization() != ArrayOrganization.LOOKUP)
                .map(this::inliningEffect)
                .reduce(new OptimizationEffect(-removedInstructions), OptimizationEffect::sum);

        return effect.cost() <= costLimit ? new InlineJumpTableAction(instructions, effect) : null;
    }

    private @Nullable OptimizationAction findPossibleArrayAccessInlining(ArrayAccessInstruction instruction, int costLimit) {
        OptimizationEffect effect = inliningEffect(instruction);
        return effect.cost() <= costLimit ? new InlineArrayAccessAction(instruction, effect) : null;
    }

    private class PromoteArrayAction extends AbstractOptimizationAction {
        private final List<ArrayAccessInstruction> instructions;

        public PromoteArrayAction(List<ArrayAccessInstruction> instructions, OptimizationEffect effect) {
            super(instructions.getFirst().getAstContext(), effect);
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

        public InlineJumpTableAction(List<ArrayAccessInstruction> instructions, OptimizationEffect effect) {
            super(instructions.getFirst().getAstContext(), effect);
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

        public InlineArrayAccessAction(ArrayAccessInstruction instruction, OptimizationEffect effect) {
            super(instruction.getAstContext(), effect);
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

    private OptimizationEffect promotionEffect(ArrayAccessInstruction original) {
        ArrayAccessInstruction optimized = instructionProcessor.copy(original);
        optimized.setArrayConstruction(ArrayConstruction.REGULAR);
        return fromComparison(original, optimized);
    }

    private OptimizationEffect inliningEffect(ArrayAccessInstruction original) {
        ArrayAccessInstruction optimized = instructionProcessor.copy(original);
        optimized.setArrayOrganization(ArrayOrganization.INLINED);
        return fromComparison(original, optimized);
    }

    private OptimizationEffect fromComparison(ArrayAccessInstruction original, ArrayAccessInstruction optimized) {
        int originalSize = original.getRealSize(null);
        double originalSteps = original.getExecutionSteps();

        int optimizedSize = optimized.getRealSize(null);
        double optimizedSteps = optimized.getExecutionSteps();

        double weight = original.getAstContext().totalWeight();

        return new OptimizationEffect(optimizedSize - originalSize, weight * (originalSteps - optimizedSteps));
    }
}
