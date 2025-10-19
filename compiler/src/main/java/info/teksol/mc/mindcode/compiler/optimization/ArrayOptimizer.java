package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.mindcode.logic.opcodes.TypedArgument;
import info.teksol.mc.profile.BuiltinEvaluation;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.logic.arguments.LogicKeyword.*;

@NullMarked
class ArrayOptimizer extends BaseOptimizer {
    public static final int MIN_LOOKUP_CAPACITY = 3;

    private final Set<String> updatedArrays = new HashSet<>();
    private int invocations = 0;

    public ArrayOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.ARRAY_OPTIMIZATION, optimizationContext);
    }

    @Override
    public void generateFinalMessages() {
        iterations = invocations;
        super.generateFinalMessages();
        if (!updatedArrays.isEmpty()) {
            emitMessage(MessageLevel.INFO, "%6d array(s) improved by %s.", updatedArrays.size(), getName());
        }
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        if (getGlobalProfile().useLookupArrays()
            && getGlobalProfile().getBuiltinEvaluation() != BuiltinEvaluation.NONE
            && getGlobalProfile().getTarget().atLeast(ProcessorVersion.V8A)) {
            selectLookupArrays();
        }

        try (OptimizationContext.LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                LogicInstruction instruction = iterator.next();
                if (instruction instanceof ArrayAccessInstruction ix && ix.getArrayOrganization() != ArrayOrganization.LOOKUP) {
                    ArrayConstruction construction = computeArrayConstruction(ix);
                    ArrayOrganization organization = computeArrayOrganization(ix);
                    if (ix.getArrayConstruction() != construction || ix.getArrayOrganization() != organization) {
                        ArrayAccessInstruction optimized = instructionProcessor.copy(ix);
                        optimized.setArrayOrganization(organization, construction);
                        OptimizationEffect effect = OptimizationEffect.fromComparison(ix, optimized);
                        if (effect.totalImprovement()) {
                            updatedArrays.add(ix.getArray().getArrayStore().getName());
                            iterator.set(optimized);
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
                .filter(ix -> ix.getArrayOrganization().supportsLookup() && !ix.getArray().isDeclaredRemote()
                              && ix.getArray().getArrayStore().getSize() >= MIN_LOOKUP_CAPACITY)
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

    private ArrayConstruction computeArrayConstruction(ArrayAccessInstruction ix) {
        return ix.getArray().getArrayStore().getSize() == 1 ||
               (ix.getArrayOrganization().isInlined() && !ix.isCompactAccessSource() && !ix.isCompactAccessTarget())
                ? ArrayConstruction.REGULAR
                : ix.getArrayConstruction();
    }

    private ArrayOrganization computeArrayOrganization(ArrayAccessInstruction ix) {
        ArrayOrganization current = ix.getArrayOrganization();
        boolean useShort = getGlobalProfile().useShortArrays();
        boolean hasSelect = instructionProcessor.isSupported(Opcode.SELECT);

        return switch (ix.getArray().getArrayStore().getSize()) {
            case 1 -> ArrayOrganization.SINGLE;
            case 2 -> useShort ? ArrayOrganization.SHORT : current;
            case 3 -> useShort && (hasSelect || !ix.getLocalProfile().useTextJumpTables()) ? ArrayOrganization.SHORT : current;
            case 4 -> useShort && hasSelect ? ArrayOrganization.SHORT : current;
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
                .collect(Collectors.groupingBy(ArrayAccessInstruction::getJumpTableId, TreeMap::new, Collectors.toList()));

        ArrayOptimization unfolding = new UnfoldingArrayOptimization();
        ArrayOptimization promoting = new PromotingArrayOptimization();
        ArrayOptimization inlining = new InliningArrayOptimization();
        ArrayOptimization inliningFolding = new InliningFoldingArrayOptimization();

        boolean canFold = instructionProcessor.isSupported(Opcode.SELECT);
        List<ArrayOptimization> allOptimizations = canFold ? List.of(unfolding, promoting, inlining, inliningFolding)
                : List.of(unfolding, promoting, inlining);
        List<ArrayOptimization> inliningOptimizations = canFold ? List.of(inlining, inliningFolding)
                : List.of(inlining);
        List<ArrayOptimization> inlinedOptimizations = List.of(unfolding, promoting);

        List<OptimizationAction> actions = new ArrayList<>();

        // Try to optimize the entire jump table at once
        allOptimizations.forEach(optimization ->
                jumpTables.entrySet().stream()
                        .filter(e -> !e.getKey().isEmpty())
                        .map(Map.Entry::getValue)
                        .forEach(instructions -> findPossibleOptimizations(instructions, optimization, actions, costLimit)));

        // Try inlining individual instructions
        // Only try three or more instructions per table - with less, full inlining is cheaper.
        // We do not consider shared jump tables, as we don't expect any savings
        inliningOptimizations.forEach(optimization ->
                jumpTables.entrySet().stream()
                    .filter(e -> !e.getKey().isEmpty() &&e.getValue().size() > 2)
                    .flatMap(e -> e.getValue().stream())
                    .filter(ix -> ix.getArrayOrganization().canInline())
                    .map(ix -> evaluateOptimization(ix, optimization))
                    .filter(plan -> plan.effect().improvement() && plan.effect().cost() <= costLimit)
                    .forEach(plan -> actions.add(new ArrayOptimizationAction(plan.instructions.getFirst().getAstContext(), plan))));

        // Try non-inlining optimizations of individual inlined instructions
        // We do not consider shared structures, as they're already inlined and do not share jump tables
        inlinedOptimizations.forEach(optimization ->
                arrayAccesses.stream()
                        .filter(ix -> ix.getArrayOrganization().isInlined())
                        .map(ix -> evaluateOptimization(ix, optimization))
                        .filter(plan -> plan.effect().improvement() && plan.effect().cost() <= costLimit)
                        .forEach(plan -> actions.add(new ArrayOptimizationAction(plan.instructions.getFirst().getAstContext(), plan)))
        );

        return actions;
    }

    private void findPossibleOptimizations(List<ArrayAccessInstruction> instructions,
            ArrayOptimization optimization, List<OptimizationAction> actions, int costLimit) {

        ArrayOptimizationPlan plan = evaluateOptimization(instructions, optimization);
        if (!plan.instructions.isEmpty() && plan.effect().improvement() && plan.effect().cost() <= costLimit) {
            AstContext context = plan.instructions.size() > 1 ? optimizationContext.getRootContext()
                    : plan.instructions.getFirst().getAstContext();
            actions.add(new ArrayOptimizationAction(context, plan));
        }
    }

    private class ArrayOptimizationAction extends AbstractOptimizationAction {
        private final ArrayOptimizationPlan optimizationPlan;

        public ArrayOptimizationAction(AstContext astContext, ArrayOptimizationPlan optimizationPlan) {
            super(astContext, optimizationPlan.effect());
            this.optimizationPlan = optimizationPlan;
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            return applyOptimization(() -> applyPlan(costLimit), toString());
        }

        private OptimizationResult applyPlan(int costLimit) {
            List<ArrayAccessInstruction> instructions = optimizationPlan.instructions;

            int[] indexes = instructions.stream()
                    .filter(optimizationPlan.optimization::canApply)
                    .mapToInt(ArrayOptimizer.this::instructionIndex)
                    .filter(index -> index >= 0)
                    .toArray();

            if (indexes.length == instructions.size()) {
                for (int i = 0; i < indexes.length; i++) {
                    LogicInstruction copy = optimizationPlan.optimization.apply(instructions.get(i));
                    replaceInstruction(indexes[i], copy);
                    updatedArrays.add(instructions.get(i).getArray().getArrayStore().getName());
                }
                return OptimizationResult.REALIZED;
            } else {
                return OptimizationResult.INVALID;
            }
        }

        @Override
        public String toString() {
            int count = optimizationPlan.instructions.size();
            ArrayAccessInstruction first = optimizationPlan.instructions.getFirst();
            return String.format("%s %s '%s'%s", optimizationPlan.optimization.name(),
                    count > 1 ? "shared table of array" : "array access to",
                    first.getArray().getArrayStore().getName().substring(1),
                    count > 1 ? "" : " at " + astContext.existingNode().sourcePosition().formatForLog()
            );
        }
    }

    private ArrayOptimizationPlan evaluateOptimization(ArrayAccessInstruction original, ArrayOptimization optimization) {
        if (optimization.canApply(original)) {
            ArrayAccessInstruction optimized = optimization.apply(original);
            OptimizationEffect effect = OptimizationEffect.fromComparison(original, optimized);
            return new ArrayOptimizationPlan(List.of(original), optimization, effect);
        } else {
            return new ArrayOptimizationPlan(List.of(), optimization, OptimizationEffect.NONE);
        }
    }

    private ArrayOptimizationPlan evaluateOptimization(List<ArrayAccessInstruction> instructions, ArrayOptimization optimization) {
        Map<String, Integer> originalStructures = new HashMap<>();
        Map<String, Integer> optimizedStructures = new HashMap<>();
        OptimizationEffect totalEffect = new OptimizationEffect();
        List<ArrayAccessInstruction> affectedInstructions = new ArrayList<>();

        for (ArrayAccessInstruction original : instructions) {
            if (optimization.canApply(original)) {
                ArrayAccessInstruction optimized = optimization.apply(original);
                OptimizationEffect effect = OptimizationEffect.fromComparison(original, optimized, originalStructures, optimizedStructures);
                totalEffect = totalEffect.add(effect);
                affectedInstructions.add(original);
            } else {
                // No change in effect
                original.getRealSize(originalStructures);
                original.getRealSize(optimizedStructures);
            }
        }

        // Don't bother inspecting shared structures if no optimization happens
        if (!affectedInstructions.isEmpty()) {
            int originalSharedSize = originalStructures.values().stream().mapToInt(Integer::intValue).sum();
            int optimizedSharedSize = optimizedStructures.values().stream().mapToInt(Integer::intValue).sum();
            totalEffect = totalEffect.add(optimizedSharedSize - originalSharedSize);
        }

        return new ArrayOptimizationPlan(affectedInstructions, optimization, totalEffect);
    }

    private interface ArrayOptimization {
        boolean canApply(ArrayAccessInstruction instruction);
        ArrayAccessInstruction apply(ArrayAccessInstruction instruction);
        String name();
    }

    private record ArrayOptimizationPlan(
            List<ArrayAccessInstruction> instructions,
            ArrayOptimization optimization,
            OptimizationEffect effect
    ) {}

    private class UnfoldingArrayOptimization implements ArrayOptimization {
        @Override
        public boolean canApply(ArrayAccessInstruction instruction) {
            return instruction.getArrayConstructor().folded();
        }

        @Override
        public ArrayAccessInstruction apply(ArrayAccessInstruction instruction) {
            return instructionProcessor.copy(instruction).setArrayFolded(false);
        }

        @Override
        public String name() {
            return "Unfold";
        }
    }

    private class PromotingArrayOptimization implements ArrayOptimization {
        @Override
        public boolean canApply(ArrayAccessInstruction instruction) {
            return instruction.getArrayConstruction().accessByName()
                   && !instruction.isCompactAccessSource() && !instruction.isCompactAccessTarget();
        }

        @Override
        public ArrayAccessInstruction apply(ArrayAccessInstruction instruction) {
            return instructionProcessor.copy(instruction).setArrayConstruction(ArrayConstruction.REGULAR);
        }

        @Override
        public String name() {
            return "Promote";
        }
    }

    private class InliningArrayOptimization implements ArrayOptimization {
        @Override
        public boolean canApply(ArrayAccessInstruction instruction) {
            return instruction.getArrayOrganization().canInline();
        }

        @Override
        public ArrayAccessInstruction apply(ArrayAccessInstruction instruction) {
            return instructionProcessor.copy(instruction).setArrayOrganization(instruction.getArrayOrganization().inline());
        }

        @Override
        public String name() {
            return "Inline";
        }
    }

    private class InliningFoldingArrayOptimization implements ArrayOptimization {
        @Override
        public boolean canApply(ArrayAccessInstruction instruction) {
            return instruction.getArrayOrganization().canInline() && instruction.getLocalProfile().useTextJumpTables()
                   && instruction.getArrayConstructor().canFold();
        }

        @Override
        public ArrayAccessInstruction apply(ArrayAccessInstruction instruction) {
            return instructionProcessor.copy(instruction).setArrayOrganization(instruction.getArrayOrganization().inline())
                    .setArrayFolded(true);
        }

        @Override
        public String name() {
            return "Inline/fold";
        }
    }
}
