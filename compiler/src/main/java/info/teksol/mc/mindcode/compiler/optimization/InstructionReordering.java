package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.instructions.EmptyInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.stream.Collectors;

@NullMarked
class InstructionReordering extends AbstractConditionalOptimizer {

    public InstructionReordering(OptimizationContext optimizationContext) {
        super(Optimization.INSTRUCTION_REORDERING, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        optimizationContext.forEachContext(AstContext::isTopSafeContext, this::optimizeContext);
        return false;
    }

    private boolean optimizeContext(AstContext context) {
        if (context.getLocalProfile().getOptimizationLevel(this.optimization) == OptimizationLevel.NONE) return false;

        LogicList body = optimizationContext.contextInstructions(context);
        if (body.size() < 2) return false;

        // Create dependency map
        Map<LogicArgument, Integer> defines = new HashMap<>();
        Map<LogicInstruction, Integer> dependants = new IdentityHashMap<>();

        int index = 0;
        int lastLocalIndex = -1;
        int lastUnsafeIndex = -1;
        for (LogicInstruction instruction : body) {
            boolean nested = instruction.getAstContext().getSafeContext() != context;
            if (!nested) lastLocalIndex = index;
            int currentIndex = nested ? lastLocalIndex + 1 : index;

            // Updates dependants: finds instructions this instruction depends on,
            // and if these instructions don't have any dependants yet, add this instruction as a dependant
            instruction.getAllReads().map(defines::get)
                    .filter(Objects::nonNull)
                    .map(body::getExisting)
                    .forEach(ix -> {
                        System.out.println("Instruction " + ix + " anchors at " + instruction);
                        dependants.putIfAbsent(ix, currentIndex);
                    });

            // Unsafe instructions depend on the previous unsafe
            if (!instruction.isSafe()) {
                if (lastUnsafeIndex >= 0) dependants.putIfAbsent(body.getExisting(lastUnsafeIndex), currentIndex);
                lastUnsafeIndex = index;
            }

            // Mark variables that are defined by this instruction
            instruction.getAllWrites().forEach(arg -> defines.put(arg, currentIndex));

            index++;
        }

        if (lastLocalIndex < 0) return false;

        // Group instructions by dependants
        TreeMap<Integer, List<LogicInstruction>> groups = body.stream()
                .filter(ix -> safeToMove(ix, context))
                .collect(Collectors.groupingBy(ix -> dependants.getOrDefault(ix, Integer.MAX_VALUE),
                TreeMap::new, Collectors.toList()));

        System.out.println("\n\n\n");
        System.out.println("Optimizing context " + context.hierarchy());
        int start = instructionIndex(Objects.requireNonNull(body.getFirst()));
        int end = instructionIndex(Objects.requireNonNull(body.getLast()));
        output(context, start, end, "Before:");

        int lastMovableIndex = lastLocalIndex;
        Map<LogicInstruction, LogicInstruction> replacements = new IdentityHashMap<>();

        // Now process groups in descending order
        groups.reversed().forEach((anchor, list) -> {
            LogicInstruction anchorOriginal = anchor == Integer.MAX_VALUE
                    ? body.getExisting(lastMovableIndex) : body.getExisting(anchor);

            LogicInstruction anchorInstruction = replacements.getOrDefault(anchorOriginal, anchorOriginal);

            System.out.println("Processing anchor " + anchorInstruction + " (" + anchor + ")");

            // We're going to move the instructions right here
            int targetIndex = instructionIndex(anchorInstruction);

            for (int i = list.size() - 1; i >= 0; i--) {
                LogicInstruction ix = list.get(i);
                if (ix.getAstContext().getSafeContext() != context || ix instanceof EmptyInstruction) continue;

                int sourceIndex = instructionIndex(ix);
                if (sourceIndex != targetIndex - 1) {
                    LogicInstruction target = instructionAt(targetIndex);

                    //targetIndex;
                    System.out.println("  Moving " + ix + " from " + sourceIndex + " to " + targetIndex);
                    removeInstruction(sourceIndex);
                    LogicInstruction newIx = ix.withContext(target.getAstContext());
                    insertInstruction(targetIndex, newIx);
                    replacements.put(ix, newIx);

                    output(context, start, end, "Moved:");
                } else {
                    targetIndex = sourceIndex;
                }
            }
        });

        output(context, start, end, "After:");

        return true;
    }

    private boolean safeToMove(LogicInstruction instruction, AstContext context) {
        return switch (instruction.getOpcode()) {
            case NOOP -> false;
            case JUMP -> false;
            case END -> false;
            default -> instruction.getAstContext().getSafeContext() == context && instruction.getAstContext().isSafe();
        };
    }

    private void output(AstContext context, int start, int end, String message) {
        System.out.println();
        System.out.println(message);
        for (int index = start; index <= end; index++) {
            LogicInstruction ix = optimizationContext.instructionAt(index);
            System.out.printf("%s %3d: [%04d] [%04d] %s%n", safeToMove(ix, context) ? " safe " : "unsafe",
                instructionIndex(ix), ix.getAstContext().id, ix.getAstContext().getSafeContext().id, ix);
        }
        System.out.println();
    }
}
