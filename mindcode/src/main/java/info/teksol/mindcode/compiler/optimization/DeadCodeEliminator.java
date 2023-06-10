package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.PushOrPopInstruction;
import info.teksol.mindcode.logic.ArgumentType;
import info.teksol.mindcode.logic.LogicVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class DeadCodeEliminator extends BaseOptimizer {
    private final Set<LogicVariable> reads = new HashSet<>();
    private final Map<LogicVariable, List<LogicInstruction>> writes = new HashMap<>();
    private final Set<LogicVariable> eliminations = new HashSet<>();

    DeadCodeEliminator(InstructionProcessor instructionProcessor) {
        super(instructionProcessor);
    }

    @Override
    protected boolean optimizeProgram() {
        analyzeDataflow();
        removeUselessWrites();
        return true;
    }

    @Override
    protected void generateFinalMessages() {
        super.generateFinalMessages();
        
        String eliminated = eliminations.stream()
                .filter(LogicVariable::isUserVariable)
                .map(LogicVariable::getFullName)
                .sorted()
                .distinct()
                .collect(Collectors.joining(", "));
        
        String uninitialized = reads.stream()
                .filter(s -> s.getType() != ArgumentType.BLOCK && !writes.containsKey(s))
                .filter(LogicVariable::isGlobalVariable)        // Non-global variables are handled by Data Flow Optimization
                .map(LogicVariable::getFullName)
                .sorted()
                .distinct()
                .collect(Collectors.joining(", "));
        
        if (!eliminated.isEmpty()) {
            emitMessage(MessageLevel.WARNING, "       List of unused variables: %s.", eliminated);
        }
        
        if (!uninitialized.isEmpty()) {
            emitMessage(MessageLevel.WARNING, "       List of uninitialized variables: %s.", uninitialized);
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
            // Preserve global and main variable assignments unless aggressive
            if ((key.isGlobalVariable() || key.isMainVariable()) && !aggressive()) continue;

            // Instruction with at most one output argument are removed immediately
            // Other instructions are inspected further to find out they're fully unused
            writes.get(key).stream()
                    .filter(ix -> ix.getOutputs() < 2 || allWritesUnread(ix))
                    .mapToInt(ix -> firstInstructionIndex(ixx -> ixx == ix))
                    .filter(i -> i >= 0)
                    .forEach(this::removeInstruction);
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

        instruction.outputArgumentsStream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .filter(Predicate.not(LogicVariable::isCompilerVariable))
                .forEach(v -> addWrite(instruction, v));
    }
}
