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
    public static final int MAX_PASSES = 1000;
    public static final int MAX_INSTRUCTIONS = 100_000;
    public static final int MAX_INSTRUCTIONS_WEBAPP = 1500;
    public static final int DEFAULT_INSTRUCTIONS = 1000;
    public static final int DEFAULT_WEBAPP_PASSES = 3;
    public static final int DEFAULT_CMDLINE_PASSES = 25;
    public static final int DEFAULT_STEP_LIMIT_WEBAPP = 1_000_000;
    public static final int DEFAULT_STEP_LIMIT_CMDLINE = 10_000_000;

    private final Map<Optimization, OptimizationLevel> levels;
    private final boolean webApplication;
    private ProcessorVersion processorVersion = ProcessorVersion.V7;
    private ProcessorEdition processorEdition = ProcessorEdition.WORLD_PROCESSOR;
    private int instructionLimit = 1000;
    private int optimizationPasses = DEFAULT_WEBAPP_PASSES;
    private GenerationGoal goal = GenerationGoal.AUTO;
    private Remarks remarks = Remarks.PASSIVE;
    private MemoryModel memoryModel = MemoryModel.VOLATILE;
    private boolean shortCircuitEval = false;
    private FinalCodeOutput finalCodeOutput = null;
    private int parseTreeLevel = 0;
    private int debugLevel = 0;
    private boolean printStackTrace = false;

    private List<SortCategory> sortVariables = List.of();

    // Compile and run
    private boolean run = false;
    private int stepLimit = DEFAULT_STEP_LIMIT_WEBAPP;

    // Schematics Builder

    private List<String> additionalTags = List.of();

    public CompilerProfile(boolean webApplication, OptimizationLevel level) {
        this.webApplication = webApplication;
        this.stepLimit = webApplication ? DEFAULT_STEP_LIMIT_WEBAPP : DEFAULT_STEP_LIMIT_CMDLINE;
        this.levels = Optimization.LIST.stream().collect(Collectors.toMap(o -> o, o -> level));
    }

    public CompilerProfile(boolean webApplication, Optimization... optimizations) {
        this.webApplication = webApplication;
        this.stepLimit = webApplication ? DEFAULT_STEP_LIMIT_WEBAPP : DEFAULT_STEP_LIMIT_CMDLINE;
        Set<Optimization> optimSet = Set.of(optimizations);
        this.levels = Optimization.LIST.stream().collect(Collectors.toMap(o -> o,
                o -> optimSet.contains(o) ? OptimizationLevel.ADVANCED : OptimizationLevel.NONE));
    }

    public static CompilerProfile fullOptimizations(boolean webApplication) {
        return new CompilerProfile(webApplication, OptimizationLevel.ADVANCED);
    }

    public static CompilerProfile standardOptimizations(boolean webApplication) {
        return new CompilerProfile(webApplication, OptimizationLevel.BASIC);
    }

    public static CompilerProfile noOptimizations(boolean webApplication) {
        return new CompilerProfile(webApplication, OptimizationLevel.NONE);
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
        return levels.getOrDefault(optimization, OptimizationLevel.NONE);
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
        return levels.values().stream().anyMatch(l -> l != OptimizationLevel.NONE);
    }

    public int getInstructionLimit() {
        return instructionLimit;
    }

    public CompilerProfile setInstructionLimit(int instructionLimit) {
        int max = webApplication ? MAX_INSTRUCTIONS_WEBAPP : MAX_INSTRUCTIONS;
        this.instructionLimit = Math.min(max, instructionLimit);
        return this;
    }

    public int getOptimizationPasses() {
        return optimizationPasses;
    }

    public CompilerProfile setOptimizationPasses(int optimizationPasses) {
        this.optimizationPasses = optimizationPasses;
        return this;
    }

    public GenerationGoal getGoal() {
        return goal;
    }

    public CompilerProfile setGoal(GenerationGoal goal) {
        this.goal = goal;
        return this;
    }

    public Remarks getRemarks() {
        return remarks;
    }

    public CompilerProfile setRemarks(Remarks remarks) {
        this.remarks = remarks;
        return this;
    }

    public MemoryModel getMemoryModel() {
        return memoryModel;
    }

    public CompilerProfile setMemoryModel(MemoryModel memoryModel) {
        this.memoryModel = memoryModel;
        return this;
    }

    public boolean isShortCircuitEval() {
        return shortCircuitEval;
    }

    public CompilerProfile setShortCircuitEval(boolean shortCircuitEval) {
        this.shortCircuitEval = shortCircuitEval;
        return this;
    }

    public CompilerProfile setFinalCodeOutput(FinalCodeOutput finalCodeOutput) {
        this.finalCodeOutput = finalCodeOutput;
        return this;
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

    public List<SortCategory> getSortVariables() {
        return sortVariables;
    }

    public CompilerProfile setSortVariables(List<SortCategory> sortVariables) {
        this.sortVariables = sortVariables;
        return this;
    }

    public List<String> getAdditionalTags() {
        return additionalTags;
    }

    public CompilerProfile setAdditionalTags(List<String> additionalTags) {
        this.additionalTags = Objects.requireNonNull(additionalTags);
        return this;
    }

    public boolean isRun() {
        return run;
    }

    public CompilerProfile setRun(boolean run) {
        this.run = run;
        return this;
    }

    public int getStepLimit() {
        return stepLimit;
    }

    public CompilerProfile setStepLimit(int stepLimit) {
        this.stepLimit = stepLimit;
        return this;
    }
}
