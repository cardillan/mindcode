package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.Tuple2;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import java.util.*;
import java.util.stream.Collectors;

class DeadCodeEliminator extends GlobalOptimizer {
    private final Set<String> reads = new HashSet<>();
    private final Map<String, List<LogicInstruction>> writes = new HashMap<>();
    private final Set<String> eliminations = new HashSet<>();

    DeadCodeEliminator(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
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
                .filter(s -> !isTemporary(s)
                        && !s.startsWith(instructionProcessor.getRetValPrefix())
                        && !isFunctionReturnVariable(s))
                .map(this::resolveVariableName)
                .sorted()
                .distinct()
                .collect(Collectors.joining(", "));
        
        String uninitialized = reads.stream()
                .filter(s -> !writes.containsKey(s)
                        && (s.matches("(__fn\\d+_|[a-zA-Z].*)") || isLocalVariable(s))
                        && !instructionProcessor.getConstantNames().contains(s)
                        && !instructionProcessor.isBlockName(s))
                .map(this::resolveVariableName)
                .sorted()
                .distinct()
                .collect(Collectors.joining(", "));
        
        if (!eliminated.isEmpty()) {
            emitMessage(MessageLevel.WARNING, "       list of unused variables: %s.", eliminated);
        }
        
        if (!uninitialized.isEmpty()) {
            emitMessage(MessageLevel.WARNING, "       list of uninitialized variables: %s.", uninitialized);
        }
    }

    private boolean isLocalVariable(String id) {
        return id.matches(instructionProcessor.getLocalPrefix() + "\\d+_.*");
    }

    private boolean isFunctionReturnVariable(String id) {
        return id.matches(instructionProcessor.getLocalPrefix() + "\\d+retval");
    }

    private String resolveVariableName(String id) {
        Tuple2<String, String> parsed = instructionProcessor.parseVariable(id);
        return parsed.getT1() == null ? id : parsed.getT1() + "." + parsed.getT2();
    }

    private void analyzeDataflow() {
        reads.clear();
        writes.clear();
        // Instruction pointer is implicitly read - writes to it must be preserved
        reads.add("@counter"); 
        // Same for stack pointer: the push/pop/call/return instructions read __sp implicitly. 
        // The initial write to __sp must be preserved
        reads.add(instructionProcessor.getStackPointer());
        program.stream().filter(ix -> !ix.isPushOrPop()).forEach(this::examineInstruction);
    }

    private void removeUselessWrites() {
        final Set<String> uselessWrites = new HashSet<>(writes.keySet());
        uselessWrites.removeAll(reads);
        for (String key : uselessWrites) {
            // Instruction with at most one output argument are removed immediately
            // Other instructions are inspected further to find out they're fully unused
            writes.get(key).stream()
                    .filter(ix -> instructionProcessor.getTotalOutputs(ix) < 2 || allWritesUnread(ix))
                    .forEach(program::remove);
        }

        eliminations.addAll(uselessWrites);
    }
    
    /**
     * @param instruction instruction to examine
     * @return true if all output arguments of the instruction are unread
     */
    private boolean allWritesUnread(LogicInstruction instruction) {
        return instructionProcessor.getTypedArguments(instruction)
                .filter(t -> t.getArgumentType().isOutput())
                .noneMatch(t -> reads.contains(t.getValue()));
    }

    private void addWrite(LogicInstruction instruction, String varName) {
        final List<LogicInstruction> ls = writes.getOrDefault(varName, new ArrayList<>());
        ls.add(instruction);
        writes.put(varName, ls);
    }

    private void examineInstruction(LogicInstruction instruction) {
        reads.addAll(instructionProcessor.getInputValues(instruction));
        instructionProcessor.getOutputValues(instruction).forEach(v -> addWrite(instruction, v));
    }
}
