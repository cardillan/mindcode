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
    private boolean shortCircuitEval = false;
    private boolean printFinalCode = false;
    private int parseTreeLevel = 0;
    private int debugLevel = 0;
    private boolean printStackTrace = true;

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

    public void setProcessorVersionEdition(ProcessorVersion processorVersion, ProcessorEdition processorEdition) {
        this.processorVersion = processorVersion;
        this.processorEdition = processorEdition;
    }

    public OptimizationLevel getOptimizationLevel(Optimization optimization) {
        return levels.getOrDefault(optimization, OptimizationLevel.OFF);
    }

    public void setOptimizationLevel(Optimization optimization, OptimizationLevel level) {
        this.levels.put(optimization, level);
    }

    public Map<Optimization, OptimizationLevel> getOptimizationLevels() {
        return Map.copyOf(levels);
    }

    public void setAllOptimizationLevels(OptimizationLevel level) {
        Optimization.LIST.forEach(o -> levels.put(o, level));
    }

    public boolean optimizationsActive() {
        return levels.values().stream().anyMatch(l -> l != OptimizationLevel.OFF);
    }

    public boolean isShortCircuitEval() {
        return shortCircuitEval;
    }

    public void setShortCircuitEval(boolean shortCircuitEval) {
        this.shortCircuitEval = shortCircuitEval;
    }

    public boolean isPrintFinalCode() {
        return printFinalCode;
    }

    public void setPrintFinalCode(boolean printFinalCode) {
        this.printFinalCode = printFinalCode;
    }

    public int getParseTreeLevel() {
        return parseTreeLevel;
    }

    public void setParseTreeLevel(int parseTreeLevel) {
        this.parseTreeLevel = parseTreeLevel;
    }

    public int getDebugLevel() {
        return debugLevel;
    }

    public void setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
    }

    public boolean isPrintStackTrace() {
        return printStackTrace;
    }

    public void setPrintStackTrace(boolean printStackTrace) {
        this.printStackTrace = printStackTrace;
    }

    public List<String> getAdditionalTags() {
        return additionalTags;
    }

    public void setAdditionalTags(List<String> additionalTags) {
        this.additionalTags = Objects.requireNonNull(additionalTags);
    }
}
