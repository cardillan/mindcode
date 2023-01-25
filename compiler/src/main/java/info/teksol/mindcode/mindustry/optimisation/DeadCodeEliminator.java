package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class DeadCodeEliminator extends GlobalOptimizer {
    private static final Set<String> CONSTANT_NAMES = Set.of("true", "false", "null");
    private static final Set<String> BLOCK_NAMES = Set.of("arc", "bank", "battery", "cell", "center", "centrifuge",
            "compressor", "conduit", "container", "conveyor", "crucible", "cultivator", "cyclone", "diode", 
            "disassembler", "display", "distributor", "dome", "door", "drill", "driver", "duo", "extractor", "factory",
            "foreshadow", "foundation", "fuse", "gate", "generator", "hail", "incinerator", "junction", "kiln", "lancer",
            "meltdown", "melter", "mender", "message", "mine", "mixer", "node", "nucleus", "panel", "parallax", "point",
            "press", "processor", "projector", "pulverizer", "reactor", "reconstructor", "ripple", "router", "salvo",
            "scatter", "scorch", "segment", "separator", "shard", "smelter", "sorter", "spectre", "swarmer", "switch",
            "tank", "tower", "tsunami", "unloader", "vault", "wall", "wave", "weaver");

    private final Set<String> reads = new HashSet<>();
    private final Map<String, List<LogicInstruction>> writes = new HashMap<>();
    private final Set<String> eliminations = new HashSet<>();

    DeadCodeEliminator(LogicInstructionPipeline next) {
        super(next);
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
                .filter(s -> !s.startsWith(LogicInstructionGenerator.TMP_PREFIX))
                .sorted()
                .collect(Collectors.joining(", "));
        
        String uninitialized = reads.stream()
                .filter(s -> !writes.containsKey(s) && s.matches("[a-zA-Z].*") && 
                        !CONSTANT_NAMES.contains(s) && !isBlockName(s))
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
        return matcher.find() && BLOCK_NAMES.contains(matcher.group(1));
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
                    .filter(ix -> ix.getOpcode().getTotalOutputs() < 2 || allWritesUnread(ix))
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
        return instruction.getTypedArguments()
                .filter(t -> t.getArgumentType().isOutput())
                .noneMatch(t -> reads.contains(t.getValue()));
    }

    private void addWrite(LogicInstruction instruction, String varName) {
        final List<LogicInstruction> ls = writes.getOrDefault(varName, new ArrayList<>());
        ls.add(instruction);
        writes.put(varName, ls);
    }

    private void examineInstruction(LogicInstruction instruction) {
        reads.addAll(instruction.getOpcode().getInputValues(instruction.getArgs()));
        instruction.getOpcode().getOutputValues(instruction.getArgs()).forEach(v -> addWrite(instruction, v));
    }
}
