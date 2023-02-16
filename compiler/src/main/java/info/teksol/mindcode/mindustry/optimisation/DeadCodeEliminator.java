package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        return removeUselessWrites();
    }

    @Override
    protected void generateFinalMessages() {
        super.generateFinalMessages();
        
        String eliminated = eliminations.stream()
                .filter(s -> !s.startsWith(instructionProcessor.getTempPrefix()))
                .sorted()
                .collect(Collectors.joining(", "));
        
        String uninitialized = reads.stream()
                .filter(s -> !writes.containsKey(s) && s.matches("[a-zA-Z].*") && 
                        !instructionProcessor.getConstantNames().contains(s) && !isBlockName(s))
                .sorted()
                .collect(Collectors.joining(", "));
        
        if (!eliminated.isEmpty()) {
            emitMessage("       list of unused variables: %s.", eliminated);
        }
        
        if (!uninitialized.isEmpty()) {
            emitMessage("       list of uninitialized variables: %s.", uninitialized);
        }
    }
    
    private static final Pattern BLOCK_NAME_PATTERN = Pattern.compile("^([a-zA-Z][a-zA-Z_]*)[1-9]\\d*$");
    
    private boolean isBlockName(String name) {
        Matcher matcher = BLOCK_NAME_PATTERN.matcher(name);
        return matcher.find() && instructionProcessor.getBlockNames().contains(matcher.group(1));
    }
    
    private void analyzeDataflow() {
        reads.clear();
        writes.clear();
        reads.add("@counter"); // instruction pointer is *always* read -- and our implementation of call/return depends on this
        program.forEach(this::examineInstruction);
    }

    /**
     * @return true if we need to do another round of dataflow analysis.
     */
    private boolean removeUselessWrites() {
        int initialSize = program.size();
        final Set<String> uselessWrites = new HashSet<>(writes.keySet());
        uselessWrites.removeAll(reads);
        for (String key : uselessWrites) {
            // Instruction with at most one output argument are removed immediatelly
            // Other instructions are inspected further to find out they're fully unused
            writes.get(key).stream()
                    .filter(ix -> instructionProcessor.getTotalOutputs(ix) < 2 || allWritesUnread(ix))
                    .forEach(program::remove);
        }

        eliminations.addAll(uselessWrites);
        return program.size() < initialSize;
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
