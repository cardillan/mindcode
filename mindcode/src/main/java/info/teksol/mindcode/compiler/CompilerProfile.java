package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Holds parameters pertaining to both Mindustry Compiler and Schematics Builder in one place.
 */
public class CompilerProfile {
    private final Map<Optimization, OptimizationLevel> levels;
    private ProcessorVersion processorVersion = ProcessorVersion.V7;
    private ProcessorEdition processorEdition = ProcessorEdition.WORLD_PROCESSOR;
    private GenerationGoal goal = GenerationGoal.SPEED;
    private MemoryModel memoryModel = MemoryModel.VOLATILE;
    private boolean shortCircuitEval = false;
    private FinalCodeOutput finalCodeOutput = null;
    private int parseTreeLevel = 0;
    private int debugLevel = 0;
    private boolean printStackTrace = false;

    // Schematics Builder

    private List<String> additionalTags = List.of();

    private CompilerProfile(OptimizationLevel level) {
        this.levels = Optimization.LIST.stream().collect(Collectors.toMap(o -> o, o -> level));
    }

    public CompilerProfile(Optimization... optimizations) {
        Set<Optimization> optimSet = Set.of(optimizations);
        this.levels = Optimization.LIST.stream().collect(Collectors.toMap(o -> o,
                o -> optimSet.contains(o) ? OptimizationLevel.AGGRESSIVE : OptimizationLevel.OFF));
    }

    public static CompilerProfile fullOptimizations() {
        return new CompilerProfile(OptimizationLevel.AGGRESSIVE);
    }

    public static CompilerProfile standardOptimizations() {
        return new CompilerProfile(OptimizationLevel.BASIC);
    }

    public static CompilerProfile noOptimizations() {
        return new CompilerProfile(OptimizationLevel.OFF);
    }

    public ProcessorVersion getProcessorVersion() {
        return processorVersion;
    }

    public ProcessorEdition getProcessorEdition() {
        return processorEdition;
    }

    public CompilerProfile setProcessorVersionEdition(ProcessorVersion processorVersion, ProcessorEdition processorEdition) {
        this.processorVersion = processorVersion;
        this.processorEdition = processorEdition;
        return this;
    }

    public CompilerProfile setProcessorEdition(ProcessorEdition processorEdition) {
        this.processorEdition = processorEdition;
        return this;
    }

    public CompilerProfile setProcessorVersion(ProcessorVersion processorVersion) {
        this.processorVersion = processorVersion;
        return this;
    }

    public OptimizationLevel getOptimizationLevel(Optimization optimization) {
        return levels.getOrDefault(optimization, OptimizationLevel.OFF);
    }

    public CompilerProfile setOptimizationLevel(Optimization optimization, OptimizationLevel level) {
        this.levels.put(optimization, level);
        return this;
    }

    public Map<Optimization, OptimizationLevel> getOptimizationLevels() {
        return Map.copyOf(levels);
    }

    public CompilerProfile setAllOptimizationLevels(OptimizationLevel level) {
        Optimization.LIST.forEach(o -> levels.put(o, level));
        return this;
    }

    public boolean optimizationsActive() {
        return levels.values().stream().anyMatch(l -> l != OptimizationLevel.OFF);
    }

    public GenerationGoal getGoal() {
        return goal;
    }

    public CompilerProfile setGoal(GenerationGoal goal) {
        this.goal = goal;
        return this;
    }

    public MemoryModel getMemoryModel() {
        return memoryModel;
    }

    public void setMemoryModel(MemoryModel memoryModel) {
        this.memoryModel = memoryModel;
    }

    public boolean isShortCircuitEval() {
        return shortCircuitEval;
    }

    public CompilerProfile setShortCircuitEval(boolean shortCircuitEval) {
        this.shortCircuitEval = shortCircuitEval;
        return this;
    }

    public void setFinalCodeOutput(FinalCodeOutput finalCodeOutput) {
        this.finalCodeOutput = finalCodeOutput;
    }

    public FinalCodeOutput getFinalCodeOutput() {
        return finalCodeOutput;
    }

    public int getParseTreeLevel() {
        return parseTreeLevel;
    }

    public CompilerProfile setParseTreeLevel(int parseTreeLevel) {
        this.parseTreeLevel = parseTreeLevel;
        return this;
    }

    public int getDebugLevel() {
        return debugLevel;
    }

    public CompilerProfile setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
        return this;
    }

    public boolean isPrintStackTrace() {
        return printStackTrace;
    }

    public CompilerProfile setPrintStackTrace(boolean printStackTrace) {
        this.printStackTrace = printStackTrace;
        return this;
    }

    public List<String> getAdditionalTags() {
        return additionalTags;
    }

    public CompilerProfile setAdditionalTags(List<String> additionalTags) {
        this.additionalTags = Objects.requireNonNull(additionalTags);
        return this;
    }
}
