package info.teksol.mindcode.compiler;

import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;
import info.teksol.mindcode.compiler.optimization.Optimization;
import java.util.EnumSet;
import java.util.Set;

public class CompilerProfile {
    private ProcessorVersion processorVersion = ProcessorVersion.V7;
    private ProcessorEdition processorEdition = ProcessorEdition.WORLD_PROCESSOR;
    private Set<Optimization> optimizations;
    private boolean printFinalCode = false;
    private int parseTreeLevel = 0;
    private int debugLevel = 0;

    private CompilerProfile(Set<Optimization> optimizations) {
        this.optimizations = optimizations;
    }

    public CompilerProfile(Optimization... optimizations) {
        this.optimizations = Set.of(optimizations);
    }

    public static CompilerProfile fullOptimizations() {
        return new CompilerProfile(EnumSet.allOf(Optimization.class));
    }

    public static CompilerProfile noOptimizations() {
        return new CompilerProfile(EnumSet.noneOf(Optimization.class));
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

    public Set<Optimization> getOptimizations() {
        return optimizations;
    }

    public void setOptimizations(Set<Optimization> optimizations) {
        this.optimizations = Set.copyOf(optimizations);
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
}
