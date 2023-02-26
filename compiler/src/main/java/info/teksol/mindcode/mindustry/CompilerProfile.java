package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.mindustry.logic.ProcessorEdition;
import info.teksol.mindcode.mindustry.logic.ProcessorVersion;
import info.teksol.mindcode.mindustry.optimisation.Optimisation;
import java.util.EnumSet;
import java.util.Set;

public class CompilerProfile {
    private ProcessorVersion processorVersion = ProcessorVersion.V7;
    private ProcessorEdition processorEdition = ProcessorEdition.WORLD_PROCESSOR;
    private Set<Optimisation> optimisations = EnumSet.allOf(Optimisation.class);
    private boolean printFinalCode = false;
    private int parseTreeLevel = 0;
    private int debugLevel = 0;

    private CompilerProfile(Set<Optimisation> optimisations) {
        this.optimisations = optimisations;
    }

    public CompilerProfile(Optimisation... optimisations) {
        this.optimisations = Set.of(optimisations);
    }

    public static CompilerProfile fullOptimizations() {
        return new CompilerProfile(EnumSet.allOf(Optimisation.class));
    }

    public static CompilerProfile noOptimizations() {
        return new CompilerProfile(EnumSet.noneOf(Optimisation.class));
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

    public Set<Optimisation> getOptimisations() {
        return optimisations;
    }

    public void setOptimisations(Set<Optimisation> optimisations) {
        this.optimisations = Set.copyOf(optimisations);
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
