package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MessageLevel;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.PushOrPopInstruction;
import info.teksol.mindcode.logic.ArgumentType;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class DeadCodeEliminator extends BaseOptimizer {
    private final Set<LogicVariable> reads = new HashSet<>();
    private final Map<LogicVariable, List<LogicInstruction>> writes = new HashMap<>();
    private final Set<LogicVariable> eliminations = new HashSet<>();

    private Set<LogicVariable> unused;
    private Set<LogicVariable> uninitialized;

    DeadCodeEliminator(OptimizationContext optimizationContext) {
        super(Optimization.DEAD_CODE_ELIMINATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        analyzeDataflow();
        removeUselessWrites();

        if (unused == null || uninitialized == null) {
            unused = findUnused();
            uninitialized = findUninitialized();
        } else {
            unused.retainAll(findUnused());
            uninitialized.retainAll(findUninitialized());
        }

        return wasUpdated();
    }

    private Set<LogicVariable> findUnused() {
        return eliminations.stream()
                .filter(LogicVariable::isUserVariable)
                .collect(Collectors.toSet());
    }

    private Set<LogicVariable> findUninitialized() {
        return reads.stream()
                .filter(s -> s.getType() != ArgumentType.BLOCK && !writes.containsKey(s))
                .filter(LogicVariable::isGlobalVariable)        // Non-global variables are handled by Data Flow Optimization
                .collect(Collectors.toSet());
    }

    @Override
    public void generateFinalMessages() {
        super.generateFinalMessages();

        optimizationContext.addUninitializedVariables(uninitialized);
        
        if (unused.stream().anyMatch(v -> !v.isOptional())) {
            emitMessage(MessageLevel.WARNING, "       List of unused variables: %s.",
                    unused.stream().filter(v -> !v.isOptional())
                            .map(LogicVariable::getFullName).sorted().distinct().collect(Collectors.joining(", ")));
        }
        
        if (!uninitialized.isEmpty()) {
            emitMessage(MessageLevel.WARNING, "       List of uninitialized variables: %s.",
                    uninitialized.stream().map(LogicVariable::getFullName).sorted().distinct().collect(Collectors.joining(", ")));
        }
    }

    private void analyzeDataflow() {
        reads.clear();
        writes.clear();
        instructionStream().filter(ix -> !(ix instanceof PushOrPopInstruction)).forEach(this::examineInstruction);
    }

    private void removeUselessWrites() {
        final Set<LogicVariable> uselessWrites = new HashSet<>(writes.keySet());
        uselessWrites.removeAll(reads);
        for (LogicVariable key : uselessWrites) {
            // Preserve global and main variable assignments unless advanced
            if ((key.isGlobalVariable() || key.isMainVariable()) && !advanced()) continue;

            // Instruction with at most one output argument are removed immediately
            // Other instructions are inspected further to find out they're fully unused
            writes.get(key).stream()
                    .filter(LogicInstruction::isSafe)
                    .filter(ix -> ix.getOutputs() < 2 || allWritesUnread(ix))
                    .mapToInt(ix -> firstInstructionIndex(ixx -> ixx == ix))
                    .filter(i -> i >= 0)
                    .forEach(this::invalidateInstruction);
        }

        eliminations.addAll(uselessWrites);
    }


    
    /**
     * @param instruction instruction to examine
     * @return true if all output arguments of the instruction are unread
     */
    private boolean allWritesUnread(LogicInstruction instruction) {
        return instruction.outputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .noneMatch(reads::contains);
    }

    private void addWrite(LogicInstruction instruction, LogicVariable variable) {
        final List<LogicInstruction> ls = writes.getOrDefault(variable, new ArrayList<>());
        ls.add(instruction);
        writes.put(variable, ls);
    }

    private void examineInstruction(LogicInstruction instruction) {
        instruction.inputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .forEach(reads::add);

        if (instruction.getOpcode() != Opcode.CALL && instruction.getOpcode() != Opcode.CALLREC) {
            instruction.outputArgumentsStream()
                    .filter(LogicVariable.class::isInstance)
                    .map(LogicVariable.class::cast)
                    .filter(Predicate.not(LogicVariable::isCompilerVariable))
                    .forEach(v -> addWrite(instruction, v));
        }
    }
}
